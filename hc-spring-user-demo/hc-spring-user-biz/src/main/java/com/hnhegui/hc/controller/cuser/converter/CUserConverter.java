package com.hnhegui.hc.controller.cuser.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.cuser.CUserBO;
import com.hnhegui.hc.bo.cuser.CUserCreateBO;
import com.hnhegui.hc.bo.cuser.CUserThirdPartyBO;
import com.hnhegui.hc.controller.cuser.request.CUserRegisterRequest;
import com.hnhegui.hc.controller.cuser.request.UpdateProfileRequest;
import com.hnhegui.hc.controller.cuser.response.CUserResponse;
import com.hnhegui.hc.controller.cuser.response.CUserThirdPartyResponse;
import com.hnhegui.hc.entity.cuser.CUser;
import com.hnhegui.hc.entity.cuser.CUserThirdParty;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CUserConverter {
    CUserConverter INSTANCE = Mappers.getMapper(CUserConverter.class);

    CUserResponse toResponse(CUserBO bo);

    List<CUserResponse> toResponseList(List<CUserBO> list);

    CUserBO entityToBo(CUser entity);

    List<CUserBO> entityToBoList(List<CUser> list);

    CUser createBoToEntity(CUserCreateBO createBO);

    CUserCreateBO registerRequestToCreateBo(CUserRegisterRequest request);

    CUserCreateBO updateProfileRequestToCreateBo(UpdateProfileRequest request);

    Page<CUserResponse> toResponsePage(Page<CUserBO> boPage);

    Page<CUserBO> entityPageToBoPage(Page<CUser> entityPage);

    CUserThirdPartyBO thirdPartyEntityToBo(CUserThirdParty entity);

    List<CUserThirdPartyBO> thirdPartyEntityToBoList(List<CUserThirdParty> list);

    CUserThirdPartyResponse toThirdPartyResponse(CUserThirdPartyBO bo);

    List<CUserThirdPartyResponse> toThirdPartyResponseList(List<CUserThirdPartyBO> list);
}
