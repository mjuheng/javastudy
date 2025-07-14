package com.huangch.cloud.utils.clz;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类工具类
 *
 * @author huangch
 * @since 2023-11-30
 */
@Slf4j
public class ClassUtils {

    /**
     * 根据字符串动态生成class
     * <p>
     * Examples:
     * <blockquote><pre>
     * Map<Class<?>, String> fieldMap = new HashMap<>();
     * fieldMap.put(String.class, "name");
     *
     * List<String> methodList = new ArrayList<>();
     * methodList.add("public void sayHello() { System.out.println(\"Hello World\"); }");
     * Class<?> clz = ClassUtils.generate("com.Student", fieldMap, methodList);
     *
     * Object obj = clz.getDeclaredConstructor().newInstance();
     * clz.getMethod("sayHello").invoke(obj);
     * </pre></blockquote>
     *
     * @param fullName   全类名
     * @param fieldMap   字段Map key.字段类型 value.字段名
     * @param methodList 方法列表 ps. public void sayHello() { System.out.println("Hello World"); }
     * @return class
     */
    public static Class<?> generate(String fullName, Map<Class<?>, String> fieldMap, List<String> methodList) {
        try {
            ClassPool pool = ClassPool.getDefault();

            CtClass ctClass = pool.makeClass(fullName);

            if (fieldMap != null) {
                for (Map.Entry<Class<?>, String> fieldEntry : fieldMap.entrySet()) {
                    Class<?> fieldClz = fieldEntry.getKey();
                    String fieldName = fieldEntry.getValue();

                    CtField nameField = new CtField(pool.get(fieldClz.getName()), fieldName, ctClass);
                    nameField.setModifiers(Modifier.PRIVATE);
                    ctClass.addField(nameField, CtField.Initializer.constant("World"));

                    ctClass.addMethod(CtNewMethod.getter("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), nameField));
                    ctClass.addMethod(CtNewMethod.setter("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), nameField));
                }
            }

            for (String method : methodList) {
                CtMethod sayHello = CtNewMethod.make(
                        method,
                        ctClass);
                ctClass.addMethod(sayHello);
            }

            // 将类加载到 JVM 并使用
            return ctClass.toClass();
        } catch (NotFoundException | CannotCompileException e) {
            log.error("generate class error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改类实现方法，需要在类被加载前执行
     * <p>
     * <blockquote><pre>
     * ClassUtils.modifyMethod("Student", "{System.out.println(\"Hello World\");}", "sayHello")
     * </pre></blockquote>
     *
     * @param fullName       全类名
     * @param source         实现方法
     * @param method         方法名
     * @param parameterTypes 方法参数
     */
    public static void modifyMethod(String fullName, String source, String method, Class<?>... parameterTypes) {
        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        try {
            CtClass ctClass = pool.get(fullName);

            List<CtClass> params = new ArrayList<>();
            for (Class<?> parameterType : parameterTypes) {
                params.add(pool.get(parameterType.getName()));
            }

            CtMethod ctMethod = ctClass.getDeclaredMethod(method, params.toArray(new CtClass[0]));
            ctMethod.setBody(source);
            ctClass.toClass();
            ctClass.detach();
        } catch (NotFoundException | CannotCompileException e) {
            log.error("method modify error", e);
            throw new RuntimeException(e);
        }
    }
}
