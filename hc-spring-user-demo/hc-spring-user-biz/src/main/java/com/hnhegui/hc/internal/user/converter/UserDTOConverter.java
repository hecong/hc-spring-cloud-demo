package com.hnhegui.hc.internal.user.converter;

import com.hnhegui.hc.bo.user.UserBO;
import com.hnhegui.hc.feign.response.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserDTOConverter {

    UserDTOConverter INSTANCE = Mappers.getMapper(UserDTOConverter.class);

    /**
     * 将UserBO转换为UserDTO
     *
     * @param userBO 用户BO
     * @return 用户DTO
     */
    UserDTO convertToUserDTO(UserBO userBO);

    /**
     * 将UserBO列表转换为UserDTO列表
     *
     * @param userBOS 用户BO列表
     * @return 用户DTO列表
     */
    List<UserDTO> convertToUserDTOList(List<UserBO> userBOS);
}