package com.example.task_service.task_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String name;
    private String description;
    private boolean completed;
    private String userUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
