package com.hnhegui.hc.controller.enterprise.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.enterprise.EnterpriseBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseCreateBO;
import com.hnhegui.hc.bo.enterprise.EnterprisePageQueryBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserCreateBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserPageQueryBO;
import com.hnhegui.hc.controller.enterprise.request.EnterpriseCreateRequest;
import com.hnhegui.hc.controller.enterprise.request.EnterprisePageRequest;
import com.hnhegui.hc.controller.enterprise.request.EnterpriseUpdateRequest;
import com.hnhegui.hc.controller.enterprise.request.EnterpriseUserCreateRequest;
import com.hnhegui.hc.controller.enterprise.request.EnterpriseUserPageRequest;
import com.hnhegui.hc.controller.enterprise.response.EnterpriseResponse;
import com.hnhegui.hc.controller.enterprise.response.EnterpriseUserResponse;
import com.hnhegui.hc.entity.enterprise.Enterprise;
import com.hnhegui.hc.entity.enterprise.EnterpriseUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EnterpriseConverter {
    EnterpriseConverter INSTANCE = Mappers.getMapper(EnterpriseConverter.class);

    EnterpriseResponse toResponse(EnterpriseBO bo);

    List<EnterpriseResponse> toResponseList(List<EnterpriseBO> list);

    EnterpriseBO entityToBo(Enterprise entity);

    List<EnterpriseBO> entityToBoList(List<Enterprise> list);

    Enterprise createBoToEntity(EnterpriseCreateBO createBO);

    EnterpriseCreateBO createRequestToCreateBo(EnterpriseCreateRequest request);

    EnterpriseCreateBO updateRequestToCreateBo(EnterpriseUpdateRequest request);

    EnterprisePageQueryBO pageRequestToPageBo(EnterprisePageRequest request);

    Page<EnterpriseResponse> toResponsePage(Page<EnterpriseBO> boPage);

    Page<EnterpriseBO> entityPageToBoPage(Page<Enterprise> entityPage);

    EnterpriseUserResponse toUserResponse(EnterpriseUserBO bo);

    List<EnterpriseUserResponse> toUserResponseList(List<EnterpriseUserBO> list);

    EnterpriseUserBO userEntityToBo(EnterpriseUser entity);

    List<EnterpriseUserBO> userEntityToBoList(List<EnterpriseUser> list);

    EnterpriseUser userCreateBoToEntity(EnterpriseUserCreateBO createBO);

    EnterpriseUserCreateBO userCreateRequestToCreateBo(EnterpriseUserCreateRequest request);

    EnterpriseUserPageQueryBO userPageRequestToPageBo(EnterpriseUserPageRequest request);

    Page<EnterpriseUserResponse> toUserResponsePage(Page<EnterpriseUserBO> boPage);

    Page<EnterpriseUserBO> userEntityPageToBoPage(Page<EnterpriseUser> entityPage);
}
