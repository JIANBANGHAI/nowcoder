package com.nowcoder.community.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
@Component
public class CookieUtils {
    public static String getCookie(HttpServletRequest request,String name){
        if(request==null || name==null){
            throw new NullPointerException("参数为空");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
    public static void removeCookie(HttpServletRequest request,String name){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)){
                    cookie.setMaxAge(0);
                }
            }
        }
    }
}
