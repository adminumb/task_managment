package com.example.task_service.task_service.mapper;

import com.example.task_service.task_service.dto.RoleDTO;
import com.example.task_service.task_service.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    Role toEntity(RoleDTO roleDTO);
}
