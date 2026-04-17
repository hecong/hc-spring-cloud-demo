package com.hnhegui.hc.controller.permission.converter;

import com.hnhegui.hc.entity.permission.Permission;
import com.hnhegui.hc.controller.permission.request.PermissionRequest;
import com.hnhegui.hc.controller.permission.response.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PermissionConverter {
    PermissionConverter INSTANCE = Mappers.getMapper(PermissionConverter.class);

    /**
     * 转换权限实体
     *
     * @param permissionRequest 权限请求
     * @return 权限实体
     */
    Permission toEntity(PermissionRequest permissionRequest);

    /**
     * 转换权限响应
     *
     * @param permission 权限
     * @return 权限响应
     */
    PermissionResponse toResponse(Permission permission);

    /**
     * 转换权限列表
     *
     * @param permissions 权限列表
     * @return 权限响应列表
     */
    List<PermissionResponse> toResponseList(List<Permission> permissions);
}