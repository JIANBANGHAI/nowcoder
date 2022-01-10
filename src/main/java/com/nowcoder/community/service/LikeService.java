package com.nowcoder.community.service;

import com.nowcoder.community.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;
    //点赞
    public void getLike(int userId, int entityType, int entityId,int entityUserId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String key = RedisUtils.getKey(entityType, entityId);
                String userKey = RedisUtils.getUserKey(entityUserId);
                boolean result = redisTemplate.opsForSet().isMember(key,userId);

                redisOperations.multi();
                if (result){
                    redisTemplate.opsForSet().remove(key,userId);
                    redisOperations.opsForValue().decrement(userKey);
                }else {
                    redisTemplate.opsForSet().add(key,userId);
                    redisOperations.opsForValue().increment(userKey);
                }
                return redisOperations.exec();
            }
        });
    }
    //计数
    public long getLikeCount(int entityType, int entityId){
        String key = RedisUtils.getKey(entityType, entityId);
        return redisTemplate.opsForSet().size(key);
    }
    //记录状态
    public int getLikeStatus(int userId, int entityType, int entityId){
        String key = RedisUtils.getKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(key,userId) ? 1 : 0;
    }

    //查询用户获得的数量
    public int getUserCount(int entityUserId){
        String userKey = RedisUtils.getUserKey(entityUserId);
        Integer o = (Integer)redisTemplate.opsForValue().get(userKey);
        return o==null?0:o.intValue();
    }
}
