package com.ratelimiter.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private Integer age;
    private String role;
}
