package xyz.zzj.springbootxztxbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.request
 * @ClassName: DeleteRequest
 * @Author: zengz
 * @CreateTime: 2024/2/8 20:28
 * @Description: 删除请求参数
 * @Version: 1.0
 */

@Data
public class DeleteRequest implements Serializable {
    private long id;
}
