package com.hnhegui.hc.service.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hnhegui.hc.entity.user.UserRole;

import java.util.List;

/**
 * @author hecong
 * @since 2026/4/17 09:05
 */
public interface UserRoleService extends IService<UserRole> {


    /**
     * 分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否分配成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

}
