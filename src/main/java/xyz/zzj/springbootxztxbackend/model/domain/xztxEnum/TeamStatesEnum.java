package xyz.zzj.springbootxztxbackend.model.domain.xztxEnum;

/**
 * @BelongsProject: misc.xml
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.enumTeam
 * @Author: zengz
 * @CreateTime: 2024/2/5 17:40
 * @Description: TODO 描述类的功能
 * @Version: 1.0
 */

/**
 * 队伍状态枚举类
 */
public enum TeamStatesEnum {
    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密");

    /**
     * 状态
     */
    private  int value;

    /**
     * 描述
     */
    private String text;

    public static TeamStatesEnum getEnumTeam(Integer value){
        if (value == null){
            return null;
        }
        TeamStatesEnum[] values = TeamStatesEnum.values();
        for (TeamStatesEnum teamStatesEnum : values) {
            if (teamStatesEnum.getValue() == value){
                //找到枚举值则返回枚举值
                return teamStatesEnum;
            }
        }
        return null;
    }

    TeamStatesEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setText(String text) {
        this.text = text;
    }
}
