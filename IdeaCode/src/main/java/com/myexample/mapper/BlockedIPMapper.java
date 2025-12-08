package com.myexample.mapper;

import com.myexample.pojo.BlockedIP;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface BlockedIPMapper {
    @Select("SELECT * FROM blocked_ips WHERE ip_address = #{ip} AND status = 1 AND (unblock_time IS NULL OR unblock_time > NOW())")
    BlockedIP findByIp(String ip);

    @Select("SELECT * FROM blocked_ips WHERE status = 1")
    List<BlockedIP> findAllActive();

    @Insert("INSERT INTO blocked_ips(ip_address, block_time, unblock_time, reason, status) " +
            "VALUES(#{ipAddress}, #{blockTime}, #{unblockTime}, #{reason}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(BlockedIP blockedIP);

    @Update("UPDATE blocked_ips SET status = 0 WHERE id = #{id}")
    void unblockById(int id);

    @Delete("DELETE FROM blocked_ips WHERE id = #{id}")
    void deleteById(int id);

    @Select("SELECT COUNT(*) FROM blocked_ips WHERE ip_address = #{ip} AND status = 1 AND (unblock_time IS NULL OR unblock_time > NOW())")
    int isBlocked(String ip);

    @Select("SELECT * FROM blocked_ips WHERE ip_address LIKE CONCAT('%', #{keyword}, '%') OR reason LIKE CONCAT('%', #{keyword}, '%')")
    List<BlockedIP> searchByKeyword(String keyword);

    @Select("SELECT * FROM blocked_ips WHERE status = 1 ORDER BY block_time DESC LIMIT #{offset}, #{limit}")
    List<BlockedIP> findAllActiveWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM blocked_ips WHERE status = 1")
    int countAllActive();
    @Update("UPDATE blocked_ips SET status = 0 WHERE status = 1 AND unblock_time <= #{now}")
    void unblockExpiredIPs(@Param("now") Date now);
}