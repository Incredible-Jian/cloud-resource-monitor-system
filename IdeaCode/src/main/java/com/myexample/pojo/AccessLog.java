package com.myexample.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessLog {
    private Long id;
    private String ipAddress;
    private Date accessTime;
    private String requestPath;
    private String userAgent;
}