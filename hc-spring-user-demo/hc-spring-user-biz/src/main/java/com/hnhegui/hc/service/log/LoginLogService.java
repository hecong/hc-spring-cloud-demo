package com.hnhegui.hc.service.log;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.log.LoginLogBO;
import com.hnhegui.hc.bo.log.LoginLogPageQueryBO;
import com.hnhegui.hc.controller.log.converter.LoginLogConverter;
import com.hnhegui.hc.common.enums.LoginStatusEnum;
import com.hnhegui.hc.common.enums.UserTypeEnum;
import com.hnhegui.hc.entity.log.LoginLog;
import com.hnhegui.hc.mapper.log.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogService {

    private final LoginLogMapper loginLogMapper;

    // ====================== 异步记录日志 ======================

    /**
     * 异步记录登录日志
     *
     * @param userType    用户类型：C/B/P
     * @param userId      用户ID
     * @param account     登录账号
     * @param loginIp     登录IP
     * @param loginDevice 登录设备
     * @param success     是否成功
     * @param failReason  失败原因
     */
    @Async
    public void recordLoginLog(String userType, Long userId, String account,
                               String loginIp, String loginDevice,
                               boolean success, String failReason) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserType(userType);
        loginLog.setUserId(userId);
        loginLog.setAccount(account);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setLoginIp(loginIp);
        loginLog.setLoginDevice(loginDevice);
        loginLog.setLoginStatus(success ? LoginStatusEnum.SUCCESS.getCode() : LoginStatusEnum.FAIL.getCode());
        loginLog.setFailReason(failReason);
        loginLogMapper.insert(loginLog);
    }

    // ====================== 查询 ======================

    /**
     * 分页查询登录日志
     *
     * @param queryBO 查询参数
     * @return 分页结果
     */
    public Page<LoginLogBO> queryLoginLogsByPage(LoginLogPageQueryBO queryBO) {
        Page<LoginLog> entityPage = loginLogMapper.selectLoginLogsByPage(queryBO);
        return LoginLogConverter.INSTANCE.entityPageToBoPage(entityPage);
    }

    /**
     * 查询指定用户近30天登录记录
     *
     * @param userId   用户ID
     * @param userType 用户类型
     * @param queryBO  分页参数
     * @return 分页结果
     */
    public Page<LoginLogBO> queryRecentLoginLogs(Long userId, String userType, LoginLogPageQueryBO queryBO) {
        Page<LoginLog> page = queryBO.toPage();
        Page<LoginLog> entityPage = loginLogMapper.selectRecentByUserIdAndType(
                userId, userType, LocalDateTime.now().minusDays(30), page);
        return LoginLogConverter.INSTANCE.entityPageToBoPage(entityPage);
    }

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    public int insertBatch(List<LoginLog> list) {
        return loginLogMapper.insertBatch(list);
    }
}
