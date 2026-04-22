package com.hnhegui.hc.mapper.enterprise;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserPageQueryBO;
import com.hnhegui.hc.common.enums.EnterpriseUserStatusEnum;
import com.hnhegui.hc.entity.enterprise.EnterpriseUser;
import org.apache.ibatis.annotations.Param;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;

public interface EnterpriseUserMapper extends BaseMapper<EnterpriseUser> {

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<EnterpriseUser> list);

    /**
     * 批量插入或更新
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<EnterpriseUser> list);

    /**
     * 根据企业ID和用户名查询
     *
     * @param enterpriseId 企业ID
     * @param username     用户名
     * @return 企业用户
     */
    default EnterpriseUser selectByEnterpriseIdAndUsername(Long enterpriseId, String username) {
        return this.selectOne(Wrappers.<EnterpriseUser>lambdaQuery()
                .eq(EnterpriseUser::getEnterpriseId, enterpriseId)
                .eq(EnterpriseUser::getUsername, username));
    }

    /**
     * 根据手机号查询在职企业用户列表（status != 5 已离职）
     *
     * @param phone 手机号
     * @return 企业用户列表
     */
    default List<EnterpriseUser> selectActiveByPhone(String phone) {
        return this.selectList(Wrappers.<EnterpriseUser>lambdaQuery()
                .eq(EnterpriseUser::getPhone, phone)
                .ne(EnterpriseUser::getStatus, EnterpriseUserStatusEnum.RESIGNED.getCode()));
    }

    /**
     * 根据邮箱查询在职企业用户列表（status != 5 已离职）
     *
     * @param email 邮箱
     * @return 企业用户列表
     */
    default List<EnterpriseUser> selectActiveByEmail(String email) {
        return this.selectList(Wrappers.<EnterpriseUser>lambdaQuery()
                .eq(EnterpriseUser::getEmail, email)
                .ne(EnterpriseUser::getStatus, EnterpriseUserStatusEnum.RESIGNED.getCode()));
    }

    /**
     * 分页查询企业用户列表
     *
     * @param queryBO 查询参数
     * @return 分页结果
     */
    default Page<EnterpriseUser> selectEnterpriseUsersByPage(EnterpriseUserPageQueryBO queryBO) {
        return this.selectPage(queryBO.toPage(), Wrappers.<EnterpriseUser>lambdaQuery()
                .eq(queryBO.getEnterpriseId() != null, EnterpriseUser::getEnterpriseId, queryBO.getEnterpriseId())
                .like(StrUtil.isNotBlank(queryBO.getName()), EnterpriseUser::getName, queryBO.getName())
                .like(StrUtil.isNotBlank(queryBO.getUsername()), EnterpriseUser::getUsername, queryBO.getUsername())
                .eq(queryBO.getStatus() != null, EnterpriseUser::getStatus, queryBO.getStatus())
                .ne(EnterpriseUser::getStatus, EnterpriseUserStatusEnum.RESIGNED.getCode())
                .orderByDesc(EnterpriseUser::getCreateTime));
    }
}
