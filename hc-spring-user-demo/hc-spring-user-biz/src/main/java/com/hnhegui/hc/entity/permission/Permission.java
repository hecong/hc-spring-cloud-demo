package com.hnhegui.hc.entity.permission;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_permission")
public class Permission extends BaseEntity {

    /**
     * 权限名(菜单名称)
     */
    private String name;
    /**
     * 权限编码
     */
    private String code;
    /**
     * 权限类型
     */
    private String type;
    /**
     * 路由路径
     */
    private String path;
    /**
     * 父权限ID
     */
    private Long parentId;

}