// 新增的异常访问检测控制器
package com.myexample.controller;

import com.myexample.service.AbnormalAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/access")
public class AbnormalAccessController {

    @Autowired
    private AbnormalAccessService accessService;

    // 检测异常访问
    @GetMapping("/check")
    public String checkAbnormalAccess(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return accessService.checkAndBlockIfNeeded(ip);
    }

    // 获取当前被封禁的IP列表
    @GetMapping("/blocked")
    public List<String> getBlockedIps() {
        return accessService.getBlockedIps();
    }
}