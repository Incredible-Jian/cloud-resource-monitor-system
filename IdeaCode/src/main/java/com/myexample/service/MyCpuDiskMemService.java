package com.myexample.service;

import com.myexample.result.CpuResult;
import com.myexample.result.DiskResult;
import com.myexample.result.MemResult;

public interface MyCpuDiskMemService {
    public CpuResult findCpuResult();
    public MemResult findMemResult();
    public DiskResult findDiskResult();
}
