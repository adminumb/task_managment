package com.example.task_service.task_service.mapper;

import com.example.task_service.task_service.dto.UserDTO;
import com.example.task_service.task_service.entity.Role;
import com.example.task_service.task_service.entity.User;
import com.example.task_service.task_service.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserMapperTest {
    @InjectMocks
    private UserMapperImpl userMapper;

    @Mock
    private RoleRepository roleRepository;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("Pavel22");
        user.setEmail("pavel@example.com");
        user.setPassword("1234");

        Role role = new Role();
        role.setName("ROLE_ADMIN");
        user.setRoles(Set.of(role));
    }

    @Test
    void testToDTO() {
        // Setup
        UserDTO userDTO = userMapper.toDTO(user);

        // Assertions
        assertNotNull(userDTO);
        assertEquals("Pavel22", userDTO.getUsername());
        assertEquals("pavel@example.com", userDTO.getEmail());
        assertTrue(userDTO.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    void testToEntity() {
        // Setup
        Set<String> roleNames = Set.of("ROLE_ADMIN");
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(java.util.Optional.of(new Role("ROLE_ADMIN")));

        User userEntity = userMapper.toEntity(new UserDTO("Pavel22", "pavel@example.com", "1234", roleNames), roleRepository);

        // Assertions
        assertNotNull(userEntity);
        assertEquals("Pavel22", userEntity.getUsername());
        assertEquals("pavel@example.com", userEntity.getEmail());
        assertTrue(userEntity.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")));
    }

    @Test
    void testMapStringToRoleRoleNotFound() {
        // Setup
        Set<String> roleNames = Set.of("ROLE_UNKNOWN");

        // Test and Assertions
        assertThrows(RuntimeException.class, () -> userMapper.mapStringToRole(roleNames, roleRepository));
    }

    @Test
    void testMapRoleToString() {
        // Setup
        Role role = new Role("ROLE_USER");
        Set<Role> roles = Set.of(role);

        // Test
        Set<String> roleNames = userMapper.mapRoleToString(roles);

        // Assertions
        assertEquals(1, roleNames.size());
        assertTrue(roleNames.contains("ROLE_USER"));
    }
}
