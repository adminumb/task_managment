package com.example.task_service.task_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VersionDto {

    private String version;

    private LocalDateTime timestamp;

}
