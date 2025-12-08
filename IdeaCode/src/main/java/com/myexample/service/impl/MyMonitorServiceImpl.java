package com.myexample.service.impl;

import com.myexample.mapper.MonitorMapper;
import com.myexample.pojo.Monitor;
import com.myexample.service.MyMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyMonitorServiceImpl implements MyMonitorService {
    @Autowired
    private MonitorMapper mymapper;
    @Override
    public void insertMonitorData(Monitor monitor){
        mymapper.insertMonitorData(monitor);
    }
}
