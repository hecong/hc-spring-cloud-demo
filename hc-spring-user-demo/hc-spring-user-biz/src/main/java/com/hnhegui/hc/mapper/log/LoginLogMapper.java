package com.hnhegui.hc.mapper.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.log.LoginLogPageQueryBO;
import com.hnhegui.hc.entity.log.LoginLog;
import org.apache.ibatis.annotations.Param;
import org.dromara.hutool.core.text.StrUtil;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<LoginLog> list);

    /**
     * 分页查询登录日志
     *
     * @param queryBO 查询参数
     * @return 分页结果
     */
    default Page<LoginLog> selectLoginLogsByPage(LoginLogPageQueryBO queryBO) {
        return this.selectPage(queryBO.toPage(), Wrappers.<LoginLog>lambdaQuery()
                .eq(StrUtil.isNotBlank(queryBO.getUserType()), LoginLog::getUserType, queryBO.getUserType())
                .eq(queryBO.getUserId() != null, LoginLog::getUserId, queryBO.getUserId())
                .orderByDesc(LoginLog::getLoginTime));
    }

    /**
     * 查询指定用户近N天登录记录（分页）
     *
     * @param userId   用户ID
     * @param userType 用户类型
     * @param since    起始时间
     * @param page     分页参数
     * @return 分页结果
     */
    default Page<LoginLog> selectRecentByUserIdAndType(Long userId, String userType,
                                                       LocalDateTime since, Page<LoginLog> page) {
        return this.selectPage(page, Wrappers.<LoginLog>lambdaQuery()
                .eq(LoginLog::getUserType, userType)
                .eq(LoginLog::getUserId, userId)
                .ge(LoginLog::getLoginTime, since)
                .orderByDesc(LoginLog::getLoginTime));
    }
}
