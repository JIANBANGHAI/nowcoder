package com.nowcoder.community.config;

import com.nowcoder.community.interceptor.LoginInterceptor;
import com.nowcoder.community.interceptor.LoginRequireInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private LoginRequireInterceptor loginRequireInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).excludePathPatterns(
                "/**/*.png","/**/*.js","/**/*.css","/**/*.png","/**/*.jpeg"
        );
        registry.addInterceptor(loginRequireInterceptor).excludePathPatterns(
                "/**/*.png","/**/*.js","/**/*.css","/**/*.png","/**/*.jpeg"
        );
    }
}
