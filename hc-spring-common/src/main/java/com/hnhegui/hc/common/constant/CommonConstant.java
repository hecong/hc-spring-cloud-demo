package com.hnhegui.hc.common.constant;

/**
 * 通用常量定义
 */
public final class CommonConstant {

    private CommonConstant() {
        // 禁止实例化
    }

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页条数
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页条数
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 失败状态码
     */
    public static final int FAIL_CODE = 500;

    /**
     * 请求头 - 用户ID
     */
    public static final String HEADER_USER_ID = "X-User-Id";

    /**
     * 请求头 - 用户名
     */
    public static final String HEADER_USER_NAME = "X-User-Name";

    /**
     * 请求头 - 租户ID
     */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    /**
     * 用户上下文
     */
    public static final String USER_CONTEXT = "userContext";

}
