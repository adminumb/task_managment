package com.example.task_service.task_service.repository;

import com.example.task_service.task_service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t JOIN FETCH t.user WHERE t.user.username = :username")
    List<Task> findByUserUsername(@Param("username") String username);


}
