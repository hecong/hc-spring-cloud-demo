package com.hnhegui.hc.controller.enterprise.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnterpriseCreateRequest {

    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
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
}
