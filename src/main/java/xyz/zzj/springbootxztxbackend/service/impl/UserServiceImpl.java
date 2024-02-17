package xyz.zzj.springbootxztxbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import xyz.zzj.springbootxztxbackend.connon.ErrorCode;
import xyz.zzj.springbootxztxbackend.exception.BusinessException;
import xyz.zzj.springbootxztxbackend.mapper.UserMapper;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.vo.UserVO;
import xyz.zzj.springbootxztxbackend.service.UserService;
import xyz.zzj.springbootxztxbackend.utils.AlgorithmUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static xyz.zzj.springbootxztxbackend.constant.UserConstant.*;

/**
 * @author zeng
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-01-08 16:50:47
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;





    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //判断非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //账户：不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        //密码：不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        //账户不能包含特殊字符，用正则表达式
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户中含特殊字符");
        }
        //判断密码和校验密码是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不相等");
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        //2、对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((S_ALT + userPassword).getBytes());
        //3、插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        //设置默认头像和昵称
        user.setUsername(userAccount);
        user.setAvatarUrl("https://img.touxiangwu.com/zb_users/upload/2022/10/202210311667198862146079.jpg");
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();

    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        //判断非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //账户：不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        }
        //密码：不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        //账户不能包含特殊字符，用正则表达式
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入正确的账号");
        }
        //校验密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((S_ALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);  //通过账号密码去数据库查数据
        if (user == null){
            //记录下日志
            log.info("\"Invalid username or password\" ");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入的账号或密码有误");
        }
        //对用户信息进行脱敏
       User safatyUser = getSafetyUser(user);
        //记录用户的登录态
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,safatyUser);
        //返回脱敏的数据
        return safatyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            return null;
        }
        User safatyUser = new User();
        safatyUser.setId(originUser.getId());
        safatyUser.setUsername(originUser.getUsername());
        safatyUser.setAvatarUrl(originUser.getAvatarUrl());
        safatyUser.setGender(originUser.getGender());
        safatyUser.setUserAccount(originUser.getUserAccount());
        safatyUser.setPhone(originUser.getPhone());
        safatyUser.setEmail(originUser.getEmail());
        safatyUser.setUserRole(originUser.getUserRole());
        safatyUser.setUserStatus(originUser.getUserStatus());
        safatyUser.setCreateTime(originUser.getCreateTime());
        safatyUser.setTags(originUser.getTags());
        safatyUser.setUserProfile(originUser.getUserProfile());
        return safatyUser;
    }

    /**
     * 注销用户登录态
     * @param httpServletRequest
     */
    @Override
    public int userLogout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 通过标签搜索用户（内存过滤）
     * @param tagNameList 标签名
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 1. 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
//        System.out.println(userList);
        Gson gson = new Gson();
        // 2. 在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 修改用户的信息
     *
     * @param user 用户返回的信息
     * @return
     */
    @Override
    public int updateUser(User user,User loginUser) {
        Long userId = user.getId();
        if (userId <= 0){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        if (!userId.equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"您不是本人和管理员无法修改");
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"无数据");
        }
        return userMapper.updateById(user);
    }

    @Override
    public int AdminUpdateUser(User user) {
        //判断id是否为空
        Long userId = user.getId();
        if (userId == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"无数据");
        }
       return userMapper.updateById(user);
    }

    /**
     * 获取当前用户信息
     * @param request 用户登录信息
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        return (User) userObj;
    }

    @Override
    //判断是否为管理员
    public boolean isAdmin(HttpServletRequest httpServletRequest){
        //判断是否为管理员
        Object userObj = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    //判断是否为管理员
    public boolean isAdmin(User longinUser){
        //判断是否为管理员
        return longinUser != null && longinUser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 用户匹配
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<UserVO> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId().equals(loginUser.getId())) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        List<User> userListFilter = this.list(userQueryWrapper);
        //过滤敏感数据
        UserVO[] userVOS = new UserVO[userListFilter.size()];
        for (int i = 0; i < userListFilter.size(); i++) {
            userVOS[i] = new UserVO();
            BeanUtils.copyProperties(userListFilter.toArray()[i],userVOS[i]);
        }
        List<UserVO> userVOList = Arrays.asList(userVOS);
        Map<Long, List<UserVO>> userIdUserListMap = userVOList.stream().collect(Collectors.groupingBy(UserVO::getId));
        List<UserVO> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

}




