package com.hnhegui.hc.converter;

import com.hnhegui.hc.dto.UserRequest;
import com.hnhegui.hc.dto.UserResponse;
import com.hnhegui.hc.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);
    
    User toEntity(UserRequest userRequest);
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}