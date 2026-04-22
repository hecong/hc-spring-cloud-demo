package com.hnhegui.hc.controller.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckEnterpriseRequest {

    /**
     * 企业编码
     */
    @NotBlank(message = "企业编码不能为空")
    private String enterpriseCode;
}
