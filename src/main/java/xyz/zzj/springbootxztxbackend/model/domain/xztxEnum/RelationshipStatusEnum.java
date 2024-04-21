package xyz.zzj.springbootxztxbackend.model.domain.xztxEnum;

/**
 * @BelongsProject: misc.xml
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.xztxEnum
 * @Author: zengz
 * @CreateTime: 2024/4/20 19:24
 * @Description: 好友状态枚举值
 * @Version: 1.0
 */
public enum RelationshipStatusEnum {
    APPLY(0,"申请中"),
    SUCCESS(1,"通过"),
    REFUSE(2,"拒绝");
    /**
     * 状态值
     */
    private Integer status;
    /**
     * 状态描述
     */
    private String description;


    //构造器
    RelationshipStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
