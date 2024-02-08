package xyz.zzj.springbootxztxbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.request
 * @ClassName: TeamAddRequest
 * @Author: zengz
 * @CreateTime: 2024/2/5 20:16
 * @Description: 队伍新增请求体
 * @Version: 1.0
 */
@Data
public class TeamAddRequest implements Serializable {

    private static final long serialVersionUID = -914015194055759414L;
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
     * 0-公开，1-私有，3-加密
     */
    private Integer status;

    /**
     * 队伍密码
     */
    private String password;

    /**
     * 队伍头像
     */
    private String teamAvatarUrl;
}
