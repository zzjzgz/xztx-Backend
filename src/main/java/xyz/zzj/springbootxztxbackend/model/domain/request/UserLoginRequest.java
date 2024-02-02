package xyz.zzj.springbootxztxbackend.model.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @BelongsProject: springboot-user-center
 * @BelongsPackage: xyz.zzj.springbootusercenter.model.request
 * @Author: zengzhaojun
 * @CreateTime: 2024-01-09  16:07
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest implements Serializable {

    //序列化id
    private static final long serialVersionUID = -2015983390376304248L;

    private String userAccount;
    private String userPassword;
}

