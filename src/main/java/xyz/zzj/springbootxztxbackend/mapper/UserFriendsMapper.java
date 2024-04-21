package xyz.zzj.springbootxztxbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xyz.zzj.springbootxztxbackend.model.domain.UserFriends;

import java.util.List;

/**
* @author zengz
* @description 针对表【user_friends】的数据库操作Mapper
* @createDate 2024-04-18 09:39:41
* @Entity xyz.zzj.springbootxztxbackend.model.domain.UserFriends
*/
public interface UserFriendsMapper extends BaseMapper<UserFriends> {

    @Select("select friendId from user_friends where userId = #{userId} and isDelete = 0 and relationshipStatus = #{relationshipStatus}")
    List<Long> getFriends(@Param("userId") Long userId,@Param("relationshipStatus") Integer relationshipStatus);

    @Select("select userId from user_friends where friendId = #{friendId} and isDelete = 0 and relationshipStatus = #{relationshipStatus}")
    List<Long> getApplyFriends(@Param("friendId") Long friendId,@Param("relationshipStatus") Integer relationshipStatus);

    int deleteFriendship(long userId);
}




