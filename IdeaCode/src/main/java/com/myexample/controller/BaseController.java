package com.myexample.controller;

import com.myexample.service.BlockedIPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    @Autowired
    private BlockedIPService blockedIPService;

    @ModelAttribute
    public void checkBlockedIP(HttpServletRequest request) {
        String clientIP = request.getRemoteAddr();
        if (blockedIPService.isIPBlocked(clientIP)) {
            throw new RuntimeException("您的IP已被封锁，无法访问");
        }
    }
}