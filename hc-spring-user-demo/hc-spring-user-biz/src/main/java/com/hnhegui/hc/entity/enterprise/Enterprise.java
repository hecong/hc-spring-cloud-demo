package com.hnhegui.hc.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_enterprise")
public class Enterprise extends BaseEntity {

    /**
     * 企业编码(8位数字+字母)
     */
    private String enterpriseCode;

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
     * 状态：1-正常，2-过期，3-禁用
     */
    private Integer status;

    /**
     * 有效期
     */
    private LocalDate validDate;

    /**
     * IP白名单(JSON数组)
     */
    private String ipWhitelist;

    /**
     * 是否开启登录互踢：0-否，1-是
     */
    private Integer loginMutualExclusion;

    /**
     * 登录密码规则(JSON配置)
     */
    private String passwordRule;
}
