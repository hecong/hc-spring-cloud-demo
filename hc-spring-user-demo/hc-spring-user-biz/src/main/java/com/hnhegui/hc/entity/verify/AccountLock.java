package com.hnhegui.hc.entity.verify;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_account_lock")
public class AccountLock extends BaseEntity {

    /**
     * 用户类型：C-C端，B-B端
     */
    private String userType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 锁定账号
     */
    private String account;

    /**
     * 锁定原因：password_error/abnormal_login
     */
    private String lockReason;

    /**
     * 预计解锁时间
     */
    private LocalDateTime unlockTime;

    /**
     * 状态：1-锁定中，2-已解锁
     */
    private Integer status;
}
