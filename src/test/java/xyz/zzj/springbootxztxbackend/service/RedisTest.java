package xyz.zzj.springbootxztxbackend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("name","zzj");
        Object name = valueOperations.get("name");
        System.out.println(name);
        Assertions.assertTrue("zzj".equals((String) name));
    }
}
