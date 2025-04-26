package com.example.task_service.task_service.controller;

import com.example.task_service.task_service.dto.TaskDTO;
import com.example.task_service.task_service.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/tasks")
    public List<TaskDTO> getAllTasks(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return taskService.getAllTasks(pageable).getContent();
    }

    @GetMapping("/tasks/{id}")
    public TaskDTO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping("/task")
    public TaskDTO createTask(@RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);
    }

    @GetMapping("/user/{username}")
    public List<TaskDTO> getTasksByUser(@PathVariable String username) {
        return taskService.getTasksByUsername(username);
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO);
    }

    @DeleteMapping("/task/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
