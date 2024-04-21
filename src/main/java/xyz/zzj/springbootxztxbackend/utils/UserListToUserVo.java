package xyz.zzj.springbootxztxbackend.utils;

import org.springframework.beans.BeanUtils;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.vo.UserVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.utils
 * @ClassName: UserListToUserVo
 * @Author: zengz
 * @CreateTime: 2024/4/18 9:58
 * @Description: TODO 描述类的功能
 * @Version: 1.0
 */
public class UserListToUserVo {
    /**
     * 对List《user》脱敏
     * @param userPage
     * @return
     */
    public static List<UserVO> getUserVOList(List<User> userPage) {
        List<UserVO> userVOList = new ArrayList<>();
        for (User user : userPage){
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user,userVO);
            userVOList.add(userVO);
        }
        return userVOList;
    }
}
