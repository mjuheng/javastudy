package com.huangch.cloud.utils.retry;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * @author huangch
 * @since 2025-12-29
 */
@Slf4j
public class RetryUtils {

    /** 默认重试次数 */
    private static final int DEFAULT_RETRY_TIMES = 3;

    /**
     * 有返回值的重试
     */
    @SafeVarargs
    public static <T> T retry(Supplier<T> supplier, Class<? extends Throwable>... retryOn) {
        return retry(supplier, DEFAULT_RETRY_TIMES, retryOn);
    }

    /**
     * 有返回值 + 指定重试次数
     */
    @SafeVarargs
    public static <T> T retry(Supplier<T> supplier,
                              int maxRetries,
                              Class<? extends Throwable>... retryOn) {

        int attempt = 0;
        while (true) {
            try {
                return supplier.get();
            } catch (Throwable e) {
                attempt++;

                if (!shouldRetry(e, attempt, maxRetries, retryOn)) {
                    throw e;
                }

                // 可选：打印日志
                log.error("Retry attempt {} due to {}", attempt, e.getClass().getSimpleName());
            }
        }
    }

    /**
     * 无返回值的重试
     */
    @SafeVarargs
    public static void retry(Runnable runnable, Class<? extends Throwable>... retryOn) {
        retry(() -> {
            runnable.run();
            return null;
        }, DEFAULT_RETRY_TIMES, retryOn);
    }

    /**
     * 判断是否需要重试
     */
    @SafeVarargs
    private static boolean shouldRetry(Throwable e,
                                       int attempt,
                                       int maxRetries,
                                       Class<? extends Throwable>... retryOn) {

        if (attempt >= maxRetries) {
            return false;
        }

        if (retryOn == null || retryOn.length == 0) {
            return true;
        }

        for (Class<? extends Throwable> clazz : retryOn) {
            if (clazz.isAssignableFrom(e.getClass())) {
                return true;
            }
        }
        return false;
    }
}

