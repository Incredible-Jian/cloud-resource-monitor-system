package com.myexample.service;

import com.myexample.pojo.BlockedIP;

import java.util.List;
import java.util.Map;

public interface BlockedIPService {
    Map<String, Object> getAllActive(int page, int size);
    List<BlockedIP> getAllActive();
    void blockIP(BlockedIP blockedIP);
    void unblockIP(int id);
    boolean isIPBlocked(String ip);
    List<BlockedIP> searchIPs(String keyword);
    void autoBlockIP(String ip, String reason);
}