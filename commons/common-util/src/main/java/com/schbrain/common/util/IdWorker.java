/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.schbrain.common.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 刚开始使用时一般为 18 位，但时间距离起始时间超过一定值后，会变为 19 位。
 * <p>
 * 消耗完 18 位所需的时间：1 * 10^18 / (3600 * 24 * 365 * 1000 * 2^22) ≈ 7.56 年。
 * <p>
 * 所以从 2015-01-01 起，大概在 2022-07-22，即时间差超过 7.56 年，就会达到 19 位。
 */
public class IdWorker {

    /**
     * 开始时间戳 (2015-01-01)
     */
    private static final long ID_EPOCH = 1420041600000L;
    /**
     * 序列在 id 中占的位数
     */
    private static final long sequenceBits = 12L;
    /**
     * 机器 id 在 id 中占的位数
     */
    private static final long workerIdBits = 5L;
    /**
     * 机器 id 偏移量
     */
    private static final long workerIdShift = sequenceBits;
    /**
     * 数据中心 id 在 id 中占的位数
     */
    private static final long datacenterIdBits = 5L;
    /**
     * 数据中心 id 偏移量
     */
    private static final long datacenterIdShift = sequenceBits + workerIdBits;
    /**
     * 最大机器 id
     */
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    /**
     * 最大数据中心 id
     */
    private static final long maxDatacenterId = ~(-1L << datacenterIdBits);
    /**
     * 时间戳偏移量
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    /**
     * 生成序列的掩码
     */
    private static final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 默认实例
     */
    private static final IdWorker INSTANCE = new IdWorker();

    /**
     * 机器 id
     */
    private final long workerId;
    /**
     * 数据中心 id
     */
    private final long datacenterId;
    /**
     * 生成 id 使用的开始时间截
     */
    private final long idEpoch;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;
    /**
     * 毫秒内序列，12位，2^12 = 4096个数字
     */
    private long sequence;

    public IdWorker() {
        this(ThreadLocalRandom.current().nextLong(maxWorkerId), ThreadLocalRandom.current().nextLong(maxDatacenterId), 0);
    }

    public IdWorker(long workerId, long datacenterId, long sequence) {
        this(workerId, datacenterId, sequence, ID_EPOCH);
    }

    public IdWorker(long workerId, long datacenterId, long sequence, long idEpoch) {
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
        this.idEpoch = idEpoch;
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException("workerId is illegal: " + workerId);
        }
        if (datacenterId < 0 || datacenterId > maxDatacenterId) {
            throw new IllegalArgumentException("datacenterId is illegal: " + workerId);
        }
        if (idEpoch >= System.currentTimeMillis()) {
            throw new IllegalArgumentException("idEpoch is illegal: " + idEpoch);
        }
    }

    /**
     * generate a new id
     */
    public static long getId() {
        return INSTANCE.nextId();
    }

    /**
     * generate a new id String
     */
    public static String getIdStr() {
        return String.valueOf(getId());
    }

    /**
     * get the timestamp (millis second) of id
     */
    public static long getIdTimestamp(long id) {
        return getIdTimestamp(id, INSTANCE);
    }

    /**
     * get the timestamp (millis second) of id
     */
    public static long getIdTimestamp(long id, IdWorker idWorker) {
        return idWorker.idEpoch + (id >> timestampLeftShift);
    }

    /**
     * generate a new id
     */
    private synchronized long nextId() {
        long timestamp = timeGen();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards.");
        }
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0;
        }
        // 上次生成ID的时间截
        lastTimestamp = timestamp;
        // 移位并通过或运算拼到一起生成ID
        return ((timestamp - idEpoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * wait for next timestamp
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * current timestamp
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

}
