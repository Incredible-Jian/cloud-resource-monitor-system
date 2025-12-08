// 异常访问服务实现
package com.myexample.service.impl;

import com.myexample.service.AbnormalAccessService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AbnormalAccessServiceImpl implements AbnormalAccessService {

    private static final int THRESHOLD = 5000; // 1分钟内访问阈值
    private static final int BLOCK_HOURS = 24; // 封禁小时数

    private final Map<String, Integer> accessCount = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockedIps = new ConcurrentHashMap<>();

    @Override
    public String checkAndBlockIfNeeded(String ip) {
        if (blockedIps.containsKey(ip)) {
            return "您的IP已被暂时封锁，请24小时后再试";
        }

        int count = accessCount.merge(ip, 1, Integer::sum);
        if (count > THRESHOLD) {
            blockedIps.put(ip, LocalDateTime.now());
            accessCount.remove(ip);
            return "检测到异常访问，您的IP已被封锁24小时";
        }
        return "正常访问";
    }

    @Override
    public List<String> getBlockedIps() {
        return new ArrayList<>(blockedIps.keySet());
    }

    @Override
    @Scheduled(fixedRate = 60000) // 每分钟清理一次计数器
    public void releaseExpiredBlocks() {
        LocalDateTime now = LocalDateTime.now();
        blockedIps.entrySet().removeIf(entry ->
                entry.getValue().plusHours(BLOCK_HOURS).isBefore(now)
        );

        // 每分钟重置访问计数器
        accessCount.clear();
    }
}