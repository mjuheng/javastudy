package com.huangch.cloud.utils.queue;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 延迟队列工具类
 *
 * @author huangch
 * @date 2023-10-07
 */
@Slf4j
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class DelayQueueUtils {

    private final DelayQueue<DelayEntity> delayQueue = new DelayQueue<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private volatile boolean running = false;

    public DelayQueueUtils() {
        this.task();
    }

    /**
     * 消费队列中的任务
     */
    private void task() {
        if (running) {
            return;
        }
        synchronized (this) {
            if (running) {
                return;
            }
            running = true;
        }
        executorService.execute(() -> {
            while (running) {
                try {
                    DelayEntity delayEntity = delayQueue.take();
                    log.debug("执行任务 id：{}, createTime: {}, endTime：{}, nowTime: {}", delayEntity.getTaskId(), delayEntity.getCreateTime(), delayEntity.getEndTime(), new Date());
                    Supplier<?> action = delayEntity.getAction();
                    action.get();
                } catch (InterruptedException e) {
                    log.error("队列执行错误", e);
                }
            }
        });
    }

    /**
     * 放入延迟任务
     *
     * @param action 延迟任务
     * @param delay  延迟时间
     * @param unit   延迟单位，最小单位为纳秒
     */
    public void delay(Supplier<?> action, long delay, TimeUnit unit) {
        long expectNanoAfter = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delay, unit);
        delayQueue.add(new DelayEntity(action, expectNanoAfter));
    }

    @SuppressWarnings({"unused", "InnerClassMayBeStatic"})
    @Data
    private class DelayEntity implements Delayed {

        private String taskId;
        private long expectNanoAfter;
        private Date createTime;
        private Date endTime;
        private Supplier<?> action;

        public DelayEntity(Supplier<?> action, long expireTime) {
            this(action, expireTime, UUID.randomUUID().toString());
        }

        public DelayEntity(Supplier<?> action, long expectNanoAfter, String taskId) {
            long currentTimeMillis = System.currentTimeMillis();

            this.action = action;
            this.expectNanoAfter = expectNanoAfter;
            this.createTime = new Date(currentTimeMillis);
            this.endTime = new Date(currentTimeMillis + (expectNanoAfter - System.nanoTime()) / 1000000);
            this.taskId = taskId;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expectNanoAfter - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (o.getDelay(TimeUnit.NANOSECONDS) - getDelay(TimeUnit.NANOSECONDS));
        }
    }
}
