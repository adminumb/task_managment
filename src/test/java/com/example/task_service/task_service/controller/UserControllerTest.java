package com.example.task_service.task_service.controller;

import com.example.task_service.task_service.dto.UserDTO;
import com.example.task_service.task_service.exception.RoleNotFoundException;
import com.example.task_service.task_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private List<UserDTO> userDTOList;

    @BeforeEach
    void setUp() {
        userDTO = UserDTO.builder()
                .username("Pavel22")
                .email("pavel@example.com")
                .password("1234")
                .roles(Set.of("ROLE_ADMIN"))
                .build();

        userDTOList = Arrays.asList(userDTO);
    }

    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(userDTOList);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Pavel22"))
                .andExpect(jsonPath("$[0].email").value("pavel@example.com"))
                .andExpect(jsonPath("$[0].roles[0]").value("ROLE_ADMIN"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void findByUsername_shouldReturnUser() throws Exception {
        when(userService.findByUsername("Pavel22")).thenReturn(userDTOList);

        mockMvc.perform(get("/api/v1/users/Pavel22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Pavel22"))
                .andExpect(jsonPath("$[0].email").value("pavel@example.com"))
                .andExpect(jsonPath("$[0].roles[0]").value("ROLE_ADMIN"));

        verify(userService, times(1)).findByUsername("Pavel22");
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Pavel22"))
                .andExpect(jsonPath("$.email").value("pavel@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));

        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    void assignRoles_shouldReturnUpdatedUser() throws Exception {
        Set<String> newRoles = Set.of("ROLE_USER", "ROLE_ADMIN");
        when(userService.assignRolesToUser(eq("Pavel22"), eq(newRoles))).thenReturn(userDTO);

        mockMvc.perform(post("/api/v1/Pavel22/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRoles)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Pavel22"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));

        verify(userService, times(1)).assignRolesToUser(eq("Pavel22"), eq(newRoles));
    }

    @Test
    void createUser_withInvalidRole_shouldReturnNotFound() throws Exception {
        UserDTO invalidUserDTO = UserDTO.builder()
                .username("Pavel22")
                .email("pavel@example.com")
                .password("1234")
                .roles(Set.of("ROLE_UNKNOWN"))
                .build();

        when(userService.createUser(any(UserDTO.class)))
                .thenThrow(new RoleNotFoundException("Role ROLE_UNKNOWN not found"));

        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Role ROLE_UNKNOWN not found"));

        verify(userService, times(1)).createUser(any(UserDTO.class));
    }
}
