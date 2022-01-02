package com.nowcoder.community.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //随机字符串
    public static String generateUUId(){
        return UUID.randomUUID().toString().replace("-","");
    }
    //密码加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
   public static String getFastJson(int code, String msg, Map<String,Object> map){
       JSONObject object = new JSONObject();
       object.put("code",code);
       object.put("msg",msg);
       if (map != null) {
           for (String key:map.keySet()){
               object.put(key,map.get(key));
           }
       }
       return object.toJSONString();
   }
    public static String getFastJson(int code, String msg){
        return getFastJson(code,msg,null);
    }
    public static String getFastJson(int code){
        return getFastJson(code,null,null);
    }

}
