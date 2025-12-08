// 异常访问服务接口
package com.myexample.service;

import java.util.List;

public interface AbnormalAccessService {
    String checkAndBlockIfNeeded(String ip);
    List<String> getBlockedIps();
    void releaseExpiredBlocks();
}