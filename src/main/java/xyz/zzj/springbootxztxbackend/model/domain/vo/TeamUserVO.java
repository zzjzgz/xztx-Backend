package xyz.zzj.springbootxztxbackend.model.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.vo
 * @ClassName: teamUserVO
 * @Author: zengz
 * @CreateTime: 2024/2/6 9:58
 * @Description: 队伍和用户信息的封装类
 * @Version: 1.0
 */

@Data
public class TeamUserVO implements Serializable {
    private static final long serialVersionUID = -5278008598557169798L;
    /**
     * 队伍id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String teamDescription;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id（队长id）
     */
    private long userId;

    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 队伍密码
     */
    private String teamAvatarUrl;

    /**
     * 创建人用户信息
     */
    private UserVO createUser;

    /**
     * 是否已加入
     */
    private boolean hasJoin;

}
