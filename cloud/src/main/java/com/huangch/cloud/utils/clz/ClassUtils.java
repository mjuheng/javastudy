package com.huangch.cloud.utils.clz;

import com.itranswarp.compiler.JavaStringCompiler;

import java.io.IOException;
import java.util.Map;

/**
 * 类工具类
 *
 * @author huangch
 * @since 2023-11-30
 */
public class ClassUtils {

    /**
     * 根据字符串动态生成class
     * <pre>
     * // source example
     * import java.util.Arrays;
     * public class Main {
     *      public static void main(String[] args) {
     *          System.out.println("hello world");
     *      }
     * }
     * </pre>
     *
     * @param packageName 包名
     * @param className   类型
     * @param source      代码字符串
     * @return class
     */
    public static Class<?> generate(String packageName, String className, String source) {
        String prefix = String.format("package %s;", packageName);
        String fullName = String.format("%s.%s", packageName, className);

        JavaStringCompiler compiler = new JavaStringCompiler();
        try {
            Map<String, byte[]> results = compiler.compile(className + ".java", prefix + source);
            return compiler.loadClass(fullName, results);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
