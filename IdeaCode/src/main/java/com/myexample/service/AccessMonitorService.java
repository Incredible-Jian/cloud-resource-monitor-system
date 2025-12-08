package com.myexample.service;

import com.myexample.mapper.AccessLogMapper;
import com.myexample.service.BlockedIPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AccessMonitorService {
    @Value("${monitoring.ip-block.threshold:100}")
    private int blockThreshold;

    @Autowired
    private AccessLogMapper accessLogMapper;

    @Autowired
    private BlockedIPService blockedIPService;

    @Scheduled(fixedRate = 60 * 1000)
    public void monitorAccessFrequency() {
        Date oneMinuteAgo = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1));
        List<String> suspiciousIPs = accessLogMapper.findHighFrequencyIPs(oneMinuteAgo, blockThreshold);

        for (String ip : suspiciousIPs) {
            blockedIPService.autoBlockIP(ip, "自动封锁: 1分钟内访问次数超过" + blockThreshold + "次");
        }
    }
}