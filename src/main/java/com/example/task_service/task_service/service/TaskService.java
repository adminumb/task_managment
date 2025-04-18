package com.example.task_service.task_service.service;

import com.example.task_service.task_service.dto.TaskDTO;
import com.example.task_service.task_service.entity.Task;
import com.example.task_service.task_service.mapper.TaskMapper;
import com.example.task_service.task_service.repository.TaskRepository;
import com.example.task_service.task_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }
        public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDTO)
                .collect(Collectors.toList());
        }

        public TaskDTO getTaskById(Long id) {
            return taskRepository.findById(id)
                    .map(taskMapper::toDTO)
                    .orElse(null);
        }

        public TaskDTO createTask(TaskDTO taskDTO) {
            Task task = taskMapper.toEntity(taskDTO);
            task = taskRepository.save(task);
            return taskMapper.toDTO(task);
        }

        public List<TaskDTO> getTasksByUsername(String username) {
            return taskRepository.findByUserUsername(username)
                    .stream()
                    .map(taskMapper::toDTO)
                    .toList();
        }

        public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
            return taskRepository.findById(id)
                    .map(existingTask -> {
                        taskMapper.updateTaskFromTaskDTO(taskDTO, existingTask);
                        return taskMapper.toDTO(taskRepository.save(existingTask));
                    })
                    .orElse(null);
        }


        public void deleteTask(Long id) {
            taskRepository.deleteById(id);
        }
}

