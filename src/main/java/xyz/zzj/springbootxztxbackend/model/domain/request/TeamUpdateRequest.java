package xyz.zzj.springbootxztxbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.request
 * @ClassName: TeamUpdateRequest
 * @Author: zengz
 * @CreateTime: 2024/2/6 12:47
 * @Description: TODO 描述类的功能
 * @Version: 1.0
 */

@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -343116751069062892L;
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
}
