/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.elan.ty.coverage.redis.mock.data;

import java.io.Serializable;
import java.util.Arrays;

public class Slice implements Comparable<Slice>, Serializable {
    private final byte[] data;

    public Slice(byte[] data) {
        this.data = data.clone();
    }

    public Slice(String data) {
        this.data = data.getBytes().clone();
    }

    public Slice(Long data) {
        this.data = String.valueOf(data).getBytes().clone();
    }

    public Slice(Double data) {
        this.data = String.valueOf(data).getBytes().clone();
    }

    public byte[] data() {
        return this.data.clone();
    }

    public int length() {
        return this.data.length;
    }

    @Override
    public String toString() {
        return new String(data);
    }

    @Override
    public boolean equals(Object b) {
        return b instanceof Slice && Arrays.equals(data, ((Slice) b).data());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public int compareTo(Slice o) {
        int len1 = data.length;
        int len2 = o.data.length;
        int lim = Math.min(len1, len2);

        int k = 0;
        while (k < lim) {
            byte b1 = data[k];
            byte b2 = o.data[k];
            if (b1 != b2) {
                return b1 - b2;
            }
            k++;
        }
        return len1 - len2;
    }
}
