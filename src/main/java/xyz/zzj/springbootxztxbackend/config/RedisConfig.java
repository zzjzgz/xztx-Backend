package xyz.zzj.springbootxztxbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        //创建RedisTemplate对象
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        //创建链接工厂
        redisTemplate.setConnectionFactory(connectionFactory);
        //设置key 的序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        //返回
        return redisTemplate;
    }
}