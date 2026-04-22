package com.hnhegui.hc.bo.enterprise;

import com.hc.framework.mybatis.model.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnterprisePageQueryBO extends PageParam {

    /**
     * 企业名称
     */
    private String name;

    /**
     * 企业编码
     */
    private String enterpriseCode;

    /**
     * 状态
     */
    private Integer status;
}
