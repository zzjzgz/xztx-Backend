package xyz.zzj.springbootxztxbackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 个人简介
     */
    private String userProfile;

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
     * 密码
     */
    private String userPassword;

    /**
     * 昵称
     */
    private String username;

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
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 0-普通，1-管理员
     */
    private Integer userRole;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 电话
     */
    private String phone;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}