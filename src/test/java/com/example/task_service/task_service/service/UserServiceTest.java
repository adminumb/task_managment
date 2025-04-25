package com.example.task_service.task_service.service;

import com.example.task_service.task_service.dto.UserDTO;
import com.example.task_service.task_service.entity.Role;
import com.example.task_service.task_service.entity.User;
import com.example.task_service.task_service.exception.BadRequestException;
import com.example.task_service.task_service.exception.RoleNotFoundException;
import com.example.task_service.task_service.exception.UserNotFoundException;
import com.example.task_service.task_service.mapper.UserMapper;
import com.example.task_service.task_service.repository.RoleRepository;
import com.example.task_service.task_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User userEntity;
    private Role roleEntity;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        userDTO = UserDTO.builder()
                .username("Pavel22")
                .email("pavel@example.com")
                .password("1234")
                .roles(Set.of("ROLE_ADMIN"))
                .build();

        roleEntity = new Role("ROLE_ADMIN");

        userEntity = User.builder()
                .id(1L)
                .username("Pavel22")
                .email("pavel@example.com")
                .password("1234")
                .roles(Set.of(roleEntity))
                .build();

        userList = Arrays.asList(userEntity);
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleEntity));
        when(userMapper.toEntity(userDTO, roleRepository)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        UserDTO createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("Pavel22", createdUser.getUsername());
        assertEquals("pavel@example.com", createdUser.getEmail());
        assertTrue(createdUser.getRoles().contains("ROLE_ADMIN"));

        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(userMapper).toEntity(userDTO, roleRepository);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDTO(userEntity);
    }

    @Test
    void createUser_WithInvalidRole_ShouldThrowException() {
        UserDTO invalidUserDTO = UserDTO.builder()
                .username("Pavel22")
                .email("pavel@example.com")
                .password("1234")
                .roles(Set.of("ROLE_UNKNOWN"))
                .build();

        when(roleRepository.findByName("ROLE_UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> userService.createUser(invalidUserDTO));

        verify(roleRepository).findByName("ROLE_UNKNOWN");
        verify(userMapper, never()).toEntity(any(), any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDTO(any());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(userList, pageable, 10);

        when(userRepository.findAllByActiveTrue(pageable)).thenReturn(userPage);
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        Page<UserDTO> users = userService.getAllUsers(pageable);

        assertNotNull(users);
        assertEquals(10, users.getTotalElements());
        assertEquals(1, users.getTotalPages());
        assertEquals(0, users.getNumber());
        assertEquals(10, users.getSize());
        assertEquals(userDTO, users.getContent().get(0));

        verify(userRepository).findAllByActiveTrue(pageable);
        verify(userMapper).toDTO(userEntity);
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        when(userRepository.findByUsernameAndActiveTrue("Pavel22")).thenReturn(Optional.of(userEntity));
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        List<UserDTO> users = userService.findByUsername("Pavel22");

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("Pavel22", users.get(0).getUsername());
        assertEquals("pavel@example.com", users.get(0).getEmail());

        verify(userRepository).findByUsernameAndActiveTrue("Pavel22");
        verify(userMapper).toDTO(userEntity);
    }

    @Test
    void findByUsername_WhenUserNotFound_ShouldReturnEmptyList() {
        when(userRepository.findByUsernameAndActiveTrue("UnknownUser")).thenReturn(Optional.empty());

        List<UserDTO> users = userService.findByUsername("UnknownUser");

        assertNotNull(users);
        assertTrue(users.isEmpty());
        verify(userRepository).findByUsernameAndActiveTrue("UnknownUser");
        verify(userMapper, never()).toDTO(any());
    }

    @Test
    void assignRolesToUser_ShouldUpdateAndReturnUser() {
        Set<String> newRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
        Role newRole = new Role("ROLE_USER");
        Set<Role> roles = Set.of(roleEntity, newRole);

        when(userRepository.findByUsername("Pavel22")).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleEntity));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(newRole));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        UserDTO updatedUser = userService.assignRolesToUser("Pavel22", newRoles);

        assertNotNull(updatedUser);
        assertEquals("Pavel22", updatedUser.getUsername());
        verify(userRepository).findByUsername("Pavel22");
        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(roleRepository).findByName("ROLE_USER");
        verify(userRepository).save(userEntity);
        verify(userMapper).toDTO(userEntity);
    }

    @Test
    void assignRolesToUser_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByUsername("UnknownUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> userService.assignRolesToUser("UnknownUser", Set.of("ROLE_ADMIN")));

        verify(userRepository).findByUsername("UnknownUser");
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void assignRolesToUser_WhenRoleNotFound_ShouldThrowException() {
        when(userRepository.findByUsername("Pavel22")).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName("ROLE_UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class,
            () -> userService.assignRolesToUser("Pavel22", Set.of("ROLE_UNKNOWN")));

        verify(userRepository).findByUsername("Pavel22");
        verify(roleRepository).findByName("ROLE_UNKNOWN");
        verify(userRepository, never()).save(any());
    }
}
