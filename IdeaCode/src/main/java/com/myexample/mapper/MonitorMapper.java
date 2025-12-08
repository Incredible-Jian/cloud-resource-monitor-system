package com.myexample.mapper;

import com.myexample.pojo.Monitor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MonitorMapper {
    @Insert("insert into monitor(mydate,ip,cpu_us,cpu_sys,cpu_id,mem_total,mem_used,disk_rate)" +
            " values(#{mydate},#{ip},#{cpu_us},#{cpu_sys},#{cpu_id},#{mem_total},#{mem_used}," +
            "#{disk_rate})")
    public void insertMonitorData(Monitor monitor);
}
