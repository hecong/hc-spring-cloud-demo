package com.hnhegui.hc.controller.cuser.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;
}
