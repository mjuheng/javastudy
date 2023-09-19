package com.huangch.cloud.utils.id;

/**
 * @author huangch
 * @date 2023-08-07
 */
public class SnowflakeIdGeneratorUtils {

    private static final long EPOCH = 1420070400000L; // 从这个时间点开始生成ID，用于减小生成的时间戳部分

    private final long machineId;
    private final long datacenterId;
    private long sequence = 0;
    private long lastTimestamp = -1;

    public SnowflakeIdGeneratorUtils(long machineId, long datacenterId) {
        if (machineId < 0 || machineId >= 1024) {
            throw new IllegalArgumentException("Machine ID must be between 0 and 1023");
        }
        if (datacenterId < 0 || datacenterId >= 32) {
            throw new IllegalArgumentException("Datacenter ID must be between 0 and 31");
        }
        this.machineId = machineId;
        this.datacenterId = datacenterId;
    }

    public synchronized long generateId() {
        long timestamp = System.currentTimeMillis() - EPOCH;

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & 4095; // 4095是12位二进制全为1的数
            if (sequence == 0) {
                // 如果序列号超过12位的最大值，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        // 将各部分组合成64位的ID
        return (timestamp << 22) | (datacenterId << 17) | (machineId << 12) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis() - EPOCH;
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis() - EPOCH;
        }
        return timestamp;
    }

}
