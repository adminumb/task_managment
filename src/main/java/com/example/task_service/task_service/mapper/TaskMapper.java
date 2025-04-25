package com.example.task_service.task_service.mapper;

import com.example.task_service.task_service.dto.TaskDTO;
import com.example.task_service.task_service.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "userUsername", source = "user.username")
    TaskDTO toDTO(Task task);

    @Mapping(target = "user.username", source = "userUsername")
    Task toEntity(TaskDTO dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user.username", source = "userUsername")
    void updateTaskFromTaskDTO(TaskDTO taskDTO, @MappingTarget Task task);
}
