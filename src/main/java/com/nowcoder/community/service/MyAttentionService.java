package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginStatus;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MyAttentionService  implements LoginStatus {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    //关注
    public void follow(int userId,int entityType,int entityId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String follow = RedisUtils.getFollow(userId, entityType);
                String onFollow = RedisUtils.getFollower(entityType,entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().add(follow,entityId,System.currentTimeMillis());
                redisOperations.opsForZSet().add(onFollow,userId,System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    //取关
    public void onFollow(int userId,int entityType,int entityId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String follow = RedisUtils.getFollow(userId, entityType);
                //follow:111:3
                String onFollow = RedisUtils.getFollower(entityType,entityId);

                redisOperations.multi();

                redisOperations.opsForZSet().remove(follow,entityId);
                redisOperations.opsForZSet().remove(onFollow,userId);

                return redisOperations.exec();
            }
        });
    }

    //用户关注实体数量
    public long getFollowCount(int userId, int entityType){
        String follow = RedisUtils.getFollow(userId, entityType);
        return redisTemplate.opsForZSet().zCard(follow);
    }
    //当前用户被加关数量
    public long getFollowerCount(int entityType, int entityId){
        String follower = RedisUtils.getFollower(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(follower);
    }

    //记录关注状态
    public boolean followerStatus(int userId,int entityType,int entityId){
        String follow = RedisUtils.getFollow(userId, entityType);
        return redisTemplate.opsForZSet().score(follow,entityId)!=null;
    }

    //获取关注列表信息
    public List<Map<String,Object>> getFollowUserList(int userId, int offset, int limit){
        String follow = RedisUtils.getFollow(userId,ENTITY_USER_DISCUSS);
        Set<Integer> set = redisTemplate.opsForZSet().range(follow,offset,offset+limit-1);
        List<Map<String,Object>> mapList = new ArrayList<>();
        if (mapList!=null){
            for (Integer targetId : set) {
                Map<String,Object> map = new HashMap<>();
                User user = userService.getUserById(targetId);
                map.put("user",user);
                double time = redisTemplate.opsForZSet().score(follow,targetId);
                map.put("followTime",new Date((long) time));
                mapList.add(map);
            }
        }
        return mapList;
    }
    //获取粉丝列表信息
    public List<Map<String,Object>> getFollowerUserList(int userId, int offset, int limit){
        String follower = RedisUtils.getFollower(ENTITY_USER_DISCUSS,userId);
        Set<Integer> set = redisTemplate.opsForZSet().range(follower,offset,offset+limit-1);
        List<Map<String,Object>> mapList = new ArrayList<>();
        if (mapList!=null){
            for (Integer targetId : set) {
                Map<String,Object> map = new HashMap<>();
                User user = userService.getUserById(targetId);
                map.put("user",user);
                double time = redisTemplate.opsForZSet().score(follower,targetId);
                map.put("followerTime",new Date((long) time));
                mapList.add(map);
            }
        }
        return mapList;
    }
}
