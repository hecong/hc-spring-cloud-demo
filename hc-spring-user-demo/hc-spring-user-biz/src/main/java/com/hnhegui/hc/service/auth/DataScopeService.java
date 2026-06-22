package com.hnhegui.hc.service.auth;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hc.framework.common.model.DataScopeInfo;
import com.hc.framework.mybatis.constant.DataPermConstants;
import com.hc.framework.mybatis.enums.DataScopeEnum;
import com.hc.framework.redis.util.RedisCacheUtils;
import com.hnhegui.hc.entity.permission.Permission;
import com.hnhegui.hc.entity.role.SysRolePermDataScope;
import com.hnhegui.hc.mapper.dept.SysDeptMapper;
import com.hnhegui.hc.mapper.dept.SysUserDeptMapper;
import com.hnhegui.hc.mapper.permission.PermissionMapper;
import com.hnhegui.hc.mapper.role.SysRolePermDataScopeMapper;
import com.hnhegui.hc.mapper.role.SysRolePermDeptScopeMapper;
import com.hnhegui.hc.mapper.user.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据范围计算服务
 *
 * <p>根据用户ID和菜单权限编码，计算有效的最终数据范围。
 * 处理多角色合并、部门递归展开、缓存等逻辑。</p>
 *
 * <p>合并优先级：ALL > DEPT_AND_CHILDREN > CUSTOM_DEPT > CURRENT_DEPT > SELF</p>
 *
 * @author hecong
 * @since 2026/6/11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataScopeService {

    private final UserRoleMapper userRoleMapper;
    private final PermissionMapper permissionMapper;
    private final SysRolePermDataScopeMapper rolePermDataScopeMapper;
    private final SysRolePermDeptScopeMapper rolePermDeptScopeMapper;
    private final SysUserDeptMapper sysUserDeptMapper;
    private final SysDeptMapper sysDeptMapper;
    private final RedisCacheUtils redisCacheUtils;

    /**
     * 缓存 key 前缀
     */
    private static final String CACHE_PREFIX = "dataScope:";
    /**
     * 缓存 TTL
     */
    private static final long CACHE_TTL_MINUTES = 10;

    /**
     * 获取或计算用户数据范围（带缓存）
     */
    public DataScopeInfo getOrComputeDataScope(Long userId, String permissionCode) {
        String cacheKey = CACHE_PREFIX + userId + ":" + permissionCode;
        DataScopeInfo cached = redisCacheUtils.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        DataScopeInfo result = computeDataScope(userId, permissionCode);
        redisCacheUtils.set(cacheKey, result, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return result;
    }

    /**
     * 计算用户对指定菜单的有效数据范围
     */
    public DataScopeInfo computeDataScope(Long userId, String permissionCode) {
        // 1. 获取用户所有角色ID
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (CollUtil.isEmpty(roleIds)) {
            return DataScopeInfo.builder()
                .self(true)
                .build();
        }

        // 2. 获取权限ID
        Long permissionId = getPermissionIdByCode(permissionCode);
        if (permissionId == null) {
            log.warn("[数据权限] 未找到权限编码对应的权限: {}", permissionCode);
            return DataScopeInfo.builder()
                .self(true)
                .build();
        }

        // 3. 查询角色-权限数据范围
        List<SysRolePermDataScope> scopes = rolePermDataScopeMapper
            .selectByRoleIdsAndPermissionId(roleIds, permissionId);

        if (CollUtil.isEmpty(scopes)) {
            return DataScopeInfo.builder()
                .self(true)
                .build();
        }

        // 4. 合并多个角色的数据范围
        return mergeDataScopes(userId, scopes);
    }

    /**
     * 合并多个角色的数据范围
     */
    private DataScopeInfo mergeDataScopes(Long userId, List<SysRolePermDataScope> scopes) {
        // 4.1 任一角色是 ALL → 直接返回 ALL
        boolean hasAll = scopes.stream()
            .anyMatch(s -> DataScopeEnum.ALL.name().equals(s.getDataScope()));
        if (hasAll) {
            return DataScopeInfo.builder()
                .all(true)
                .build();
        }

        // 4.2 获取用户直接部门ID
        Set<Long> userDeptIds = new LinkedHashSet<>(sysUserDeptMapper.selectDeptIdsByUserId(userId));

        // 4.3 检查是否存在 DEPT_AND_CHILDREN
        boolean hasDeptAndChildren = scopes.stream()
            .anyMatch(s -> DataScopeEnum.DEPT_AND_CHILDREN.name().equals(s.getDataScope()));

        Set<Long> allDeptIds = new LinkedHashSet<>();
        boolean self = false;

        if (hasDeptAndChildren && CollUtil.isNotEmpty(userDeptIds)) {
            // DEPT_AND_CHILDREN：展开所有用户部门的下级部门
            allDeptIds.addAll(userDeptIds);
            allDeptIds.addAll(expandDeptTree(userDeptIds, userId));
        }

        // 4.4 处理 CUSTOM_DEPT / CURRENT_DEPT / SELF
        for (SysRolePermDataScope scope : scopes) {
            DataScopeEnum scopeEnum = DataScopeEnum.valueOf(scope.getDataScope());
            switch (scopeEnum) {
                case CUSTOM_DEPT:
                    // 查询该 scope 指定的部门ID
                    List<Long> customDeptIds = rolePermDeptScopeMapper
                        .selectDeptIdsByScopeIds(Collections.singletonList(scope.getId()));
                    allDeptIds.addAll(customDeptIds);
                    break;
                case CURRENT_DEPT:
                    allDeptIds.addAll(userDeptIds);
                    break;
                case SELF:
                    self = true;
                    break;
                default:
                    break;
            }
        }

        return DataScopeInfo.builder()
            .all(false)
            .self(self)
            .deptIds(allDeptIds)
            .build();
    }

    /**
     * 递归展开部门树（上限截断 + 告警）
     */
    private Set<Long> expandDeptTree(Set<Long> userDeptIds, Long userId) {
        Set<Long> children = new LinkedHashSet<>();
        for (Long deptId : userDeptIds) {
            children.addAll(sysDeptMapper.selectChildIdsByAncestor(deptId));
        }
        if (children.size() > DataPermConstants.MAX_DEPT_EXPAND_SIZE) {
            log.warn("[数据权限] 部门展开超限 userId={} total={} max={}，已截断",
                userId, children.size(), DataPermConstants.MAX_DEPT_EXPAND_SIZE);
            return children.stream()
                .limit(DataPermConstants.MAX_DEPT_EXPAND_SIZE)
                .collect(Collectors.toSet());
        }
        return children;
    }

    /**
     * 根据权限编码查询权限ID
     */
    private Long getPermissionIdByCode(String permissionCode) {
        Permission permission = permissionMapper.selectOne(
            Wrappers.<Permission>lambdaQuery()
                .select(Permission::getId)
                .eq(Permission::getCode, permissionCode),
            false);
        return permission != null ? permission.getId() : null;
    }

    // ==================== 缓存失效 ====================

    /**
     * 清除指定用户的全部数据权限缓存
     */
    public void evictUserCache(Long userId) {
        redisCacheUtils.deleteByPrefix(CACHE_PREFIX + userId + ":");
    }

    /**
     * 清除指定权限的全部用户数据权限缓存
     */
    public void evictPermissionCache(String permissionCode) {
        redisCacheUtils.deleteByPrefix(CACHE_PREFIX);
    }

    /**
     * 全局清除数据权限缓存
     */
    public void evictAllCache() {
        redisCacheUtils.deleteByPrefix(CACHE_PREFIX);
    }
}
