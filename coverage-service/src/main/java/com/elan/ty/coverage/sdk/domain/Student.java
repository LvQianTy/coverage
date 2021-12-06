package com.elan.ty.coverage.sdk.domain;

import lombok.Data;

@Data
public class Student {
    /**
     * 学号
     */
    private String number;

    /**
     * 名字
     */
    private String name;

    /**
     * 年龄
     */
    private int age;
}
