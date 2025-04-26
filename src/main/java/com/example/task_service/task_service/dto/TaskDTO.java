package com.example.task_service.task_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private String userUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
