package com.hnhegui.hc.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hc.framework.mybatis.model.PageData;
import com.hc.framework.web.model.Result;
import com.hnhegui.hc.controller.user.converter.UserConverter;
import com.hnhegui.hc.controller.user.request.AssignRolesRequest;
import com.hnhegui.hc.controller.user.request.UserPageRequest;
import com.hnhegui.hc.controller.user.request.UserRequest;
import com.hnhegui.hc.controller.user.response.UserResponse;
import com.hnhegui.hc.service.user.UserRoleService;
import com.hnhegui.hc.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final UserRoleService userRoleService;


    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public Result<List<UserResponse>> list() {
        List<UserResponse> users = UserConverter.INSTANCE.toResponseList(userService.listUsers());
        return Result.success(users);
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public Result<UserResponse> add(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = UserConverter.INSTANCE.toResponse(userService.saveUser(UserConverter.INSTANCE.requestToCreateBo(userRequest)));
        return Result.success(userResponse);
    }

    /**
     * 编辑用户
     */
    @PutMapping("/edit/{id}")
    public Result<UserResponse> edit(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        UserResponse userResponse = UserConverter.INSTANCE.toResponse(userService.updateUser(id, UserConverter.INSTANCE.requestToCreateBo(userRequest)));
        return Result.success(userResponse);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 根据id获取用户
     */
    @GetMapping("/get/{id}")
    public Result<UserResponse> get(@PathVariable Long id) {
        UserResponse userResponse = UserConverter.INSTANCE.toResponse(userService.getUserById(id));
        return Result.success(userResponse);
    }

    /**
     * 分配角色
     */
    @PostMapping("/assign-roles")
    public Result<Void> assignRoles(@RequestBody AssignRolesRequest request) {
        boolean success = userRoleService.assignRoles(request.getUserId(), request.getRoleIds());
        if (success) {
            return Result.success();
        } else {
            return Result.error("分配角色失败");
        }
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<PageData<UserResponse>> page(@Validated UserPageRequest request) {
        Page<UserResponse> userResponsePage = UserConverter.INSTANCE.toResponsePage(userService.listUsersByPage(UserConverter.INSTANCE.requestToPageBo(request)));
        return Result.success(PageData.of(userResponsePage));
    }

}