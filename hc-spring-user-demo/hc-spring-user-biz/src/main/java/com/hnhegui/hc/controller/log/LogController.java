package com.hnhegui.hc.controller.log;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hc.framework.mybatis.model.PageData;
import com.hc.framework.web.model.Result;
import com.hnhegui.hc.bo.log.LoginLogBO;
import com.hnhegui.hc.bo.log.LoginLogPageQueryBO;
import com.hnhegui.hc.controller.log.converter.LoginLogConverter;
import com.hnhegui.hc.controller.log.request.LoginLogPageRequest;
import com.hnhegui.hc.controller.log.response.LoginLogResponse;
import com.hnhegui.hc.service.log.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {

    private final LoginLogService loginLogService;

    /**
     * 登录日志分页查询
     */
    @GetMapping("/login/page")
    public Result<PageData<LoginLogResponse>> queryLoginLogs(LoginLogPageRequest request) {
        LoginLogPageQueryBO queryBO = LoginLogConverter.INSTANCE.pageRequestToPageBo(request);
        Page<LoginLogBO> boPage = loginLogService.queryLoginLogsByPage(queryBO);
        Page<LoginLogResponse> responsePage = LoginLogConverter.INSTANCE.toResponsePage(boPage);
        return Result.success(PageData.of(responsePage));
    }
}
