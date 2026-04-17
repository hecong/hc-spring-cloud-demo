package com.hnhegui.hc.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user_role")
public class UserRole extends BaseEntity {
    private Long userId;
    private Long roleId;
}