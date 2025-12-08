package com.myexample.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Monitor {
    private int id;
    private String mydate;
    private String ip;
    private Double cpu_us;
    private Double cpu_sys;
    private Double cpu_id;
    private int mem_total;
    private int mem_used;
    private int disk_rate;
}
