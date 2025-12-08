package com.myexample.mapper;

import com.myexample.result.CpuResult;
import com.myexample.result.DiskResult;
import com.myexample.result.MemResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MyCpuDiskMemNum {
    @Select("SELECT mydate, cpu_id FROM monitor ORDER BY mydate DESC LIMIT 1")
    CpuResult findCpuResult();

    @Select("SELECT mydate, mem_total, mem_used FROM monitor ORDER BY mydate DESC LIMIT 1")
    MemResult findMemResult();

    @Select("SELECT mydate, disk_rate FROM monitor ORDER BY mydate DESC LIMIT 1")
    DiskResult findDiskResult();
}
