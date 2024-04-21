package xyz.zzj.springbootxztxbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import xyz.zzj.springbootxztxbackend.connon.ErrorCode;
import xyz.zzj.springbootxztxbackend.exception.BusinessException;
import xyz.zzj.springbootxztxbackend.mapper.UserFriendsMapper;
import xyz.zzj.springbootxztxbackend.mapper.UserMapper;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.UserFriends;
import xyz.zzj.springbootxztxbackend.model.domain.vo.UserVO;
import xyz.zzj.springbootxztxbackend.model.domain.xztxEnum.RelationshipStatusEnum;
import xyz.zzj.springbootxztxbackend.service.UserFriendsService;
import xyz.zzj.springbootxztxbackend.utils.UserListToUserVo;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
* @author zengz
* @description 针对表【user_friends】的数据库操作Service实现
* @createDate 2024-04-18 09:22:18
*/
@Service
public class UserFriendsServiceImpl extends ServiceImpl<UserFriendsMapper, UserFriends>
    implements UserFriendsService{

    @Resource
    private UserFriendsMapper userFriendsMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public List<UserVO> getFriends(String name ,User loginUser,Integer relationshipStatus) {
        //获取登录的用户id
        Long userId = loginUser.getId();
        //将用户id作为好友发起人进行查询好友id集合
        List<Long> userFriends = userFriendsMapper.getFriends(userId,relationshipStatus);
        if (CollectionUtils.isEmpty(userFriends)){
            return Collections.emptyList();
        }
        //过滤相同的数据
        HashSet<Long> hashSet = new HashSet<>(userFriends);
        userFriends.clear();
        userFriends.addAll(hashSet);
        //通过查询到的好友id去查询用户数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId,userFriends);
        if (name != null){
            queryWrapper.like(User::getUsername,name);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return UserListToUserVo.getUserVOList(userList);
    }

    @Override
    @Transactional
    public int addFriends(Long userId, long friendId) {
        UserFriends userFriends = new UserFriends();
        userFriends.setFriendId(friendId);
        userFriends.setUserId(userId);
        //判断是否是好友
        LambdaQueryWrapper<UserFriends> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFriends::getFriendId,friendId);
        queryWrapper.eq(UserFriends::getUserId,userId);
        Long count = userFriendsMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请勿重复加好友");
        }
        //插入数据，你作为发起人的插入数据
        int insert = userFriendsMapper.insert(userFriends);
        if (insert <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return insert;
    }

    @Override
    public boolean deleteFriend(long id) {
        int delete = userFriendsMapper.deleteFriendship(id);
        return delete > 0;
    }

    @Override
    public Long getApplyFriendNum(Long userId) {
        LambdaQueryWrapper<UserFriends> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //将用户id作为好友id查询，好友申请数量
        lambdaQueryWrapper.eq(UserFriends::getFriendId,userId);
        lambdaQueryWrapper.eq(UserFriends::getRelationshipStatus,0);
        return userFriendsMapper.selectCount(lambdaQueryWrapper);
    }

    @Override
    public List<UserVO> getApplyFriends(User loginUser, int relationshipStatus) {
        //获取登录的用户id
        Long userId = loginUser.getId();
        //将用户id作为好友id进行查询，被申请的用户id
        List<Long> userFriends = userFriendsMapper.getApplyFriends(userId,relationshipStatus);
        if (CollectionUtils.isEmpty(userFriends)){
            return Collections.emptyList();
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId,userFriends);
        List<User> userList = userMapper.selectList(queryWrapper);
        return UserListToUserVo.getUserVOList(userList);
    }

    @Override
    @Transactional
    public boolean agreeFriend(long userId,long friendId) {
        LambdaUpdateWrapper<UserFriends> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        //将状态改为通过
        lambdaUpdateWrapper.eq(UserFriends::getUserId,userId)
                .set(UserFriends::getRelationshipStatus, RelationshipStatusEnum.SUCCESS.getStatus());
        boolean update = this.update(lambdaUpdateWrapper);
        if (!update){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"更新失败");
        }
        //插入一条数据进行双向绑定
        UserFriends userFriends = new UserFriends();
        userFriends.setFriendId(userId);
        userFriends.setUserId(friendId);
        userFriends.setRelationshipStatus(RelationshipStatusEnum.SUCCESS.getStatus());
        return userFriendsMapper.insert(userFriends) > 0;
    }

    @Override
    public boolean refuseFriend(long userId, Long friendId) {
        UserFriends userFriends = new UserFriends();
        LambdaUpdateWrapper<UserFriends> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(UserFriends::getUserId,userId);
        lambdaUpdateWrapper.eq(UserFriends::getFriendId,friendId);
        userFriends.setRelationshipStatus(RelationshipStatusEnum.REFUSE.getStatus());
        return userFriendsMapper.update(userFriends,lambdaUpdateWrapper) > 0;
    }
}




