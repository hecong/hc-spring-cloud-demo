package com.hnhegui.hc.controller;

import com.hc.framework.web.model.Result;
import com.hnhegui.hc.dto.PermissionRequest;
import com.hnhegui.hc.dto.PermissionResponse;
import com.hnhegui.hc.service.PermissionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/list")
    public Result<List<PermissionResponse>> list() {
        List<PermissionResponse> permissions = permissionService.listPermissions();
        return Result.success(permissions);
    }

    @PostMapping("/add")
    public Result<PermissionResponse> add(@RequestBody PermissionRequest permissionRequest) {
        PermissionResponse permissionResponse = permissionService.savePermission(permissionRequest);
        return Result.success(permissionResponse);
    }

    @PutMapping("/edit/{id}")
    public Result<PermissionResponse> edit(@PathVariable Long id, @RequestBody PermissionRequest permissionRequest) {
        PermissionResponse permissionResponse = permissionService.updatePermission(id, permissionRequest);
        return Result.success(permissionResponse);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = permissionService.deletePermission(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping("/get/{id}")
    public Result<PermissionResponse> get(@PathVariable Long id) {
        PermissionResponse permissionResponse = permissionService.getPermissionById(id);
        return Result.success(permissionResponse);
    }

    /**
     * 根据菜单初始化路由权限缓存
     */
    @PostMapping("/init")
    public Result<Void> initDynamicAuthRouteCache(){
        permissionService.initDynamicAuthRouteCache();
        return Result.success();
    }
}