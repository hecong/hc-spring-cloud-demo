package com.hnhegui.hc.controller.role.converter;

import com.hnhegui.hc.controller.role.request.RoleRequest;
import com.hnhegui.hc.controller.role.response.RoleResponse;
import com.hnhegui.hc.entity.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RoleConverter {
    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);
    /**
     * Convert a role request to a role entity.
     *
     * @param roleRequest the role request to converter
     * @return the role entity
     */
    Role toEntity(RoleRequest roleRequest);
    /**
     * Convert a role entity to a role response.
     *
     * @param role the role entity to converter
     * @return the role response
     */
    RoleResponse toResponse(Role role);

    /**
     * Convert a list of roles to a list of role responses.
     *
     * @param roles the list of roles to converter
     * @return the list of role responses
     */
    List<RoleResponse> toResponseList(List<Role> roles);
}