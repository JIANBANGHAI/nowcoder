package com.nowcoder.community;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.nowcoder.community.service.MyAttentionService;
import com.nowcoder.community.utils.RedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class AttentionTest {
    @Autowired
    private MyAttentionService myAttentionService;

    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void follow(){
        try {
            myAttentionService.follow(111,3,153);
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    @Test
    public void fo1(){
        try {
            boolean b = myAttentionService.followerStatus(111, 3, 149);
            System.out.println(b);
        } catch (Exception e) {
            System.out.println("error");
        }
    }
    @Test
    public void f02(){
        try {
            boolean b = myAttentionService.followerStatus(111, 3, 169);
            System.out.println(b);
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    @Test
    public void f03(){
        String follow = RedisUtils.getFollow(111, 149);
        boolean a = redisTemplate.opsForZSet().score(follow,149)!=null;
        System.out.println(a);
    }
}
