package com.hnhegui.hc.entity.role;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role")
public class Role extends BaseEntity {

    /**
     * 角色名
     */
    private String name;
    /**
     * 角色编码
     */
    private String code;
    /**
     * 描述
     */
    private String description;

    /**
     * 企业ID，NULL表示平台角色
     */
    private Long enterpriseId;
}