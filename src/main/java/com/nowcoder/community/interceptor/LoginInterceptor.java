package com.nowcoder.community.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CookieUtils;
import com.nowcoder.community.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private ThreadUtil threadUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtils.getCookie(request, "ticket");
        if (ticket!=null) {
            LoginTicket ticket1 = userService.getTicket(ticket);
            if (ticket1 != null && ticket1.getStatus() == 0 && ticket1.getExpired().after(new Date())) {
                User user = userService.getUserById(ticket1.getUserId());
                threadUtil.setThreadLocal(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User threadLocal = threadUtil.getThreadLocal();
        if(threadLocal!=null && modelAndView!=null){
            modelAndView.addObject("loginTicket",threadLocal);

        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadUtil.clearThreadLocal();
    }
}
