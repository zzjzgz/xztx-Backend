package xyz.zzj.springbootxztxbackend.model.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.zzj.springbootxztxbackend.connon.PageRequest;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.dto
 * @ClassName: TeamQueryDTO
 * @Author: zengz
 * @CreateTime: 2024/2/5 11:38
 * @Description: 队伍查询封装类
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQueryDTO extends PageRequest {
    /**
     * id
     */
    private long id;

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
     * 用户id（队伍id）
     */
    private long userId;

    /**
     * 0-公开，1-私有，3-加密
     */
    private Integer status;
}
