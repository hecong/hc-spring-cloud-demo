package com.hnhegui.hc.gateway.provider;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.hnhegui.hc.context.core.UserContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hnhegui.hc.common.constant.CommonConstant.USER_CONTEXT;


/**
 * @author hecong
 * @since 2026/4/10 09:20
 */
@RequiredArgsConstructor
public class SaTokenGatewayStpInterface implements StpInterface {



    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        UserContext userContext = (UserContext) StpUtil.getSession().get(USER_CONTEXT);
        return userContext.getPermissions();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        UserContext userContext = (UserContext) StpUtil.getSession().get(USER_CONTEXT);
        return userContext.getRoles();
    }
}
