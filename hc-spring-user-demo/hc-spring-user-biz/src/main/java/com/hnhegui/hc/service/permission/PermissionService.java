package com.hnhegui.hc.service.permission;

import com.hc.framework.mybatis.service.BaseService;
import com.hnhegui.hc.entity.permission.Permission;
import com.hnhegui.hc.controller.permission.request.PermissionRequest;
import com.hnhegui.hc.controller.permission.response.PermissionResponse;

import java.util.List;

public interface PermissionService extends BaseService<Permission> {

    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionResponse> getPermissionsByRoleId(Long roleId);

    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<PermissionResponse> getPermissionsByUserId(Long userId);

    /**
     * 保存权限
     *
     * @param permissionRequest 权限请求参数
     * @return 权限响应参数
     */
    PermissionResponse savePermission(PermissionRequest permissionRequest);

    /**
     * 更新权限
     *
     * @param id                权限ID
     * @param permissionRequest 权限请求参数
     * @return 权限响应参数
     */
    PermissionResponse updatePermission(Long id, PermissionRequest permissionRequest);

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否删除成功
     */
    boolean deletePermission(Long id);

    /**
     * 根据ID获取权限
     *
     * @param id 权限ID
     * @return 权限响应参数
     */
    PermissionResponse getPermissionById(Long id);

    /**
     * 获取所有权限列表
     *
     * @return 权限响应参数列表
     */
    List<PermissionResponse> listPermissions();

    /**
     * 初始化动态鉴权路由缓存
     */
    void initDynamicAuthRouteCache();
}