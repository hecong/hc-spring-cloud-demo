package com.hnhegui.hc.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hc.framework.mybatis.service.BaseService;
import com.hnhegui.hc.bo.user.UserBO;
import com.hnhegui.hc.bo.user.UserCreateBO;
import com.hnhegui.hc.bo.user.UserPageQueryBO;
import com.hnhegui.hc.entity.user.User;

import java.util.List;

public interface UserService extends BaseService<User> {
    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    UserBO getUserByUsername(String username);

    /**
     * 根据角色ID获取用户列表
     *
     * @param roleId 角色ID
     * @return 用户列表
     */
    List<UserBO> getUsersByRoleId(Long roleId);

    /**
     * 保存用户
     *
     * @param userCreateBO 用户请求
     * @return 用户
     */
    UserBO saveUser(UserCreateBO userCreateBO);

    /**
     * 更新用户
     *
     * @param id           用户ID
     * @param userCreateBO 用户请求
     * @return 用户
     */
    UserBO updateUser(Long id, UserCreateBO userCreateBO);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户
     */
    UserBO getUserById(Long id);

    /**
     * 列出所有用户
     *
     * @return 用户列表
     */
    List<UserBO> listUsers();

    /**
     * 根据分页条件获取用户列表
     *
     * @param userPageQueryBO 分页条件
     * @return 用户列表
     */
    Page<UserBO> listUsersByPage(UserPageQueryBO userPageQueryBO);
}