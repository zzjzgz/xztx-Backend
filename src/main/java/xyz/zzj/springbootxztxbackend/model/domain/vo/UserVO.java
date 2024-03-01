package xyz.zzj.springbootxztxbackend.model.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.vo
 * @ClassName: UserVO
 * @Author: zengz
 * @CreateTime: 2024/2/6 10:01
 * @Description: 用户信息封装类
 * @Version: 1.0
 */

@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = -8283895406845945371L;
    /**
     * id
     */
    private Long id;

    /**
     * 个人简介
     */
    private String userProfile;

    /**
     * 昵称
     */
    private String username;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 0-表示正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 0-普通，1-管理员
     */
    private Integer userRole;

    /**
     * 标签列表json
     */
    private String tags;


}
