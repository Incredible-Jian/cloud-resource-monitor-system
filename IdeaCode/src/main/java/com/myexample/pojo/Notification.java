package com.myexample.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Notification {
    private String message;
    private Date timestamp;

    public Notification(String message) {
        this.message = message;
        this.timestamp = new Date();
    }
}