package com.example.task_service.task_service.controller;

import com.example.task_service.task_service.dto.TaskDTO;
import com.example.task_service.task_service.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskDTO taskDTO;
    private List<TaskDTO> taskDTOList;

    @BeforeEach
    void setUp() {
        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setName("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setCompleted(false);
        taskDTO.setUserUsername("testuser");
        taskDTO.setCreatedAt(LocalDateTime.now());
        taskDTO.setUpdatedAt(LocalDateTime.now());

        taskDTOList = Arrays.asList(taskDTO);
    }

    @Test
    void getAllTasks_shouldReturnTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(taskDTOList);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Task"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].userUsername").value("testuser"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].updatedAt").exists());

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskDTO);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.userUsername").value("testuser"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(taskDTO);

        mockMvc.perform(post("/api/v1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.userUsername").value("testuser"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(taskService, times(1)).createTask(any(TaskDTO.class));
    }

    @Test
    void getTasksByUser_shouldReturnUserTasks() throws Exception {
        when(taskService.getTasksByUsername("testuser")).thenReturn(taskDTOList);

        mockMvc.perform(get("/api/v1/user/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Task"))
                .andExpect(jsonPath("$[0].userUsername").value("testuser"));

        verify(taskService, times(1)).getTasksByUsername("testuser");
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(taskDTO);

        mockMvc.perform(put("/api/v1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.userUsername").value("testuser"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(taskService, times(1)).updateTask(eq(1L), any(TaskDTO.class));
    }

    @Test
    void deleteTask_shouldReturnOk() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/v1/1"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteTask(1L);
    }
} 