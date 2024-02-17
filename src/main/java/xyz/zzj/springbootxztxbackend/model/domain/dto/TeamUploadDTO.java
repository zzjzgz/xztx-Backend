package xyz.zzj.springbootxztxbackend.model.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.dto
 * @ClassName: TeamUploadDTO
 * @Author: zengz
 * @CreateTime: 2024/2/17 22:44
 * @Description: TODO 描述类的功能
 * @Version: 1.0
 */

@Data
public class TeamUploadDTO {
    String fileName;
    MultipartFile file;
}
