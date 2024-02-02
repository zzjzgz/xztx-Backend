package xyz.zzj.springbootxztxbackend.connon;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject: springboot-user-center
 * @BelongsPackage: xyz.zzj.springbootusercenter.connon
 * @Author: zengzhaojun
 * @CreateTime: 2024-01-13  10:22
 * @Description: TODO
 * @Version: 1.0
 */

/**
 * 通用返回类
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    //业务状态码
    private int code;

    //接收返回的信息
    private T data;

    //返回状态码信息信息
    private String message;

    //返回状态码的错误详细信息
    private String description;

    public BaseResponse(int code, T data, String massage,String description) {
        this.code = code;
        this.data = data;
        this.message = massage;
        this.description = description;
    }

    public BaseResponse(int code, T data, String massage) {
        this.code = code;
        this.data = data;
        this.message = massage;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
        this.message = "";
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }

}

