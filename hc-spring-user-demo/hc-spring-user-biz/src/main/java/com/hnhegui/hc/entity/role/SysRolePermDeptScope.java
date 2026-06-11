package com.hnhegui.hc.entity.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色-权限自定义部门范围明细
 *
 * <p>仅在 data_scope=CUSTOM_DEPT 时使用，存储该角色-权限对应的指定部门ID。</p>
 *
 * @author hecong
 * @since 2026/6/11
 */
@Data
@TableName("sys_role_perm_dept_scope")
public class SysRolePermDeptScope {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 关联 sys_role_permission_data_scope.id */
    private Long scopeId;

    /** 指定可访问的部门ID */
    private Long deptId;
}
