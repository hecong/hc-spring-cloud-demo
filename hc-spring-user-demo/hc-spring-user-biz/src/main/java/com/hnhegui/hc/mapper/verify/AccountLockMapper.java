package com.hnhegui.hc.mapper.verify;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.common.enums.AccountLockStatusEnum;
import com.hnhegui.hc.entity.verify.AccountLock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountLockMapper extends BaseMapper<AccountLock> {

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<AccountLock> list);

    /**
     * 将指定账号的锁定记录置为已解锁（status=2）
     *
     * @param userType 用户类型
     * @param account  账号
     * @return 影响行数
     */
    default int unlockByUserTypeAndAccount(String userType, String account) {
        return this.update(null, Wrappers.<AccountLock>lambdaUpdate()
                .eq(AccountLock::getUserType, userType)
                .eq(AccountLock::getAccount, account)
                .eq(AccountLock::getStatus, AccountLockStatusEnum.LOCKED.getCode())
                .set(AccountLock::getStatus, AccountLockStatusEnum.UNLOCKED.getCode()));
    }
}
