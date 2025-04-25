package com.example.task_service.task_service.controller;

import com.example.task_service.task_service.dto.UserDTO;
import com.example.task_service.task_service.entity.Role;
import com.example.task_service.task_service.entity.User;
import com.example.task_service.task_service.mapper.UserMapper;
import com.example.task_service.task_service.repository.RoleRepository;
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

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class UserControllerTest {

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
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    private User testUser;
    private UserDTO testUserDTO;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Clear repositories
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create test role
        testRole = Role.builder()
                .name("ROLE_ADMIN")
                .build();
        roleRepository.save(testRole);

        // Create test user
        testUser = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("testPassword")
                .active(true)
                .roles(Set.of(testRole))
                .build();
        userRepository.save(testUser);

        testUserDTO = userMapper.toDTO(testUser);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].username").value(testUserDTO.getUsername()))
                .andExpect(jsonPath("$[0].email").value(testUserDTO.getEmail()))
                .andExpect(jsonPath("$[0].roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/{username}", testUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(testUserDTO.getUsername()))
                .andExpect(jsonPath("$[0].email").value(testUserDTO.getEmail()))
                .andExpect(jsonPath("$[0].roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/users/{username}", "nonExistentUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() throws Exception {
        UserDTO newUserDTO = UserDTO.builder()
                .username("newUser")
                .email("new@example.com")
                .password("newPassword")
                .roles(Set.of("ROLE_ADMIN"))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(newUserDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(newUserDTO.getEmail()))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    void createUser_WithNonExistentRole_ShouldReturnBadRequest() throws Exception {
        UserDTO invalidUserDTO = UserDTO.builder()
                .username("invalidUser")
                .email("invalid@example.com")
                .password("invalidPassword")
                .roles(Set.of("ROLE_UNKNOWN"))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignRoles_ShouldUpdateUserRoles() throws Exception {
        Set<String> newRoles = Set.of("ROLE_ADMIN", "ROLE_USER");

        // Create ROLE_USER first
        Role userRole = Role.builder()
                .name("ROLE_USER")
                .build();
        roleRepository.save(userRole);

        mockMvc.perform(post("/api/v1/{username}/roles", testUser.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRoles)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles.length()").value(2))
                .andExpect(jsonPath("$.roles").value(org.hamcrest.Matchers.containsInAnyOrder("ROLE_ADMIN", "ROLE_USER")));
    }

    @Test
    void deleteUser_ShouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/user/{id}", testUser.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users/{username}", testUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
