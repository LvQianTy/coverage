/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.elan.ty.coverage.redis.mock.data;

import org.springframework.data.redis.connection.DataType;

public class RedisObject {
    private Long ttl;
    private DataType type;
    private Object data;

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
