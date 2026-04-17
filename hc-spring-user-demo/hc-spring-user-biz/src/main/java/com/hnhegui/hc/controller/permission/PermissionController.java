package com.hnhegui.hc.controller.permission;

import com.hc.framework.web.model.Result;
import com.hnhegui.hc.controller.permission.request.PermissionRequest;
import com.hnhegui.hc.controller.permission.response.PermissionResponse;
import com.hnhegui.hc.service.permission.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    /**
     * 获取权限列表
     */
    @GetMapping("/list")
    public Result<List<PermissionResponse>> list() {
        List<PermissionResponse> permissions = permissionService.listPermissions();
        return Result.success(permissions);
    }

    /**
     * 添加权限
     */
    @PostMapping("/add")
    public Result<PermissionResponse> add(@RequestBody PermissionRequest permissionRequest) {
        PermissionResponse permissionResponse = permissionService.savePermission(permissionRequest);
        return Result.success(permissionResponse);
    }
    /**
     * 编辑权限
     */

    @PutMapping("/edit/{id}")
    public Result<PermissionResponse> edit(@PathVariable Long id, @RequestBody PermissionRequest permissionRequest) {
        PermissionResponse permissionResponse = permissionService.updatePermission(id, permissionRequest);
        return Result.success(permissionResponse);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = permissionService.deletePermission(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 根据id获取权限
     */
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