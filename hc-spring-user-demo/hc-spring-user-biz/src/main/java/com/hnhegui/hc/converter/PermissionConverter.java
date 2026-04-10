package com.hnhegui.hc.converter;

import com.hnhegui.hc.dto.PermissionRequest;
import com.hnhegui.hc.dto.PermissionResponse;
import com.hnhegui.hc.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PermissionConverter {
    PermissionConverter INSTANCE = Mappers.getMapper(PermissionConverter.class);
    
    Permission toEntity(PermissionRequest permissionRequest);
    PermissionResponse toResponse(Permission permission);
}