package com.hnhegui.hc.controller.auth.converter;

import com.hnhegui.hc.bo.auth.CUserLoginInfoBO;
import com.hnhegui.hc.bo.auth.CurrentUserInfoBO;
import com.hnhegui.hc.bo.auth.EnterpriseUserLoginInfoBO;
import com.hnhegui.hc.bo.auth.IdentityItemBO;
import com.hnhegui.hc.bo.auth.LoginResultBO;
import com.hnhegui.hc.controller.auth.response.CheckEnterpriseResponse;
import com.hnhegui.hc.controller.auth.response.CUserLoginInfoResponse;
import com.hnhegui.hc.controller.auth.response.CurrentUserInfoResponse;
import com.hnhegui.hc.controller.auth.response.EnterpriseUserLoginInfoResponse;
import com.hnhegui.hc.controller.auth.response.IdentityItemResponse;
import com.hnhegui.hc.controller.auth.response.LoginResponse;
import com.hnhegui.hc.bo.enterprise.EnterpriseBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AuthConverter {
    AuthConverter INSTANCE = Mappers.getMapper(AuthConverter.class);

    LoginResponse toLoginResponse(LoginResultBO bo);

    CUserLoginInfoResponse toCUserLoginInfoResponse(CUserLoginInfoBO bo);

    EnterpriseUserLoginInfoResponse toEnterpriseUserLoginInfoResponse(EnterpriseUserLoginInfoBO bo);

    IdentityItemResponse toIdentityItemResponse(IdentityItemBO bo);

    List<IdentityItemResponse> toIdentityItemResponseList(List<IdentityItemBO> list);

    CurrentUserInfoResponse toCurrentUserInfoResponse(CurrentUserInfoBO bo);

    CheckEnterpriseResponse toCheckEnterpriseResponse(EnterpriseBO bo);
}
