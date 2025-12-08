package com.myexample.controller;

import com.myexample.service.MyCpuDiskMemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MyCpuDiskMemController {
    @Autowired
    private MyCpuDiskMemService mycdmservice;
    @RequestMapping("/cpu")
    public String getcpu(){
        return mycdmservice.findCpuResult().toString();
    }
    @RequestMapping("/mem")
    public String getmem(){
        return mycdmservice.findMemResult().toString();
    }
    @RequestMapping("/disk")
    public String getdisk(){
        return mycdmservice.findDiskResult().toString();
    }

}
