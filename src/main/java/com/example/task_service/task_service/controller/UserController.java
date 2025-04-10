package com.example.task_service.task_service.controller;

import com.example.task_service.task_service.dto.UserDTO;
import com.example.task_service.task_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{username}")
    public List<UserDTO> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }


    @PostMapping("/{username}/roles")
    public UserDTO assignRoles(@PathVariable String username, @RequestBody Set<String> roleNames) {
        return userService.assignRolesToUser(username, roleNames);
    }

    @PostMapping("/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}

