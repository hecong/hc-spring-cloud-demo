package com.hnhegui.hc.bo.user;

import com.hc.framework.mybatis.model.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hecong
 * @since 2026/4/17 16:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageQueryBO extends PageParam {

    /**
     * 用户名
     */
    private String username;
    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String phone;
}
