package com.example.task_service.task_service.repository;

import com.example.task_service.task_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.active = true")
    Optional<User> findByUsernameAndActiveTrue(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.active = true")
    Page<User> findAllByActiveTrue(Pageable pageable);

}
