package com.myexample.config;

import com.myexample.interceptor.AccessLogInterceptor;
import com.myexample.interceptor.AccessControlInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AccessControlInterceptor accessControlInterceptor;
    private final AccessLogInterceptor accessLogInterceptor;

    @Autowired
    public WebConfig(AccessControlInterceptor accessControlInterceptor,
                     AccessLogInterceptor accessLogInterceptor) {
        this.accessControlInterceptor = accessControlInterceptor;
        this.accessLogInterceptor = accessLogInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 访问日志拦截器（记录所有访问）
        registry.addInterceptor(accessLogInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/favicon.ico");

        // 访问控制拦截器（IP封禁检查）
        registry.addInterceptor(accessControlInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/static/**",
                        "/error",
                        "/ip-block/**",
                        "/favicon.ico"
                );
    }
}