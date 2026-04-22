package com.hnhegui.hc.mapper.cuser;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.entity.cuser.CUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CUserMapper extends BaseMapper<CUser> {

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<CUser> list);

    /**
     * 批量插入或更新
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<CUser> list);

    /**
     * 根据手机号查询C端用户
     *
     * @param phone 手机号
     * @return C端用户
     */
    default CUser selectByPhone(String phone) {
        return this.selectOne(Wrappers.<CUser>lambdaQuery()
                .eq(CUser::getPhone, phone));
    }

    /**
     * 根据邮箱查询C端用户
     *
     * @param email 邮箱
     * @return C端用户
     */
    default CUser selectByEmail(String email) {
        return this.selectOne(Wrappers.<CUser>lambdaQuery()
                .eq(CUser::getEmail, email));
    }

    /**
     * 根据用户名查询C端用户
     *
     * @param username 用户名
     * @return C端用户
     */
    default CUser selectByUsername(String username) {
        return this.selectOne(Wrappers.<CUser>lambdaQuery()
                .eq(CUser::getUsername, username));
    }

    /**
     * 根据账号（手机号 or 邮箱 or 用户名）查询C端用户（用于登录）
     *
     * @param account 账号
     * @return C端用户
     */
    default CUser selectByAccount(String account) {
        return this.selectOne(Wrappers.<CUser>lambdaQuery()
                .eq(CUser::getPhone, account)
                .or().eq(CUser::getEmail, account)
                .or().eq(CUser::getUsername, account));
    }
}
