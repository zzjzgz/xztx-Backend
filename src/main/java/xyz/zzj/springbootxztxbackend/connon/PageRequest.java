package xyz.zzj.springbootxztxbackend.connon;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.connon
 * @ClassName: PageRequest
 * @Author: zengz
 * @CreateTime: 2024/2/5 16:00
 * @Description: TODO 描述类的功能
 * @Version: 1.0
 */

@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 5226487869216774684L;
    /**
     * 页面大小
     */
    protected int pageSize;

    /**
     * 当前页数
     */
    protected int pageNum;

}
