package com.myexample.mapper;

import com.myexample.pojo.AccessLog;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface AccessLogMapper {
    @Insert("INSERT INTO access_logs(ip_address, access_time, request_path, user_agent) " +
            "VALUES(#{ipAddress}, #{accessTime}, #{requestPath}, #{userAgent})")
    void insert(AccessLog accessLog);

    @Select("SELECT COUNT(*) FROM access_logs " +
            "WHERE ip_address = #{ip} " +
            "AND access_time >= #{startTime}")
    int countAccessSince(@Param("ip") String ip, @Param("startTime") Date startTime);

    @Select("SELECT ip_address FROM access_logs " +
            "WHERE access_time >= #{since} " +
            "GROUP BY ip_address " +
            "HAVING COUNT(*) > #{threshold}")
    List<String> findHighFrequencyIPs(@Param("since") Date since, @Param("threshold") int threshold);

    @Delete("DELETE FROM access_logs WHERE access_time < #{expireTime} LIMIT #{limit}")
    int deleteExpiredLogsBatch(@Param("expireTime") Date expireTime, @Param("limit") int limit);
}