package com.hnhegui.hc.controller.user.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author hecong
 * @since 2026/4/20 16:50
 */
@Data
public class UserExportResponse {

    private Long id;
    @ExcelProperty("用户名")
    private String username;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("邮箱")
    private String email;
    @ExcelProperty("状态")
    private Integer status;
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;

}
