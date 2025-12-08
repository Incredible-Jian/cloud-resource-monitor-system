package com.myexample.service.impl;

import com.myexample.mapper.BlockedIPMapper;
import com.myexample.pojo.BlockedIP;
import com.myexample.service.BlockedIPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlockedIPServiceImpl implements BlockedIPService {
    @Autowired
    private BlockedIPMapper blockedIPMapper;

    @Override
    public Map<String, Object> getAllActive(int page, int size) {
        int offset = (page - 1) * size;
        List<BlockedIP> content = blockedIPMapper.findAllActiveWithPagination(offset, size);
        int totalElements = blockedIPMapper.countAllActive();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("currentPage", page);

        return response;
    }

    @Override
    public List<BlockedIP> getAllActive() {
        return blockedIPMapper.findAllActive();
    }

    @Override
    public void blockIP(BlockedIP blockedIP) {
        blockedIPMapper.insert(blockedIP);
    }

    @Override
    public void unblockIP(int id) {
        blockedIPMapper.unblockById(id);
    }

    @Override
    public boolean isIPBlocked(String ip) {
        return blockedIPMapper.isBlocked(ip) > 0;
    }

    @Override
    public List<BlockedIP> searchIPs(String keyword) {
        return blockedIPMapper.searchByKeyword(keyword);
    }

    @Override
    public void autoBlockIP(String ip, String reason) {
        if (!isIPBlocked(ip)) {
            BlockedIP blockedIP = new BlockedIP();
            blockedIP.setIpAddress(ip);
            blockedIP.setBlockTime(new Date());

            // 设置24小时后自动解封
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 24);
            blockedIP.setUnblockTime(cal.getTime());

            blockedIP.setReason(reason);
            blockedIP.setStatus(1);
            blockIP(blockedIP);
        }
    }
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void autoUnblockExpiredIPs() {
        Date now = new Date();
        blockedIPMapper.unblockExpiredIPs(now);
    }
}