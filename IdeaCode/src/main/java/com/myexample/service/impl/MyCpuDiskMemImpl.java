package com.myexample.service.impl;

import com.myexample.mapper.MyCpuDiskMemNum;
import com.myexample.result.CpuResult;
import com.myexample.result.DiskResult;
import com.myexample.result.MemResult;
import com.myexample.service.MyCpuDiskMemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyCpuDiskMemImpl implements MyCpuDiskMemService {

    @Autowired
    private MyCpuDiskMemNum mycpudiskmem;

    @Override
    public CpuResult findCpuResult() {
        return mycpudiskmem.findCpuResult();
    }

    @Override
    public MemResult findMemResult() {
        return mycpudiskmem.findMemResult();
    }

    @Override
    public DiskResult findDiskResult() {
        return mycpudiskmem.findDiskResult();
    }
}
