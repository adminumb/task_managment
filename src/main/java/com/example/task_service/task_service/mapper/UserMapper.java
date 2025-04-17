package com.example.task_service.task_service.mapper;

import com.example.task_service.task_service.dto.TaskDTO;
import com.example.task_service.task_service.dto.UserDTO;
import com.example.task_service.task_service.entity.Role;
import com.example.task_service.task_service.entity.Task;
import com.example.task_service.task_service.entity.User;
import com.example.task_service.task_service.repository.RoleRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface UserMapper {

    // Мы не передаём полный объект Role, а только названия ролей (ROLE_ADMIN, ROLE_USER и т. д.)
    @Mapping(target = "roles", expression = "java(mapRoleToString(user.getRoles()))") // Правильный синтаксис для маппинга
    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", expression = "java(mapStringToRole(dto.getRoles(), roleRepository))") // Используем правильный метод для маппинга ролей
    User toEntity(UserDTO dto, @Context RoleRepository roleRepository);

    // Set<Role> → Set<String>
    default Set<String> mapRoleToString(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    // Set<String> → Set<Role>
    default Set<Role> mapStringToRole(Set<String> roleNames, RoleRepository roleRepository) {
        return roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
    }
}
