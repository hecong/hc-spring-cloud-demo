package com.hnhegui.hc.entity.verify;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_verification_code")
public class VerificationCode extends BaseEntity {

    /**
     * 发送目标(手机号/邮箱)
     */
    private String target;

    /**
     * 验证码
     */
    private String code;

    /**
     * 场景：login/register/reset
     */
    private String scene;

    /**
     * 状态：1-有效，2-已使用，3-已过期
     */
    private Integer status;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
