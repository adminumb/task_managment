package com.example.task_service.task_service.controller;

import com.example.task_service.task_service.dto.TaskDTO;
import com.example.task_service.task_service.entity.Task;
import com.example.task_service.task_service.entity.User;
import com.example.task_service.task_service.mapper.TaskMapper;
import com.example.task_service.task_service.repository.TaskRepository;
import com.example.task_service.task_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class TaskControllerTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Task testTask;
    private TaskDTO testTaskDTO;
    @Autowired
    private  TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        // Clear repositories
        taskRepository.deleteAll();
        userRepository.deleteAll();


        testUser = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("testPassword")
                .active(true)
                .build();
        userRepository.save(testUser);


        // Create test task
        testTask = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .active(true)
                .user(testUser)
                .build();
        taskRepository.save(testTask);

        testTaskDTO = taskMapper.toDTO(testTask);

    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() throws Exception {

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].title").value(testTaskDTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(testTaskDTO.getDescription()))
                .andExpect(jsonPath("$[0].completed").value(testTaskDTO.isCompleted()))
                .andExpect(jsonPath("$[0].userUsername").value(testUser.getUsername()));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/{id}", testTaskDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testTaskDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(testTaskDTO.getDescription()))
                .andExpect(jsonPath("$.completed").value(testTaskDTO.isCompleted()))
                .andExpect(jsonPath("$.userUsername").value(testUser.getUsername()));
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_ShouldCreateAndReturnTask() throws Exception {
        mockMvc.perform(post("/api/v1/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testTaskDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(testTaskDTO.getDescription()))
                .andExpect(jsonPath("$.completed").value(testTaskDTO.isCompleted()))
                .andExpect(jsonPath("$.userUsername").value(testUser.getUsername()));
    }

    @Test
    void createTask_WithNonExistentUser_ShouldReturnBadRequest() throws Exception {
        testTaskDTO.setUserUsername("nonExistentUser");
        mockMvc.perform(post("/api/v1/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTasksByUser_ShouldReturnUserTasks() throws Exception {
        mockMvc.perform(get("/api/v1/user/{username}", testUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(testTaskDTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(testTaskDTO.getDescription()))
                .andExpect(jsonPath("$[0].completed").value(testTaskDTO.isCompleted()))
                .andExpect(jsonPath("$[0].userUsername").value(testUser.getUsername()));
    }

    @Test
    void getTasksByUser_WithNonExistentUser_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/user/{username}", "nonExistentUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateAndReturnTask() throws Exception {
        testTaskDTO.setTitle("Updated Title");
        testTaskDTO.setDescription("Updated Description");

        mockMvc.perform(put("/api/v1/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testTaskDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(testTaskDTO.getDescription()))
                .andExpect(jsonPath("$.completed").value(testTaskDTO.isCompleted()))
                .andExpect(jsonPath("$.userUsername").value(testUser.getUsername()));
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/api/v1/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_ShouldDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/v1/task/{id}", testTask.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/tasks/{id}", testTask.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/task/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}

