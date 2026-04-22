package com.hnhegui.hc.controller.user.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.user.UserBO;
import com.hnhegui.hc.bo.user.UserCreateBO;
import com.hnhegui.hc.bo.user.UserPageQueryBO;
import com.hnhegui.hc.controller.user.request.UserPageRequest;
import com.hnhegui.hc.controller.user.request.UserRequest;
import com.hnhegui.hc.controller.user.response.UserExportResponse;
import com.hnhegui.hc.controller.user.response.UserResponse;
import com.hnhegui.hc.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    /**
     * Entity to Response
     */
    UserResponse toResponse(UserBO userBO);

    /**
     * Entity List to Response List
     */
    List<UserResponse> toResponseList(List<UserBO> users);

    /**
     * Entity to BO
     */
    UserBO entityToBo(User user);

    /**
     * Entity List to BO List
     */
    List<UserBO> entityToBoList(List<User> users);

    /**
     * Create BO to Entity
     */
    User createBoToEntity(UserCreateBO userCreateBO);

    /**
     * Request to Create BO
     */
    UserCreateBO requestToCreateBo(UserRequest userRequest);

    /**
     * Request to Page BO
     */
    UserPageQueryBO requestToPageBo(UserPageRequest request);


    /**
     * BO Page to Response Page
     */
    Page<UserResponse> toResponsePage(Page<UserBO> userBOIPage);

    /**
     * Entity Page to BO Page
     */
    Page<UserBO> entityPageToBoPage(Page<User> userIPage);

    /**
     * Entity List to Export Response List
     */
    List<UserExportResponse> toExportResponseList(List<UserBO> userBOS);
}