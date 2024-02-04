package xyz.zzj.springbootxztxbackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.service.UserService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static xyz.zzj.springbootxztxbackend.constant.UserConstant.PRECACHE_KEY_PREFIX;
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
    private List<Long> mainUser = Arrays.asList(1L);

    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "0 0 0 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock(PRECACHE_KEY_PREFIX + "lock");
        try {
            //实现了只有一个线程获取锁
            if (lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                System.out.println("tryLock:"+Thread.currentThread().getId());
                //测试续期机制
//                Thread.sleep(30000);
                for (Long aLong : mainUser) {
                    String key = RECOMMEND_KEY_PREFIX + aLong ;
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unLock:"+Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}


