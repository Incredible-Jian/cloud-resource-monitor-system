package com.myexample.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockedIP {
    private int id;
    private String ipAddress;
    private Date blockTime;
    private Date unblockTime;
    private String reason;
    private int status; // 0-已解封, 1-封禁中
}