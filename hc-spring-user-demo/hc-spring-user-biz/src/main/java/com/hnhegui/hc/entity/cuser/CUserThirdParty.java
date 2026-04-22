package com.hnhegui.hc.entity.cuser;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("c_user_third_party")
public class CUserThirdParty extends BaseEntity {

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
