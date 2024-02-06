package xyz.zzj.springbootxztxbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.request
 * @ClassName: TeamQuitRequest
 * @Author: zengz
 * @CreateTime: 2024/2/6 17:40
 * @Description: 用户退出队伍请求体
 * @Version: 1.0
 */

@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -4708068283623642064L;
    /**
     * 队伍id
     */
    private Long teamId;

}
