package xyz.zzj.springbootxztxbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.request
 * @ClassName: teamJoinRequest
 * @Author: zengz
 * @CreateTime: 2024/2/6 15:00
 * @Description: 加入队伍的信息
 * @Version: 1.0
 */
@Data
public class TeamJoinRequest implements Serializable {
    /**
     * id
     */
    private Long teamId;
    /**
     * 队伍密码
     */
    private String teamPassword;
}
