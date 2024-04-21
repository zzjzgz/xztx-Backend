package xyz.zzj.springbootxztxbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import xyz.zzj.springbootxztxbackend.connon.BaseResponse;
import xyz.zzj.springbootxztxbackend.connon.ErrorCode;
import xyz.zzj.springbootxztxbackend.connon.ResultUtils;
import xyz.zzj.springbootxztxbackend.constant.UserConstant;
import xyz.zzj.springbootxztxbackend.exception.BusinessException;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.request.UserLoginRequest;
import xyz.zzj.springbootxztxbackend.model.domain.request.UserRegisterRequest;
import xyz.zzj.springbootxztxbackend.model.domain.vo.UserVO;
import xyz.zzj.springbootxztxbackend.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static xyz.zzj.springbootxztxbackend.constant.UserConstant.RECOMMEND_KEY_PREFIX;
import static xyz.zzj.springbootxztxbackend.constant.UserConstant.USER_LOGIN_STATE;
import static xyz.zzj.springbootxztxbackend.utils.UserListToUserVo.getUserVOList;

/**
 * @BelongsProject: springboot-user-center
 * @BelongsPackage: xyz.zzj.springbootusercenter.controller
 * @Author: zengzhaojun
 * @CreateTime: 2024-01-09  15:30
 * @Description: TODO
 * @Version: 1.0
 */

@RestController
@RequestMapping("/user")
//这个是线上用于跨域的，本地请注释其注解，上线记得改服务器地址
//@CrossOrigin(origins = {"http://localhost:5173/"},allowCredentials = "true")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    //注册
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return null;
        }
        long l = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(l);
    }

    //登录
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    //用户注销
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int i = userService.userLogout(request);
        return ResultUtils.success(i);
    }

    //用户信息接口
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest httpServletRequest){
        Object userObj = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR,"用户未登录");
        }
        Long userId = currentUser.getId();
        if (userId == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        //记录用户的登录态
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return ResultUtils.success(safetyUser);
    }

    //搜索用户
    @GetMapping("/search")
    public BaseResponse<List<User> > userSearch(String username,HttpServletRequest httpServletRequest){
        //判断是否为管理员
        if (!userService.isAdmin(httpServletRequest)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //查询数据
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isAnyBlank(username)){  //存在就查询，不存在就返回空
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> collect = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request) throws IllegalAccessException {
        int num = 0;
        //校验是否为空
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        /*//对除id外的所有字段判空
        Class<?> clazz = user.getClass(); // 获取对象的Class信息

        Field[] fields = clazz.getDeclaredFields(); // 获取对象的所有字段

        for (int i = 0;i< fields.length;i++){
            if (!fields[i].getName().equals("id")){
                fields[i].setAccessible(true); // 设置为可访问

                Object value = fields[i].get(user); // 获取该字段的值

                if (value == null || "".equals(value)) { // 如果值为null或空字符串
                    num++;
                }
            }
        }
        if (num == fields.length-2){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR,"请填入有效值");
        }*/
        User loginUser = userService.getLoginUser(request);
        //是管理员
        if (userService.isAdmin(loginUser)){
            int flag = userService.AdminUpdateUser(user);
            return ResultUtils.success(flag);
        }
        int flag = userService.updateUser(user,loginUser);
        return ResultUtils.success(flag);
    }

    //删除用户
    @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(long id,HttpServletRequest httpServletRequest){
        //判断是否为管理员
        if (!userService.isAdmin(httpServletRequest)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);//更新逻辑删除字段
        return ResultUtils.success(b);
    }

    //按标签搜索用户
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    //主页推荐
    @GetMapping("/recommend")
    public BaseResponse<Page<UserVO>> recommendUser(int pageSize, int pageNum,HttpServletRequest request){
        Page<User> userPage = null;
        Page<UserVO> userVOPage = null;
        try {
            User loginUser = userService.getLoginUser(request);
            //1、判断缓存是否存在
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            String key = RECOMMEND_KEY_PREFIX + loginUser.getId();
            List<UserVO> userVOList = (List<UserVO>)valueOperations.get(key);
            //2、缓存存在返回
            if (!CollectionUtils.isEmpty(userVOList)){
                //2.1、list转分页
                userVOPage = getlistPage(pageSize, pageNum, userVOList);
                return ResultUtils.success(userVOPage);
            }
            //3、缓存中不存在，查数据库
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.ne("id",loginUser.getId());
            //3.1、分页
            long count = userService.count();
            //3.2、获取随机数据
            List<User> list = new ArrayList<>();
            if (count > pageSize){
                Random rand = new Random();
                int offset = Math.abs(rand.nextInt((int) (count - pageSize + 1)));
                //3.3、大于10链接分页查询
                queryWrapper.last("limit " + pageSize + " offset " + offset);
            }
            list = userService.list(queryWrapper);
            //3.3、对数据进行脱敏
            userVOList = getUserVOList(list);
            //计算距离必返回前端
            String keyGeo = UserConstant.USER_GEO_KEY;
            //计算 userId为 1 的用户和其他用户的距离
            for (User user : list){
                Distance distance = stringRedisTemplate.opsForGeo()
                        .distance(keyGeo,String.valueOf(loginUser.getId()),String.valueOf(user.getId()), RedisGeoCommands.DistanceUnit.KILOMETERS);
                userVOList.stream().map(userVO -> {
                    if (userVO.getId().equals(user.getId())){
                        userVO.setDistance(distance.getValue());
                    }
                    return userVO;
                }).collect(Collectors.toList());
            }
            //3.4、list转page
            userVOPage = getlistPage(pageSize, pageNum, userVOList);
            //4、不存在，写入缓存
            try {
                valueOperations.set(key,userVOList,30, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("redis set error",e);
            }
        }catch (BusinessException e){
            //用户未登录，可以看这个
            userPage = userService.page(new Page<>(pageNum, pageSize));
            //对page数据进行脱敏
            userVOPage = getUserVOPage(pageSize, pageNum, userPage);
            return ResultUtils.success(userVOPage);
        }
        return ResultUtils.success(userVOPage);
    }

    /**
     * 将list转换为page
     * @param pageSize
     * @param pageNum
     * @param userList
     * @return
     */
    public static Page<UserVO> getlistPage(int pageSize, int pageNum, List<UserVO> userList) {
        Page<UserVO> userPage;
        userPage = new Page<>(pageNum, pageSize);
        // 当前页第一条数据在List中的位置
        int start = (int)((userPage.getCurrent() - 1) * userPage.getSize());
        // 当前页最后一条数据在List中的位置
        int end = (int)((start + userPage.getSize()) > userList.size() ? userList.size() : (userPage.getSize() * userPage.getCurrent()));
        userPage.setRecords(new ArrayList<>());
        userPage.setTotal(userList.size());
        if (userPage.getSize()*(userPage.getCurrent()-1) <= userPage.getTotal()) {
            // 分隔列表 当前页存在数据时 设置
            userPage.setRecords(userList.subList(start, end));
        }
        return userPage;
    }




    /**
     * 对page分页数据进行脱敏
     * @param pageSize
     * @param pageNum
     * @param userPage
     * @return
     */
    private static Page<UserVO> getUserVOPage(int pageSize, int pageNum, Page<User> userPage) {
        Page<UserVO> userVOPage;
        List<UserVO> userVOList = getUserVOList(userPage.getRecords());
        userVOPage = getlistPage(pageSize, pageNum, userVOList);
        return userVOPage;
    }

    //匹配
    @GetMapping("/match")
    public BaseResponse<List<UserVO>> matchUser(long num,HttpServletRequest request){
        if (num <= 0 || num>=20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> userVOList = userService.matchUsers(num, loginUser);
        if (CollectionUtils.isEmpty(userVOList)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(userVOList);
    }

    /**
     * 搜索附近用户
     * @param request
     * @return
     */
    @GetMapping("/nearby")
    public BaseResponse<List<UserVO>> nearbyUser(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        Long userId = loginUser.getId();
        List<UserVO> userVOList = userService.nearbyUser(userId);
        if (CollectionUtils.isEmpty(userVOList)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(userVOList);
    }
}

