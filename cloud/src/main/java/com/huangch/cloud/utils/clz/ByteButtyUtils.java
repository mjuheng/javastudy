package com.huangch.cloud.utils.clz;

import com.huangch.cloud.service.impl.UserServiceImpl;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Callable;

/**
 * @author huangch
 * @since 2025-09-24
 */
public class ByteButtyUtils {

    public static void init() {
        Instrumentation inst = ByteBuddyAgent.install();
        new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(ElementMatchers.named(UserServiceImpl.class.getName()))
                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                        builder.method(ElementMatchers.named("echo"))
                                .intercept(MethodDelegation.to(UseConnectionInterceptor.class))
                )
                .installOn(inst);
        try {
            inst.retransformClasses(UserServiceImpl.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class UseConnectionInterceptor {
        @RuntimeType
        public static String intercept(@AllArguments Object[] args, @SuperCall Callable<String> callback) throws Exception {
            System.out.println("hahaha");
            return callback.call();
        }
    }
}
