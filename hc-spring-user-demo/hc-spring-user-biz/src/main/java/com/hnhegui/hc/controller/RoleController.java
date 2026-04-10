package com.hnhegui.hc.controller;

import com.hc.framework.web.model.Result;
import com.hnhegui.hc.dto.RoleRequest;
import com.hnhegui.hc.dto.RoleResponse;
import com.hnhegui.hc.service.RoleService;
import lombok.Getter;
import lombok.Setter;
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
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public Result<List<RoleResponse>> list() {
        List<RoleResponse> roles = roleService.listRoles();
        return Result.success(roles);
    }

    @PostMapping("/add")
    public Result<RoleResponse> add(@RequestBody RoleRequest roleRequest) {
        RoleResponse roleResponse = roleService.saveRole(roleRequest);
        return Result.success(roleResponse);
    }

    @PutMapping("/edit/{id}")
    public Result<RoleResponse> edit(@PathVariable Long id, @RequestBody RoleRequest roleRequest) {
        RoleResponse roleResponse = roleService.updateRole(id, roleRequest);
        return Result.success(roleResponse);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = roleService.deleteRole(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping("/get/{id}")
    public Result<RoleResponse> get(@PathVariable Long id) {
        RoleResponse roleResponse = roleService.getRoleById(id);
        return Result.success(roleResponse);
    }

    @PostMapping("/assign-permissions")
    public Result<Void> assignPermissions(@RequestBody AssignPermissionsRequest request) {
        boolean success = roleService.assignPermissions(request.getRoleId(), request.getPermissionIds());
        if (success) {
            return Result.success();
        } else {
            return Result.error("分配权限失败");
        }
    }

    @Setter
    @Getter
    static class AssignPermissionsRequest {
        private Long roleId;
        private List<Long> permissionIds;

    }
}