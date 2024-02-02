package xyz.zzj.springbootxztxbackend.service;

import xyz.zzj.springbootxztxbackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author zeng
* @description 针对表【user】的数据库操作Service
* @createDate 2024-01-08 16:50:47
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 账户
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return 新用户的id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount        用户的账号
     * @param userPassword       用户的密码
     * @param httpServletRequest 保存用户的登录态
     * @return 返回用户的信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    /**
     * 用户信息脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 请求用户注销
     * @param httpServletRequest
     * @return
     */
    int userLogout(HttpServletRequest httpServletRequest);

    /**
     * 通过标签搜索用户
     * @param tagNameList 标签名
     * @return
     */
    List<User> searchUserByTags(List<String> tagNameList);

    /**
     * 用户自己修改用户信息
     *
     * @param user 用户返回的信息
     * @return
     */
    int updateUser(User user,User loginUser);

    /**
     * 管理员修改用户信息
     *
     * @param user 用户返回的信息
     * @return
     */
    int AdminUpdateUser(User user);

    /**
     * 获取当前用户信息
     * @param request 用户登录信息
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param longinUser 登录者的信息
     * @return
     */
    boolean isAdmin(User longinUser);
}
