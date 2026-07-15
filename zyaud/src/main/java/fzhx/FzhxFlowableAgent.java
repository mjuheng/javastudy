package fzhx;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public final class FzhxFlowableAgent {
    private static final String PROCESS_TASK_CLASS =
        "com/zyaud/base/flowable/core/workflow/service/impl/ProcessTaskServiceImpl";
    private static final String TASK_EXECUTOR_CLASS =
        "org/springframework/scheduling/concurrent/ThreadPoolTaskExecutor";
    private static final String LOG_PATH_PROPERTY = "process.task.exception.agent.log";

    private FzhxFlowableAgent() {
    }

    public static void register(Instrumentation instrumentation) {
        // 在一个转换函数中集中处理 Flowable 待办任务和 Spring 线程池增强。
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader,
                                    String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) {
                if (!PROCESS_TASK_CLASS.equals(className) && !TASK_EXECUTOR_CLASS.equals(className)) {
                    return null;
                }

                CtClass targetClass = null;
                try {
                    ClassPool classPool = new ClassPool(true);
                    if (loader != null) {
                        classPool.insertClassPath(new LoaderClassPath(loader));
                    }
                    targetClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    int enhancedMethodCount = 0;

                    if (PROCESS_TASK_CLASS.equals(className)) {
                        for (CtMethod method : targetClass.getDeclaredMethods()) {
                            if (method.getName().startsWith("lambda$getUserTodoList$")
                                && method.getSignature().contains("Ljava/util/concurrent/CountDownLatch;")) {
                                method.addCatch(
                                    "{ fzhx.FzhxFlowableAgent.writeLog(\"获取用户待办异步任务执行异常\", $e); throw $e; }",
                                    classPool.get(Throwable.class.getName())
                                );
                                enhancedMethodCount++;
                            }
                        }
                    } else {
                        for (CtMethod method : targetClass.getDeclaredMethods("submit")) {
                            if ("(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;".equals(method.getSignature())) {
                                method.insertBefore(
                                    "{ $1 = fzhx.FzhxFlowableAgent$TargetTaskRunnableWrapper.wrapIfTarget($1); }"
                                );
                                enhancedMethodCount++;
                            }
                        }
                    }

                    if (enhancedMethodCount == 0) {
                        throw new IllegalStateException("未找到需要增强的目标方法：" + className);
                    }
                    writeLog("已增强 " + className.replace('/', '.') + "，命中方法数：" + enhancedMethodCount, null);
                    return targetClass.toBytecode();
                } catch (Throwable e) {
                    writeLog("增强 Flowable 待办异常诊断逻辑失败：" + className, e);
                    return null;
                } finally {
                    if (targetClass != null) {
                        targetClass.detach();
                    }
                }
            }
        });
    }

    public static synchronized void writeLog(String message, Throwable throwable) {
        String level = throwable == null ? "INFO" : "ERROR";
        System.err.println("[fzhx-flowable-agent] " + level + " " + message);
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }

        String logPath = System.getProperty(LOG_PATH_PROPERTY, "process-task-exception-agent.log");
        try (PrintStream printStream = new PrintStream(new FileOutputStream(new File(logPath), true), true, "UTF-8")) {
            printStream.println("[fzhx-flowable-agent] " + level + " " + message);
            if (throwable != null) {
                throwable.printStackTrace(printStream);
            }
        } catch (Exception e) {
            System.err.println("[fzhx-flowable-agent] ERROR 写入日志文件失败：" + logPath);
            e.printStackTrace(System.err);
        }
    }

    public static final class TargetTaskRunnableWrapper implements Runnable {
        private final Runnable delegate;

        private TargetTaskRunnableWrapper(Runnable delegate) {
            this.delegate = delegate;
        }

        public static Runnable wrapIfTarget(Runnable runnable) {
            // 只包装从 getUserTodoList 调用链提交的异步任务。
            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                if ("com.zyaud.base.flowable.core.workflow.service.impl.ProcessTaskServiceImpl".equals(element.getClassName())
                    && "getUserTodoList".equals(element.getMethodName())) {
                    writeLog("已捕获用户待办异步任务提交，任务类型：" + runnable.getClass().getName(), null);
                    return new TargetTaskRunnableWrapper(runnable);
                }
            }
            return runnable;
        }

        @Override
        public void run() {
            writeLog("用户待办异步任务开始执行，线程：" + Thread.currentThread().getName(), null);
            try {
                delegate.run();
                writeLog("用户待办异步任务正常结束，线程：" + Thread.currentThread().getName(), null);
            } catch (Throwable e) {
                writeLog("获取用户待办异步任务执行异常", e);
                throw e;
            }
        }
    }
}
