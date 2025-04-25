package com.example.task_service.task_service.service;

import com.example.task_service.task_service.dto.TaskDTO;
import com.example.task_service.task_service.entity.Task;
import com.example.task_service.task_service.entity.User;
import com.example.task_service.task_service.mapper.TaskMapper;
import com.example.task_service.task_service.repository.TaskRepository;
import com.example.task_service.task_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .password("testPassword")
                .active(true)
                .build();

        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .active(true)
                .user(user)
                .build();

        taskDTO = TaskDTO.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .userUsername("testUser")
                .build();
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(task);
        Pageable pageable = PageRequest.of(0, 10);
        // Create PageImpl with total elements = 10 to match the page size
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, 10);

        when(taskRepository.findAllByActiveTrue(pageable)).thenReturn(taskPage);
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        Page<TaskDTO> result = taskService.getAllTasks(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(taskDTO, result.getContent().get(0));
        verify(taskRepository).findAllByActiveTrue(pageable);
        verify(taskMapper).toDTO(task);
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(taskDTO, result);
        verify(taskRepository).findByIdAndActiveTrue(1L);
        verify(taskMapper).toDTO(task);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldThrowException() {
        // Arrange
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository).findByIdAndActiveTrue(1L);
        verify(taskMapper, never()).toDTO(any());
    }

    @Test
    void createTask_ShouldCreateAndReturnTask() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskMapper.toEntity(any(TaskDTO.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.createTask(taskDTO);

        // Assert
        assertNotNull(result);
        assertEquals(taskDTO, result);
        verify(userRepository).findByUsername("testUser");
        verify(taskMapper).toEntity(taskDTO);
        verify(taskRepository).save(task);
        verify(taskMapper).toDTO(task);
    }

    @Test
    void getTasksByUsername_ShouldReturnTasksForUser() {
        // Arrange
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByUserUsername("testUser")).thenReturn(tasks);
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        List<TaskDTO> result = taskService.getTasksByUsername("testUser");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskDTO, result.get(0));
        verify(taskRepository).findByUserUsername("testUser");
        verify(taskMapper).toDTO(task);
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateAndReturnTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.updateTask(1L, taskDTO);

        // Assert
        assertNotNull(result);
        assertEquals(taskDTO, result);
        verify(taskRepository).findById(1L);
        verify(taskMapper).updateTaskFromTaskDTO(taskDTO, task);
        verify(taskRepository).save(task);
        verify(taskMapper).toDTO(task);
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(1L, taskDTO));
        verify(taskRepository).findById(1L);
        verify(taskMapper, never()).updateTaskFromTaskDTO(any(), any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_ShouldSetActiveToFalse() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(task);
        assertFalse(task.isActive());
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository).findById(1L);
        verify(taskRepository, never()).save(any());
    }
}
