package com.hnhegui.hc.controller.log.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.log.LoginLogBO;
import com.hnhegui.hc.bo.log.LoginLogPageQueryBO;
import com.hnhegui.hc.controller.log.request.LoginLogPageRequest;
import com.hnhegui.hc.controller.log.response.LoginLogResponse;
import com.hnhegui.hc.entity.log.LoginLog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LoginLogConverter {
    LoginLogConverter INSTANCE = Mappers.getMapper(LoginLogConverter.class);

    LoginLogResponse toResponse(LoginLogBO bo);

    List<LoginLogResponse> toResponseList(List<LoginLogBO> list);

    LoginLogBO entityToBo(LoginLog entity);

    List<LoginLogBO> entityToBoList(List<LoginLog> list);

    LoginLogPageQueryBO pageRequestToPageBo(LoginLogPageRequest request);

    Page<LoginLogResponse> toResponsePage(Page<LoginLogBO> boPage);

    Page<LoginLogBO> entityPageToBoPage(Page<LoginLog> entityPage);
}
