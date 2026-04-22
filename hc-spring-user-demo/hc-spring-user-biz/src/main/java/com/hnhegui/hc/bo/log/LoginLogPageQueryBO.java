package com.hnhegui.hc.bo.log;

import com.hc.framework.mybatis.model.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginLogPageQueryBO extends PageParam {

    /**
     * 用户类型：C/B/P
     */
    private String userType;

    /**
     * 用户ID
     */
    private Long userId;
}
