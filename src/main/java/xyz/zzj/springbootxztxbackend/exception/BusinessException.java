package xyz.zzj.springbootxztxbackend.exception;

import xyz.zzj.springbootxztxbackend.connon.ErrorCode;

/**
 * @BelongsProject: springboot-user-center
 * @BelongsPackage: xyz.zzj.springbootusercenter.exception
 * @Author: zengzhaojun
 * @CreateTime: 2024-01-13  11:26
 * @Description: TODO
 * @Version: 1.0
 */
public class BusinessException extends RuntimeException{
    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

