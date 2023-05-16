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

/***
 * @author adyliu (imxylz@gmail.com)
 * @since 1.0
 */
public class IdWorker {

    /**
     * 生成的自增id的大小减少到18位
     */
    private static final long ID_EPOCH = 1420041600000L;

    private static final long workerIdBits = 5L;

    private static final long datacenterIdBits = 5L;

    private static final long maxWorkerId = ~(-1L << workerIdBits);

    private static final long maxDatacenterId = ~(-1L << datacenterIdBits);

    private static final long sequenceBits = 12L;

    private static final long workerIdShift = sequenceBits;

    private static final long datacenterIdShift = sequenceBits + workerIdBits;

    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    private static final long sequenceMask = ~(-1L << sequenceBits);

    private static final IdWorker INSTANCE = new IdWorker();

    private final long workerId;

    private final long datacenterId;

    private final long idEpoch;

    private long lastTimestamp = -1L;

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
        return String.valueOf(INSTANCE.nextId());
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

    private synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards.");
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - idEpoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

}
