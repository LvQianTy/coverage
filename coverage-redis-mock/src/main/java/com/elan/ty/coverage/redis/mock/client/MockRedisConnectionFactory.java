/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.elan.ty.coverage.redis.mock.client;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;

public class MockRedisConnectionFactory implements RedisConnectionFactory {
    private volatile static MockRedisConnection redisConnection;

    @Override
    public RedisConnection getConnection() {
        if (redisConnection == null) {
            synchronized(MockRedisConnection.class) {
                if (redisConnection == null) {
                    redisConnection = new MockRedisConnection();
                }
            }
        }
        return redisConnection;
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        return null;
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return false;
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return null;
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        return null;
    }
}
