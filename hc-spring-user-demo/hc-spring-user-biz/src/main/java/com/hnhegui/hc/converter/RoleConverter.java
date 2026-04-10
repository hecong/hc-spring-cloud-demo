package com.hnhegui.hc.converter;

import com.hnhegui.hc.dto.RoleRequest;
import com.hnhegui.hc.dto.RoleResponse;
import com.hnhegui.hc.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleConverter {
    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);
    
    Role toEntity(RoleRequest roleRequest);
    RoleResponse toResponse(Role role);
}