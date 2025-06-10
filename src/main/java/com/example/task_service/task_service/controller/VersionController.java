package com.example.task_service.task_service.controller;

import com.example.task_service.task_service.dto.VersionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1")
public class VersionController {
    @Value("${app.version}")
    private String appVersion;

    @GetMapping("/info")
    @Operation(summary = "Version page", description = "Info about current version of task service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    public VersionDto welcome() {
        return new VersionDto(appVersion, LocalDateTime.now());
    }
}
