package com.myexample.interceptor;

import com.myexample.mapper.AccessLogMapper;
import com.myexample.pojo.AccessLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class AccessLogInterceptor implements HandlerInterceptor {
    @Autowired
    private AccessLogMapper accessLogMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = getClientIP(request);

        AccessLog log = new AccessLog();
        log.setIpAddress(ip);
        log.setAccessTime(new Date());
        log.setRequestPath(request.getRequestURI());
        log.setUserAgent(request.getHeader("User-Agent"));

        accessLogMapper.insert(log);

        return true;
    }

    private String getClientIP(HttpServletRequest request) {
        // 使用与AccessControlInterceptor相同的IP获取逻辑
        // 这里省略具体实现，直接调用AccessControlInterceptor的方法
        // 实际项目中应复用同一IP获取方法
        return new AccessControlInterceptor().getClientIp(request);
    }
}