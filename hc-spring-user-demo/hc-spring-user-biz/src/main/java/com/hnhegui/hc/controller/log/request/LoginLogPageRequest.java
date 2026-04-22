package com.hnhegui.hc.controller.log.request;

import com.hc.framework.mybatis.model.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginLogPageRequest extends PageParam {

    /**
     * 用户类型：C/B/P
     */
    private String userType;

    /**
     * 用户ID
     */
    private Long userId;
}
