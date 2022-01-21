package com.nowcoder.community.utils;

/**
 * 生成存放于redis的结构类型
 * like:count:1:103
 */
public class RedisUtils {
    private static final String KEY = "like:entity";
    private static final String USER_KEY = "like:user";
    private static final String FOLLOW = "follow";
    private static final String FOLLOWER = "follower";
    private static final String KAPTCHA = "kaptcha";
    private static final String USER = "user";
    private static final String SPLIT = ":";

    //like:entity:1:111
    public static final String getKey(int entityType,int entityId){
        return KEY+SPLIT+entityType+SPLIT+entityId;
    }
    //like:user:111
    public static final String getUserKey(int userId){
        return USER_KEY+SPLIT+userId;
    }
    //关注实体->follow:111:1
    public static String getFollow(int userId,int entityType){
        //follow:169:3
        return FOLLOW+SPLIT+userId+SPLIT+entityType;
    }
    //我的粉丝->follower:1:153
    public static String getFollower(int entityType,int entityId){
        return FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
    //保存验证码
    public static String saveCode(String ticket){
        return KAPTCHA+SPLIT+ticket;
    }

    public static String getCatchKey(int id){
        return USER+SPLIT+id;
    }
}
