/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.elan.ty.coverage.redis.mock.config;

import com.elan.ty.coverage.redis.mock.client.MockRedisConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisMockConfiguration {

    @Bean
    public RedisConnectionFactory redisMockConnectionFactory() {
        try {
            RedisConnectionFactory factory = new MockRedisConnectionFactory();
            return factory;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Bean
    public RedisTemplate redisMockTemplate(RedisConnectionFactory factory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

}
