import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author 36020
 */
@SuppressWarnings("CallToPrintStackTrace")
public class ZyaudFsCronAgent {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("hello java agent " + args);

        // 在 agent 加载时，向 JVM 添加一个字节码转换器
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                // 判断是否是我们需要修改的类
                String classNameFormatted = className.replace("/", ".");
                if ("com.zyaud.fzhx.fs.core.schedule.FileUploadStatusUpdateTask".equals(classNameFormatted)) {
                    try {
                        ClassPool classPool = ClassPool.getDefault();
                        CtClass ctClass = classPool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));

                        CtMethod method = ctClass.getDeclaredMethod("updateStatus");
                        method.setBody("{  }");

                        // 返回修改后的字节码
                        return ctClass.toBytecode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if ("com.zyaud.fzhx.multitask.thread.MultitaskGroupManagerThread".equals(classNameFormatted) || "com.zyaud.fzhx.multitask.thread.UpdateDeadTaskThread".equals(classNameFormatted)) {
                    try {
                        ClassPool classPool = ClassPool.getDefault();
                        CtClass ctClass = classPool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));

                        CtMethod method = ctClass.getDeclaredMethod("run");
                        method.setBody("{  }");

                        // 返回修改后的字节码
                        return ctClass.toBytecode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 返回原始字节码
                return classfileBuffer;
            }
        });
    }
}
