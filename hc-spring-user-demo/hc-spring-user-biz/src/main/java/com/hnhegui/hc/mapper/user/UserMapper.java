package com.hnhegui.hc.mapper.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.user.UserPageQueryBO;
import com.hnhegui.hc.entity.user.User;
import org.apache.ibatis.annotations.Param;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author system
 * @date 2025-01-01
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 批量插入用户
     *
     * @param list 用户集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<User> list);

    /**
     * 批量插入或更新用户（存在则更新，不存在则插入）
     *
     * @param list 用户集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<User> list);

    /**
     * 分页查询用户列表
     *
     * @param queryBO 分页查询参数
     * @return 分页结果
     */
    default Page<User> selectUsersByPage(UserPageQueryBO queryBO) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();

        wrapper.eq(StrUtil.isNotBlank(queryBO.getUsername()), User::getUsername, queryBO.getUsername())
            .eq(StrUtil.isNotBlank(queryBO.getPhone()), User::getPhone, queryBO.getPhone())
            .eq(StrUtil.isNotBlank(queryBO.getName()), User::getName, queryBO.getName());

        return this.selectPage(queryBO.toPage(), wrapper);
    }
}