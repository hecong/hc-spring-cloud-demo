package com.hnhegui.hc.bo.enterprise;

import com.hc.framework.mybatis.model.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnterpriseUserPageQueryBO extends PageParam {

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 姓名
     */
    private String name;

    /**
     * 状态
     */
    private Integer status;
}
