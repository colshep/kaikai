package com.plunger.api;

public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    LOGIN_FAILED(401, "登录名或登录密码不正确"),
    FORBIDDEN(403, "没有相关权限"),
    VALIDATE_FAILED(404, "参数检验失败"),
    FAILED(500, "操作失败");

    private long code;
    private String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
