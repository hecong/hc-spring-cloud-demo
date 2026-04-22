package com.hnhegui.hc.bo.enterprise;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EnterpriseCreateBO {

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

    /**
     * 有效期
     */
    private LocalDate validDate;

    /**
     * IP白名单
     */
    private String ipWhitelist;

    /**
     * 是否开启登录互踢：0-否，1-是
     */
    private Integer loginMutualExclusion;

    /**
     * 登录密码规则
     */
    private String passwordRule;
}
