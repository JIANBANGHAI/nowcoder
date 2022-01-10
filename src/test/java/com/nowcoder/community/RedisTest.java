package com.nowcoder.community;

import com.nowcoder.community.service.LikeService;
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
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LikeService likeService;
    @Test
    public void redisTest(){
        String key = "age";
        redisTemplate.opsForValue().set(key,20);
        System.out.println(redisTemplate.opsForValue().get(key));
    }

    @Test
    public void likeServiceTest(){

    }
    @Test
    public void likeCountTest(){
        long likeCount = likeService.getLikeCount(1, 295);
        System.out.println(likeCount);
    }
}
