package com.hnhegui.hc.controller.cuser.response;

import lombok.Data;

@Data
public class CUserThirdPartyResponse {

    /**
     * ID
     */
    private Long id;

    /**
     * 第三方平台
     */
    private String platform;

    /**
     * 第三方昵称
     */
    private String bindNickname;

    /**
     * 第三方头像
     */
    private String bindAvatar;
}
