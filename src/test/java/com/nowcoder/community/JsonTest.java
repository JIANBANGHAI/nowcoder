package com.nowcoder.community;

import com.nowcoder.community.utils.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class JsonTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void jsonT1(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","admin");
        map.put("age",20);
        String fastJson = CommunityUtil.getFastJson(0, "successful!",map);
        System.out.println(fastJson);
    }
    @Test
    public void a1(){
       redisTemplate.opsForValue().set("name","admin");
       redisTemplate.opsForValue().get("name");

    }
}
