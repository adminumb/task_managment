package com.example.task_service.task_service.service;

import com.example.logexecution.annotation.LogExecutionTime;
import com.example.task_service.task_service.dto.TaskDTO;
import com.example.task_service.task_service.entity.Task;
import com.example.task_service.task_service.entity.User;
import com.example.task_service.task_service.exception.BadRequestException;
import com.example.task_service.task_service.mapper.TaskMapper;
import com.example.task_service.task_service.repository.TaskRepository;
import com.example.task_service.task_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    @LogExecutionTime
    public Page<TaskDTO> getAllTasks(Pageable pageable) {
        return taskRepository.findAllByActiveTrue(pageable)
                .map(taskMapper::toDTO);
    }

    @LogExecutionTime
    public TaskDTO getTaskById(Long id) {
        return taskRepository.findByIdAndActiveTrue(id)
                .map(taskMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
    }

    @LogExecutionTime
    public TaskDTO createTask(TaskDTO taskDTO) {
        if (taskDTO.getUserUsername() == null) {
            throw new BadRequestException("User username is required");
        }

        User user = userRepository.findByUsername(taskDTO.getUserUsername())
                .orElseThrow(() -> new BadRequestException("User with username " + taskDTO.getUserUsername() + " not found"));

        Task task = taskMapper.toEntity(taskDTO);
        task.setUser(user);
        task = taskRepository.save(task);
        return taskMapper.toDTO(task);
    }

    @LogExecutionTime
    public List<TaskDTO> getTasksByUsername(String username) {
        return taskRepository.findByUserUsername(username)
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @LogExecutionTime
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    taskMapper.updateTaskFromTaskDTO(taskDTO, existingTask);
                    return taskMapper.toDTO(taskRepository.save(existingTask));
                })
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
    }

    @LogExecutionTime
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
        task.setActive(false);
        taskRepository.save(task);
    }
}
