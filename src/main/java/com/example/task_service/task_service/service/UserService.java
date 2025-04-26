package com.example.task_service.task_service.service;

import com.example.logexecution.annotation.LogExecutionTime;
import com.example.task_service.task_service.dto.UserDTO;
import com.example.task_service.task_service.entity.Role;
import com.example.task_service.task_service.entity.User;
import com.example.task_service.task_service.exception.BadRequestException;
import com.example.task_service.task_service.exception.RoleNotFoundException;
import com.example.task_service.task_service.exception.UserNotFoundException;
import com.example.task_service.task_service.mapper.UserMapper;
import com.example.task_service.task_service.repository.RoleRepository;
import com.example.task_service.task_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;


    @LogExecutionTime
    public UserDTO assignRolesToUser(String username, Set<String> roleNames) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("This User not found"));

        Set<Role> roles = roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new RoleNotFoundException("This Role not found: " + name)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @LogExecutionTime
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAllByActiveTrue(pageable)
                .map(userMapper::toDTO);
    }
    @LogExecutionTime
    public List<UserDTO> findByUsername(String username) {
        return userRepository.findByUsernameAndActiveTrue(username)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    @LogExecutionTime
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
            throw new BadRequestException("At least one role is required");
        }

        // Validate all roles exist before creating the user
        Set<Role> roles = userDTO.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BadRequestException("Role " + roleName + " not found")))
                .collect(Collectors.toSet());

        User user = userMapper.toEntity(userDTO, roleRepository);
        user.setRoles(roles);
        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @LogExecutionTime
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

}
