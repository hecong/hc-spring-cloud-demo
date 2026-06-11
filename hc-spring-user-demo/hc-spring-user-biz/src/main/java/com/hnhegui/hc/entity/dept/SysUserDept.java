package com.hnhegui.hc.entity.dept;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户-部门关联实体（多对多）
 *
 * @author hecong
 * @since 2026/6/11
 */
@Data
@TableName("sys_user_dept")
public class SysUserDept {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 企业ID（租户隔离） */
    private Long enterpriseId;
}
