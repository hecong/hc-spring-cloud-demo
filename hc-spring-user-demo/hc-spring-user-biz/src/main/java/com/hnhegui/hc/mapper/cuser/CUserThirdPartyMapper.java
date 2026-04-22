package com.hnhegui.hc.mapper.cuser;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.entity.cuser.CUserThirdParty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CUserThirdPartyMapper extends BaseMapper<CUserThirdParty> {

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<CUserThirdParty> list);

    /**
     * 批量插入或更新
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<CUserThirdParty> list);

    /**
     * 根据用户ID查询第三方绑定列表
     *
     * @param userId 用户ID
     * @return 第三方绑定列表
     */
    default List<CUserThirdParty> selectByUserId(Long userId) {
        return this.selectList(Wrappers.<CUserThirdParty>lambdaQuery()
                .eq(CUserThirdParty::getUserId, userId));
    }

    /**
     * 根据平台和openId查询绑定记录
     *
     * @param platform 平台
     * @param openId   openId
     * @return 绑定记录
     */
    default CUserThirdParty selectByPlatformAndOpenId(String platform, String openId) {
        return this.selectOne(Wrappers.<CUserThirdParty>lambdaQuery()
                .eq(CUserThirdParty::getPlatform, platform)
                .eq(CUserThirdParty::getOpenId, openId));
    }

    /**
     * 根据用户ID和平台删除绑定
     *
     * @param userId   用户ID
     * @param platform 平台
     * @return 影响行数
     */
    default int deleteByUserIdAndPlatform(Long userId, String platform) {
        return this.delete(Wrappers.<CUserThirdParty>lambdaQuery()
                .eq(CUserThirdParty::getUserId, userId)
                .eq(CUserThirdParty::getPlatform, platform));
    }
}
