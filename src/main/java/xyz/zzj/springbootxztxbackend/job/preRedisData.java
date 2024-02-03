package xyz.zzj.springbootxztxbackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.service.UserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static xyz.zzj.springbootxztxbackend.constant.UserConstant.RECOMMEND_KEY_PREFIX;

@EnableScheduling
@Slf4j
@Component
public class preRedisData {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    //缓存预热的重点用户,可以通过数据库标识查询---> 重点用户的 id
    private List<Long> mainUser = new ArrayList<>();

    @Scheduled(cron = "0 40 17 * *  *")
    public void doCacheRecommendUser(){
        mainUser.add(1L);
        for (Long aLong : mainUser) {
            String key = RECOMMEND_KEY_PREFIX + aLong;
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            //3、缓存中不存在，查数据库
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            //分页
            Page<User> userPage = userService.page(new Page<>(1, 10), queryWrapper);
            //4、不存在，写入缓存
            try {
                valueOperations.set(key,userPage,60, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("redis set error",e);
            }
        }
    }

}
