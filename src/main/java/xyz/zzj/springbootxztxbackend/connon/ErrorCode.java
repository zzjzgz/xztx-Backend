package xyz.zzj.springbootxztxbackend.connon;

public enum ErrorCode {
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    PARAMS_NULL_ERROR(40001,"请求数据为空",""),
    LOGIN_ERROR(41000,"未登录",""),
    NO_AUTH_ERROR(40101,"无权限",""),
    SYSTEM_ERROR(50000,"系统内部异常","");


    /**
     * 状态码信息
     */
    private final int code;
    /**
     * 状态码描述(详细)
     */
    private final String message;
    /**
     * 状态码详情
     */
    private final String description;

    ErrorCode(int code, String massage, String description) {
        this.code = code;
        this.message = massage;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
