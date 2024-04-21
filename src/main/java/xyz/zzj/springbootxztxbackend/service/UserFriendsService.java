package xyz.zzj.springbootxztxbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.UserFriends;
import xyz.zzj.springbootxztxbackend.model.domain.vo.UserVO;

import java.util.List;

/**
* @author zengz
* @description 针对表【user_friends】的数据库操作Service
* @createDate 2024-04-18 09:22:18
*/
public interface UserFriendsService extends IService<UserFriends> {

    List<UserVO> getFriends(String name ,User loginUser,Integer relationshipStatus);

    int addFriends(Long userId, long friendId);

    boolean deleteFriend(long id);

    Long getApplyFriendNum(Long userId);

    List<UserVO> getApplyFriends(User loginUser, int relationshipStatus);

    boolean agreeFriend(long userId,long friendId);

    boolean refuseFriend(long userId, Long friendId);

}
