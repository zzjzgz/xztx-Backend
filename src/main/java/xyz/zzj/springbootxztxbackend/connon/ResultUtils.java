package xyz.zzj.springbootxztxbackend.connon;

/**
 * @BelongsProject: springboot-user-center
 * @BelongsPackage: xyz.zzj.springbootusercenter.connon
 * @Author: zengzhaojun
 * @CreateTime: 2024-01-13  10:31
 * @Description: TODO
 * @Version: 1.0
 */

//通用返回类
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static BaseResponse error(ErrorCode errorCode,String massage,String description){
        return new BaseResponse<>(errorCode.getCode(),massage,description);
    }

    public static BaseResponse error(ErrorCode errorCode,String description){
        return new BaseResponse<>(errorCode.getCode(),description);
    }

    public static BaseResponse error(int code,String massage,String description){
        return new BaseResponse<>(code,null,massage,description);
    }

}

