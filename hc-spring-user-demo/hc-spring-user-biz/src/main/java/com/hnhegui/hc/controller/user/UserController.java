package com.hnhegui.hc.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hc.framework.excel.model.ExcelExportRequest;
import com.hc.framework.excel.model.ExcelTaskStatus;
import com.hc.framework.excel.service.ExcelExportService;
import com.hc.framework.mybatis.model.PageData;
import com.hc.framework.web.model.Result;
import com.hnhegui.hc.bo.user.UserBO;
import com.hnhegui.hc.controller.user.converter.UserConverter;
import com.hnhegui.hc.controller.user.request.AssignRolesRequest;
import com.hnhegui.hc.controller.user.request.UserPageRequest;
import com.hnhegui.hc.controller.user.request.UserRequest;
import com.hnhegui.hc.controller.user.response.UserExportResponse;
import com.hnhegui.hc.controller.user.response.UserResponse;
import com.hnhegui.hc.service.user.UserRoleService;
import com.hnhegui.hc.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final UserRoleService userRoleService;

    private final ExcelExportService excelExportService;


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


    /**
     * 同步导出用户列表
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        ExcelExportRequest req = ExcelExportRequest.builder()
            .fileName("用户列表")
            .sheetName("Sheet1")
            .build();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(req.getFileName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        List<UserBO> userBOS = userService.listUsers();
        List<UserExportResponse> userExportResponses = UserConverter.INSTANCE.toExportResponseList(userBOS);
        excelExportService.exportData(req, userExportResponses, UserExportResponse.class, response.getOutputStream());
    }

    /**
     * 异步导出用户列表 - 创建导出任务（带进度回调）
     */
    @PostMapping("/export-async")
    public Result<String> exportAsync() {
        ExcelExportRequest req = ExcelExportRequest.builder()
            .fileName("用户列表")
            .sheetName("Sheet1")
            .build();

        // 使用 Supplier 延迟加载数据，避免在提交任务时立即查询
        // 进度回调会在每批数据写入时被触发，参数为当前已处理的数据条数
        String taskId = excelExportService.exportDataAsync(
            req,
            () -> {
                List<UserBO> userBOS = userService.listUsers();
                return UserConverter.INSTANCE.toExportResponseList(userBOS);
            },
            UserExportResponse.class,
            taskStatus -> {
                System.out.println("[" + taskStatus.getTaskId() + "] 导出进度: " + taskStatus.getProgress() + " 条数据");
            }
        );

        return Result.success(taskId);
    }

    /**
     * 查询异步导出任务状态
     */
    @GetMapping("/export-async/status/{taskId}")
    public Result<ExcelTaskStatus> getExportStatus(@PathVariable String taskId) {
        ExcelTaskStatus status = excelExportService.getTaskStatus(taskId);
        return Result.success(status);
    }

    /**
     * 下载异步导出的文件
     */
    @GetMapping("/export-async/download/{taskId}")
    public void downloadExportFile(@PathVariable String taskId, HttpServletResponse response) throws IOException {
        // 1. 查询任务状态
        ExcelTaskStatus status = excelExportService.getTaskStatus(taskId);

        if (status == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("任务不存在");
            return;
        }

        // 检查任务状态
        ExcelTaskStatus.TaskState state = status.getState();
        if (state == ExcelTaskStatus.TaskState.PENDING || state == ExcelTaskStatus.TaskState.RUNNING) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("任务尚未完成，当前进度：" + status.getProgress() + "%");
            return;
        }

        if (state == ExcelTaskStatus.TaskState.FAIL) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("任务执行失败：" + status.getErrorMsg());
            return;
        }

        // 2. 获取文件路径并下载
        String filePath = excelExportService.getExportFilePath(taskId);
        if (filePath == null || !new File(filePath).exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("文件不存在或已过期");
            return;
        }

        File file = new File(filePath);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        response.setContentLength((int) file.length());

        try (InputStream inputStream = new FileInputStream(file)) {
            inputStream.transferTo(response.getOutputStream());
        }
    }

}