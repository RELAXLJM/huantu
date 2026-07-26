package com.huantu.common;

/**
 * 统一状态码枚举
 */
public enum ResultCode {

    // ==================== 通用状态码 ====================
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // ==================== 业务状态码 ====================
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    PHONE_ALREADY_REGISTERED(1003, "该手机号已注册"),
    SMS_CODE_ERROR(1004, "验证码错误"),
    SMS_CODE_EXPIRED(1005, "验证码已过期"),
    SESSION_EXPIRED(1006, "会话已过期，请重新登录"),

    ROUTE_NOT_FOUND(2001, "路线不存在"),
    ROUTE_PERMISSION_DENIED(2002, "无权操作此路线"),
    SCENIC_NOT_FOUND(2003, "景点不存在"),

    POST_NOT_FOUND(3001, "帖子不存在"),
    POST_PERMISSION_DENIED(3002, "无权操作此帖子"),
    COMMENT_TOO_FREQUENT(3003, "操作过于频繁，请稍后再试"),

    FAVORITE_ALREADY_EXISTS(4001, "已经收藏过了"),
    FAVORITE_NOT_FOUND(4002, "收藏记录不存在"),

    FILE_UPLOAD_FAILED(5001, "文件上传失败"),
    FILE_TOO_LARGE(5002, "文件大小超出限制"),
    FILE_TYPE_NOT_SUPPORTED(5003, "不支持的文件类型"),

    AI_GENERATE_FAILED(6001, "AI路线生成失败，请稍后重试"),
    AI_SERVICE_UNAVAILABLE(6002, "AI服务暂不可用"),

    THIRD_PARTY_ERROR(7001, "第三方服务调用失败"),
    RATE_LIMIT_EXCEEDED(7002, "请求过于频繁，请稍后再试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
