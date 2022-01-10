package com.nowcoder.community.service;

import com.nowcoder.community.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MyAttentionService {
    @Autowired
    private RedisTemplate redisTemplate;
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
}
