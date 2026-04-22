package com.hnhegui.hc.mapper.enterprise;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.entity.enterprise.Enterprise;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EnterpriseMapper extends BaseMapper<Enterprise> {

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<Enterprise> list);

    /**
     * 批量插入或更新
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<Enterprise> list);

    /**
     * 根据企业编码查询
     *
     * @param enterpriseCode 企业编码
     * @return 企业
     */
    default Enterprise selectByEnterpriseCode(String enterpriseCode) {
        return this.selectOne(Wrappers.<Enterprise>lambdaQuery()
                .eq(Enterprise::getEnterpriseCode, enterpriseCode));
    }
}
