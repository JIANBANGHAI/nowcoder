package com.nowcoder.community.interceptor;

import com.nowcoder.community.annotition.LoginRequire;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginRequireInterceptor implements HandlerInterceptor {
    @Autowired
    private ThreadUtil threadUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            HandlerMethod h =(HandlerMethod)handler;
            LoginRequire methodAnnotation = h.getMethodAnnotation(LoginRequire.class);
            User threadLocal = threadUtil.getThreadLocal();
            if (methodAnnotation!=null && threadLocal==null){
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
