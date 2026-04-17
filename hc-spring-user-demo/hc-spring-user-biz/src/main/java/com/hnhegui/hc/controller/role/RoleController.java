package com.hnhegui.hc.controller.role;

import com.hc.framework.web.model.Result;
import com.hnhegui.hc.controller.role.request.AssignPermissionsRequest;
import com.hnhegui.hc.controller.role.request.RoleRequest;
import com.hnhegui.hc.controller.role.response.RoleResponse;
import com.hnhegui.hc.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    /**
     * 获取角色列表
     * @return 角色列表
     */
    @GetMapping("/list")
    public Result<List<RoleResponse>> list() {
        List<RoleResponse> roles = roleService.listRoles();
        return Result.success(roles);
    }

    /**
     * 添加角色
     * @param roleRequest 角色信息
     * @return 添加后的角色信息
     */
    @PostMapping("/add")
    public Result<RoleResponse> add(@RequestBody RoleRequest roleRequest) {
        RoleResponse roleResponse = roleService.saveRole(roleRequest);
        return Result.success(roleResponse);
    }

    /**
     * 编辑角色
     * @param id 角色ID
     * @param roleRequest 角色信息
     * @return 编辑后的角色信息
     */
    @PutMapping("/edit/{id}")
    public Result<RoleResponse> edit(@PathVariable Long id, @RequestBody RoleRequest roleRequest) {
        RoleResponse roleResponse = roleService.updateRole(id, roleRequest);
        return Result.success(roleResponse);
    }

    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = roleService.deleteRole(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 获取角色详情
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/get/{id}")
    public Result<RoleResponse> get(@PathVariable Long id) {
        RoleResponse roleResponse = roleService.getRoleById(id);
        return Result.success(roleResponse);
    }

    /**
     * 分配权限
     * @param request 分配权限请求
     * @return 分配结果
     */
    @PostMapping("/assign-permissions")
    public Result<Void> assignPermissions(@RequestBody AssignPermissionsRequest request) {
        boolean success = roleService.assignPermissions(request.getRoleId(), request.getPermissionIds());
        if (success) {
            return Result.success();
        } else {
            return Result.error("分配权限失败");
        }
    }


}