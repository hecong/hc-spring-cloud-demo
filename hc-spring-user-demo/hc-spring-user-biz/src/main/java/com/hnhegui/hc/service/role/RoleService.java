package com.hnhegui.hc.service.role;

import com.hc.framework.mybatis.service.BaseService;
import com.hnhegui.hc.entity.role.Role;
import com.hnhegui.hc.controller.role.request.RoleRequest;
import com.hnhegui.hc.controller.role.response.RoleResponse;

import java.util.List;

public interface RoleService extends BaseService<Role> {
    /**
     * 根据用户ID获取角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleResponse> getRolesByUserId(Long userId);

    /**
     * 分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否分配成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 保存角色
     * @param roleRequest 角色请求参数
     * @return 角色响应参数
     */
    RoleResponse saveRole(RoleRequest roleRequest);

    /**
     * 更新角色
     * @param id 角色ID
     * @param roleRequest 角色请求参数
     * @return 角色响应参数
     */
    RoleResponse updateRole(Long id, RoleRequest roleRequest);

    /**
     * 删除角色
     * @param id 角色ID
     * @return 是否删除成功
     */
    boolean deleteRole(Long id);

    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色响应参数
     */
    RoleResponse getRoleById(Long id);

    /**
     * 获取角色列表
     * @return 角色响应参数列表
     */
    List<RoleResponse> listRoles();
}