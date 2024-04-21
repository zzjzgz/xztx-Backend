package xyz.zzj.springbootxztxbackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user_friends
 */
@TableName(value ="user_friends")
@Data
public class UserFriends implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 好友发起人id（主id）
     */
    private Long userId;

    /**
     * 好友接收人id（好友id）
     */
    private Long friendId;

    /**
     * 好友状态--0 申请中，1 通过，2 未通过
     */
    private Integer relationshipStatus;

    /**
     * 删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}