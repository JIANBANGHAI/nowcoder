package com.nowcoder.community.utils;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ThreadUtil {
    private ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public  User getThreadLocal(){
        return threadLocal.get();
    }
    public void setThreadLocal(User user){
        threadLocal.set(user);
    }

    public void clearThreadLocal(){
        threadLocal.remove();
    }
}
