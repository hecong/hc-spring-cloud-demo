package com.hnhegui.hc.controller;

import com.hc.framework.web.model.Result;
import com.hnhegui.hc.dto.UserRequest;
import com.hnhegui.hc.dto.UserResponse;
import com.hnhegui.hc.service.UserService;
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
@RequestMapping("/sys/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public Result<List<UserResponse>> list() {
        List<UserResponse> users = userService.listUsers();
        return Result.success(users);
    }

    @PostMapping("/add")
    public Result<UserResponse> add(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.saveUser(userRequest);
        return Result.success(userResponse);
    }

    @PutMapping("/edit/{id}")
    public Result<UserResponse> edit(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.updateUser(id, userRequest);
        return Result.success(userResponse);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping("/get/{id}")
    public Result<UserResponse> get(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return Result.success(userResponse);
    }

    @PostMapping("/assign-roles")
    public Result<Void> assignRoles(@RequestBody AssignRolesRequest request) {
        boolean success = userService.assignRoles(request.getUserId(), request.getRoleIds());
        if (success) {
            return Result.success();
        } else {
            return Result.error("分配角色失败");
        }
    }

    @Setter
    @Getter
    static class AssignRolesRequest {
        private Long userId;
        private List<Long> roleIds;

    }
}