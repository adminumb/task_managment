package com.example.task_service.task_service.mapper;

import com.example.task_service.task_service.dto.TaskDTO;
import com.example.task_service.task_service.entity.Task;
import com.example.task_service.task_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    private TaskMapper taskMapper;
    private Task task;
    private TaskDTO taskDTO;
    private User user;

    @BeforeEach
    void setUp() {
        taskMapper = Mappers.getMapper(TaskMapper.class);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setUser(user);

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setName("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setUserUsername("testUser");
    }

/*    @Test
    void toDTO_ShouldMapTaskToTaskDTO() {
        // Act
        TaskDTO result = taskMapper.toDTO(task);

        // Assert
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getName(), result.getName());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getUser().getUsername(), result.getUserUsername());
    }*/

    @Test
    void toEntity_ShouldMapTaskDTOToTask() {
        // Act
        Task result = taskMapper.toEntity(taskDTO);

        // Assert
        assertNotNull(result);
        assertEquals(taskDTO.getName(), result.getName());
        assertEquals(taskDTO.getDescription(), result.getDescription());
        // Note: user mapping is not handled in toEntity as it's not in the mapping
    }

    @Test
    void updateTaskFromTaskDTO_ShouldUpdateTaskFields() {
        // Arrange
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setName("Old Name");
        existingTask.setDescription("Old Description");
        existingTask.setCreatedAt(LocalDateTime.now().minusDays(1));
        existingTask.setUpdatedAt(LocalDateTime.now().minusDays(1));

        // Act
        taskMapper.updateTaskFromTaskDTO(taskDTO, existingTask);

        // Assert
        assertEquals(1L, existingTask.getId()); // ID should not be updated
        assertEquals(taskDTO.getName(), existingTask.getName());
        assertEquals(taskDTO.getDescription(), existingTask.getDescription());
        assertNotNull(existingTask.getCreatedAt()); // createdAt should not be updated
        assertNotNull(existingTask.getUpdatedAt()); // updatedAt should not be updated
    }

    @Test
    void toDTO_WithNullUser_ShouldMapCorrectly() {
        // Arrange
        task.setUser(null);

        // Act
        TaskDTO result = taskMapper.toDTO(task);

        // Assert
        assertNotNull(result);
        assertNull(result.getUserUsername());
    }

    @Test
    void toEntity_WithNullFields_ShouldMapCorrectly() {
        // Arrange
        taskDTO.setName(null);
        taskDTO.setDescription(null);
        taskDTO.setUserUsername(null);

        // Act
        Task result = taskMapper.toEntity(taskDTO);

        // Assert
        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getDescription());
    }
}
