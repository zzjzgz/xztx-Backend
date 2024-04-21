package xyz.zzj.springbootxztxbackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import xyz.zzj.springbootxztxbackend.constant.UserConstant;
import xyz.zzj.springbootxztxbackend.model.domain.User;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.service
 * @ClassName: RedisGeoTest
 * @Author: zengz
 * @CreateTime: 2024/4/21 17:38
 * @Description: TODO 描述类的功能
 * @Version: 1.0
 */
@SpringBootTest
class RedisGeoTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Test
    void importUserGeoByRedis(){
        //查询所有用户
        List<User> userList = userService.list();
        String key = UserConstant.USER_GEO_KEY;
        //初始化经纬度list
        List<RedisGeoCommands.GeoLocation<String>> locationList = new ArrayList<>(userList.size());
        for (User user : userList){
            locationList.add(new RedisGeoCommands.GeoLocation<>(String.valueOf(user.getId())
                    ,new Point(user.getLongitude(),user.getLatitude())));
        }
        stringRedisTemplate.opsForGeo().add(key,locationList);
    }

    @Test
    void getUserGeo(){
        String key = UserConstant.USER_GEO_KEY;
        List<User> userList = userService.list();
        //计算 userId为 1 的用户和其他用户的距离
        for (User user : userList){
            Distance distance = stringRedisTemplate.opsForGeo()
                    .distance(key,"2",String.valueOf(user.getId()),RedisGeoCommands.DistanceUnit.KILOMETERS);
            System.out.println(user.getId() + "：" + distance.getValue() + distance.getUnit());
        }
    }

    @Test
    void searchUserGeo(){
        User user = userService.getById(1L);
        Distance geoRedis = new Distance(200, RedisGeoCommands.DistanceUnit.KILOMETERS);
        //以用户为中心，半径为180km搜索附近的用户
        Circle circle = new Circle(new Point(user.getLongitude(),user.getLatitude()),geoRedis);
        //查询后返回经纬度和id，并按距离从低到高排序
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs().includeCoordinates().sort(Sort.Direction.ASC);
        //查询200km范围内的用户
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius = stringRedisTemplate.opsForGeo()
                .radius(UserConstant.USER_GEO_KEY, circle, geoRadiusCommandArgs);
        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : radius) {
              if (!result.getContent().getName().equals("1")){
                  System.out.println(result.getContent().getName());
              }
        }
    }


}
