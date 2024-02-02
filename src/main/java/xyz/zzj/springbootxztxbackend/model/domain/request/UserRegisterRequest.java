package xyz.zzj.springbootxztxbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject: springboot-user-center
 * @BelongsPackage: xyz.zzj.springbootusercenter.model.registerGarameter
 * @Author: zengzhaojun
 * @CreateTime: 2024-01-09  15:53
 * @Description: TODO
 * @Version: 1.0
 */

@Data
public class UserRegisterRequest implements Serializable {

    //序列化id
    private static final long serialVersionUID = -2015983390376304248L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
}

