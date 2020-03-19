package com.example.consumer.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LXJ
 * @description
 * @date 2020/3/2 20:33
 */
@Data
public class Order implements Serializable {
    private String id;
    private String name;
    private String content;
}
