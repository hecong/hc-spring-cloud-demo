package com.hnhegui.hc.entity.role;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色-权限数据范围实体
 *
 * <p>每个 (role_id, permission_id) 对应一个数据范围，
 * 不同菜单可以有不同的数据权限。</p>
 *
 * @author hecong
 * @since 2026/6/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role_permission_data_scope")
public class SysRolePermDataScope extends BaseEntity {

    /** 角色ID */
    private Long roleId;

    /** 菜单/权限ID（关联 sys_permission.id） */
    private Long permissionId;

    /** 数据范围：ALL|DEPT_AND_CHILDREN|CUSTOM_DEPT|CURRENT_DEPT|SELF */
    private String dataScope;

    /** 企业ID（租户隔离） */
    private Long enterpriseId;
}
