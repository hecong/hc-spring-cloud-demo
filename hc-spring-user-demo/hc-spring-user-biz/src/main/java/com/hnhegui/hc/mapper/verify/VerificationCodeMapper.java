package com.hnhegui.hc.mapper.verify;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.common.enums.VerificationCodeStatusEnum;
import com.hnhegui.hc.entity.verify.VerificationCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<VerificationCode> list);

    /**
     * 将同一目标+场景下的有效验证码置为失效（status=3）
     *
     * @param target 发送目标
     * @param scene  场景
     * @return 影响行数
     */
    default int expireValidCodes(String target, String scene) {
        return this.update(null, Wrappers.<VerificationCode>lambdaUpdate()
                .eq(VerificationCode::getTarget, target)
                .eq(VerificationCode::getScene, scene)
                .eq(VerificationCode::getStatus, VerificationCodeStatusEnum.VALID.getCode())
                .set(VerificationCode::getStatus, VerificationCodeStatusEnum.EXPIRED.getCode()));
    }

    /**
     * 将已使用的验证码状态置为已使用（status=2）
     *
     * @param target 发送目标
     * @param code   验证码
     * @param scene  场景
     * @return 影响行数
     */
    default int markUsed(String target, String code, String scene) {
        return this.update(null, Wrappers.<VerificationCode>lambdaUpdate()
                .eq(VerificationCode::getTarget, target)
                .eq(VerificationCode::getCode, code)
                .eq(VerificationCode::getScene, scene)
                .eq(VerificationCode::getStatus, VerificationCodeStatusEnum.VALID.getCode())
                .set(VerificationCode::getStatus, VerificationCodeStatusEnum.USED.getCode()));
    }
}
