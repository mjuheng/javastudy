package fzhx;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过 javaagent 注入 MyBatis 执行器，打印还原参数后的 SQL 和耗时。
 *
 * @author 36020
 */
public final class MyBatisSqlLogAgent {
    private static final String BASE_EXECUTOR_CLASS = "org/apache/ibatis/executor/BaseExecutor";
    private static final String LOGGER_NAME = "MyBatisSqlLog";
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\?");
    private static final long SLOW_SQL_MS = 1000L;
    private static final ThreadLocal<Deque<Long>> SQL_START_TIME = ThreadLocal.withInitial(ArrayDeque::new);
    public static final String ORANGE = "\u001B[38;2;255;165;0m";
    public static final String RESET = "\u001B[0m";

    private MyBatisSqlLogAgent() {
    }

    public static void register(Instrumentation instrumentation) {
        // 只增强 MyBatis BaseExecutor，避免对业务 mapper 或其他框架类造成额外侵入。
        instrumentation.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            // 类名不匹配时直接跳过，让 JVM 原样加载当前类。
            if (!BASE_EXECUTOR_CLASS.equals(className)) {
                return null;
            }

            CtClass targetClass = null;
            try {
                // 使用目标类自己的 ClassLoader 构建 ClassPool，确保能解析 MyBatis 相关类型。
                ClassPool classPool = new ClassPool(true);
                if (loader != null) {
                    classPool.insertClassPath(new LoaderClassPath(loader));
                }
                targetClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

                // 分别增强查询和更新入口，命中数为 0 时主动报错，方便启动时发现版本不兼容。
                int enhancedMethodCount = 0;
                // BaseExecutor 的 4 参数 query 会转调 6 参数 query，只注入 6 参数方法避免重复日志。
                enhancedMethodCount += enhanceMethod(classPool, targetClass, "query",
                        "(Lorg/apache/ibatis/mapping/MappedStatement;Ljava/lang/Object;Lorg/apache/ibatis/session/RowBounds;Lorg/apache/ibatis/session/ResultHandler;Lorg/apache/ibatis/cache/CacheKey;Lorg/apache/ibatis/mapping/BoundSql;)Ljava/util/List;");
                enhancedMethodCount += enhanceMethod(classPool, targetClass, "update",
                        "(Lorg/apache/ibatis/mapping/MappedStatement;Ljava/lang/Object;)I");

                if (enhancedMethodCount == 0) {
                    throw new IllegalStateException("未找到需要增强的 MyBatis 执行方法");
                }
                info("已增强 " + className.replace('/', '.') + "，命中方法数：" + enhancedMethodCount);
                return targetClass.toBytecode();
            } catch (Throwable e) {
                error("增强 MyBatis SQL 日志失败：" + className, e);
                return null;
            } finally {
                if (targetClass != null) {
                    targetClass.detach();
                }
            }
        });
    }

    private static int enhanceMethod(ClassPool classPool, CtClass targetClass, String methodName, String signature)
            throws Exception {
        for (CtMethod method : targetClass.getDeclaredMethods(methodName)) {
            // 通过 JVM 方法签名精确匹配目标重载，避免增强到同名的其他方法。
            if (signature.equals(method.getSignature())) {
                // 执行前把开始时间压入线程栈，避免 Javassist catch 块访问不到局部变量。
                method.insertBefore("{ fzhx.MyBatisSqlLogAgent.startSqlLog(); }");
                // 正常返回时打印 SQL；异常路径交给下面的 catch 注入处理。
                method.insertAfter(
                        "{ fzhx.MyBatisSqlLogAgent.logSql($1, $args, null); }",
                        false
                );
                // 目标 SQL 执行异常时先打印完整 SQL 和原始异常，再把异常原样抛回业务调用链。
                method.addCatch(
                        "{ fzhx.MyBatisSqlLogAgent.logSql($1, $args, $e); throw $e; }",
                        classPool.get(Throwable.class.getName())
                );
                return 1;
            }
        }
        return 0;
    }

    public static void startSqlLog() {
        // 使用栈结构兼容同一线程内 SQL 嵌套执行的情况。
        SQL_START_TIME.get().push(System.currentTimeMillis());
    }

    public static void logSql(Object mappedStatement, Object[] args, Throwable throwable) {
        long duration = getDuration();
        try {
            // 通过反射读取 mapperId 和 BoundSql，避免 agent 编译期直接依赖 MyBatis。
            String mapperId = String.valueOf(invoke(mappedStatement, "getId"));
            String sql = resolveSql(mappedStatement, args);
            // 按执行结果选择日志级别：异常输出 error，慢 SQL 输出 warn，普通 SQL 输出 info。
            if (throwable != null) {
                error(mapperId + " execute sql error: " + sql + " (" + duration + " ms)", throwable);
            } else if (duration >= SLOW_SQL_MS) {
                warn(mapperId + " execute sql took more than " + SLOW_SQL_MS + " ms: " + sql + " (" + duration + " ms)");
            } else {
                info(mapperId + " execute sql: " + sql + " (" + duration + " ms)");
            }
        } catch (Throwable e) {
            error("打印 MyBatis SQL 日志失败", e);
        }
    }

    private static long getDuration() {
        Deque<Long> startTimeStack = SQL_START_TIME.get();
        Long startTime = startTimeStack.poll();
        if (startTimeStack.isEmpty()) {
            SQL_START_TIME.remove();
        }
        return startTime == null ? 0L : System.currentTimeMillis() - startTime;
    }
    private static String resolveSql(Object mappedStatement, Object[] args) throws Exception {
        // query 的 6 参数方法会直接传入 BoundSql；update 方法没有 BoundSql 时从 MappedStatement 现取。
        Object parameterObject = args != null && args.length > 1 ? args[1] : null;
        Object boundSql = args != null && args.length > 5 && args[5] != null
                ? args[5]
                : invoke(mappedStatement, "getBoundSql", new Class<?>[]{Object.class}, new Object[]{parameterObject});
        Object configuration = invoke(mappedStatement, "getConfiguration");
        return replaceSqlParameters(configuration, boundSql, parameterObject);
    }

    private static String replaceSqlParameters(Object configuration, Object boundSql, Object fallbackParameterObject)
            throws Exception {
        // 先压缩 SQL 模板空白，再按 MyBatis 参数映射顺序逐个替换问号。
        String sql = String.valueOf(invoke(boundSql, "getSql")).replaceAll("[\\s]+", " ").trim();
        Object parameterObject = invoke(boundSql, "getParameterObject");
        if (parameterObject == null) {
            parameterObject = fallbackParameterObject;
        }

        Object parameterMappingsObject = invoke(boundSql, "getParameterMappings");
        if (!(parameterMappingsObject instanceof List<?>) || ((List<?>) parameterMappingsObject).isEmpty()
                || parameterObject == null) {
            return sql;
        }

        List<?> parameterMappings = (List<?>) parameterMappingsObject;
        Object typeHandlerRegistry = invoke(configuration, "getTypeHandlerRegistry");
        // 基础类型、字符串、日期等存在直接 TypeHandler 的参数，可以按单值参数处理。
        Boolean hasDirectTypeHandler = (Boolean) invoke(typeHandlerRegistry, "hasTypeHandler",
                new Class<?>[]{Class.class}, new Object[]{parameterObject.getClass()});
        if (Boolean.TRUE.equals(hasDirectTypeHandler)) {
            return replaceDirectParameters(sql, parameterMappings, parameterObject);
        }
        return replaceObjectParameters(configuration, boundSql, sql, parameterMappings, parameterObject);
    }

    private static String replaceDirectParameters(String sql, List<?> parameterMappings, Object parameterObject) {
        // 单值参数场景下，同一个参数对象按占位符顺序写入 SQL。
        Matcher matcher = PARAMETER_PATTERN.matcher(sql);
        StringBuffer sqlBuffer = new StringBuffer();
        for (int i = 0; i < parameterMappings.size() && matcher.find(); i++) {
            matcher.appendReplacement(sqlBuffer, Matcher.quoteReplacement(resolveParameterValue(parameterObject)));
        }
        matcher.appendTail(sqlBuffer);
        return sqlBuffer.toString();
    }

    private static String replaceObjectParameters(Object configuration,
                                                  Object boundSql,
                                                  String sql,
                                                  List<?> parameterMappings,
                                                  Object parameterObject) throws Exception {
        // 复杂对象和 Map 参数交给 MyBatis MetaObject 解析，保证嵌套属性也能读取。
        Object metaObject = invoke(configuration, "newMetaObject",
                new Class<?>[]{Object.class}, new Object[]{parameterObject});
        Matcher matcher = PARAMETER_PATTERN.matcher(sql);
        StringBuffer sqlBuffer = new StringBuffer();
        for (Object parameterMapping : parameterMappings) {
            Object value = null;
            String propertyName = String.valueOf(invoke(parameterMapping, "getProperty"));
            if (matcher.find()) {
                // MyBatis foreach 生成的临时参数优先从 BoundSql additionalParameters 读取。
                if (hasAdditionalParameter(boundSql, propertyName)) {
                    value = invoke(boundSql, "getAdditionalParameter",
                            new Class<?>[]{String.class}, new Object[]{propertyName});
                } else if (hasGetter(metaObject, propertyName)) {
                    value = invoke(metaObject, "getValue", new Class<?>[]{String.class}, new Object[]{propertyName});
                } else if (parameterObject instanceof Map<?, ?>) {
                    value = ((Map<?, ?>) parameterObject).get(propertyName);
                }
                matcher.appendReplacement(sqlBuffer, Matcher.quoteReplacement(resolveParameterValue(value)));
            }
        }
        matcher.appendTail(sqlBuffer);
        return sqlBuffer.toString();
    }

    private static boolean hasAdditionalParameter(Object boundSql, String propertyName) throws Exception {
        return Boolean.TRUE.equals(invoke(boundSql, "hasAdditionalParameter",
                new Class<?>[]{String.class}, new Object[]{propertyName}));
    }

    private static boolean hasGetter(Object metaObject, String propertyName) throws Exception {
        return Boolean.TRUE.equals(invoke(metaObject, "hasGetter",
                new Class<?>[]{String.class}, new Object[]{propertyName}));
    }

    private static String resolveParameterValue(Object value) {
        // 输出尽量接近可直接执行的 SQL 字面量，字符串和枚举需要补引号。
        if (value == null) {
            return "null";
        }
        if (value instanceof CharSequence || value instanceof Character) {
            return "'" + String.valueOf(value).replace("'", "''") + "'";
        }
        if (value instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT,
                    Locale.CHINA);
            return "'" + formatter.format(value) + "'";
        }
        if (value instanceof Enum<?>) {
            return "'" + ((Enum<?>) value).name().replace("'", "''") + "'";
        }
        return String.valueOf(value);
    }

    private static Object invoke(Object target, String methodName) throws Exception {
        return invoke(target, methodName, new Class<?>[0], new Object[0]);
    }

    private static Object invoke(Object target, String methodName, Class<?>[] parameterTypes, Object[] args)
            throws Exception {
        // 统一反射调用入口，InvocationTargetException 需要拆出原始异常再继续抛出。
        Method method = target.getClass().getMethod(methodName, parameterTypes);
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof Exception) {
                throw (Exception) targetException;
            }
            if (targetException instanceof Error) {
                throw (Error) targetException;
            }
            throw e;
        }
    }

    private static void info(String message) {
        log("info", message, null);
    }

    private static void warn(String message) {
        log("warn", message, null);
    }

    private static void error(String message, Throwable throwable) {
        log("error", message, throwable);
    }

    private static void log(String level, String message, Throwable throwable) {
        try {
            // agent 类可能由系统类加载器加载，slf4j 优先从业务线程上下文 ClassLoader 获取。
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Class<?> loggerFactoryClass = contextClassLoader == null
                    ? Class.forName("org.slf4j.LoggerFactory")
                    : Class.forName("org.slf4j.LoggerFactory", false, contextClassLoader);
            Object log = loggerFactoryClass.getMethod("getLogger", String.class).invoke(null, LOGGER_NAME);
            Method logMethod = log.getClass().getMethod(level, String.class, Throwable.class);
            logMethod.invoke(log, ORANGE + message + RESET, throwable);
        } catch (Throwable e) {
            // 找不到 slf4j 或反射调用失败时，至少把日志和原始异常输出到标准错误。
            System.err.println("[" + LOGGER_NAME + "] " + level.toUpperCase(Locale.ROOT) + " " + message);
            if (throwable != null) {
                throwable.printStackTrace(System.err);
            }
            if (throwable == null && "error".equals(level)) {
                e.printStackTrace(System.err);
            }
        }
    }
}



