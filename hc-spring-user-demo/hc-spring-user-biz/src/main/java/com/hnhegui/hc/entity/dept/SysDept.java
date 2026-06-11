package com.hnhegui.hc.entity.dept;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体
 *
 * @author hecong
 * @since 2026/6/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /** 父部门ID（0=顶级部门） */
    private Long parentId;

    /** 部门名称 */
    private String deptName;

    /** 祖级ID列表（逗号分隔，如 0,1,5，用于快速查询子部门） */
    private String ancestors;

    /** 排序 */
    private Integer sortOrder;

    /** 状态（0=禁用，1=正常） */
    private Integer status;

    /** 企业ID（租户隔离） */
    private Long enterpriseId;
}
