package xyz.zzj.springbootxztxbackend.model.domain.dto;

import lombok.Data;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.model.domain.dto
 * @ClassName: NearbyUserDto
 * @Author: zengz
 * @CreateTime: 2024/4/21 20:43
 * @Description: 用户传回的经纬度
 * @Version: 1.0
 */
@Data
public class NearbyUserDTO {
    /**
     * 纬度
     */
    private Double latitude;

    private Double longitude;
}
