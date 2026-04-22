package com.hnhegui.hc.controller.enterprise.request;

import lombok.Data;

@Data
public class EnterpriseUpdateRequest {

    /**
     * 企业名称
     */
    private String name;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 企业地址
     */
    private String address;
}
