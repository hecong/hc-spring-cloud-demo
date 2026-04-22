package com.hnhegui.hc.bo.cuser;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CUserThirdPartyBO {

    /**
     * ID
     */
    private Long id;

    /**
     * C端用户ID
     */
    private Long userId;

    /**
     * 第三方平台：wechat/alipay/qq
     */
    private String platform;

    /**
     * 第三方openId
     */
    private String openId;

    /**
     * 第三方unionId
     */
    private String unionId;

    /**
     * 第三方昵称
     */
    private String bindNickname;

    /**
     * 第三方头像
     */
    private String bindAvatar;
}
