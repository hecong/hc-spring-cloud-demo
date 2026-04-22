package com.hnhegui.hc.controller.auth.response;

import lombok.Data;

@Data
public class CheckEnterpriseResponse {

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 企业编码
     */
    private String enterpriseCode;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 状态
     */
    private Integer status;
}
