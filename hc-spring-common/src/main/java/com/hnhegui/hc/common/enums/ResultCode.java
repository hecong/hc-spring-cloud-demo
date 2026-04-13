package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 响应结果枚举
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAIL(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方式错误
     */
    METHOD_NOT_ALLOWED(405, "请求方式错误"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统错误"),

    /**
     * 业务错误
     */
    BUSINESS_ERROR(501, "业务错误"),

    /**
     * 数据已存在
     */
    DATA_EXISTS(502, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXISTS(503, "数据不存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
