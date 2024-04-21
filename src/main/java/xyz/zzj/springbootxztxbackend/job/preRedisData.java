package xyz.zzj.springbootxztxbackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.vo.UserVO;
import xyz.zzj.springbootxztxbackend.service.UserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static xyz.zzj.springbootxztxbackend.constant.UserConstant.PRECACHE_KEY_PREFIX;
import static xyz.zzj.springbootxztxbackend.constant.UserConstant.RECOMMEND_KEY_PREFIX;
import static xyz.zzj.springbootxztxbackend.utils.UserListToUserVo.getUserVOList;

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
                log.info("tryLock:"+Thread.currentThread().getId());
                //测试续期机制
//                Thread.sleep(30000);
                for (Long aLong : mainUser) {
                    String key = RECOMMEND_KEY_PREFIX + aLong ;
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    //3.1、分页
                    long count = userService.count();
                    //3.2、获取随机数据
                    List<User> list = new ArrayList<>();
                    if (count > 10){
                        Random rand = new Random();
                        int offset = Math.abs(rand.nextInt((int) (count - 10 + 1)));
                        //3.3、大于10链接分页查询
                        queryWrapper.last("limit " + 10 + " offset " + offset);
                    }
                    list = userService.list(queryWrapper);
                    //3.3、对数据进行脱敏
                    List<UserVO> userVOList = getUserVOList(list);
                    //4、不存在，将脱敏后的list写入缓存
                    try {
                        valueOperations.set(key,userVOList,30, TimeUnit.SECONDS);
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
                log.info("unLock:"+Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}


