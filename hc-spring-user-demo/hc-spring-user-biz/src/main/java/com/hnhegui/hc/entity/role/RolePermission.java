package com.hnhegui.hc.entity.role;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role_permission")
public class RolePermission extends BaseEntity {

    private Long roleId;
    private Long permissionId;
}