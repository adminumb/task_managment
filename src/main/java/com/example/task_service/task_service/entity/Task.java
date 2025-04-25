    package com.example.task_service.task_service.entity;

    import jakarta.persistence.*;
    import lombok.*;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "task")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Task {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String title;

        @Column(nullable = true, columnDefinition = "text")
        private String description;
        private boolean completed;

        @Column
        private boolean active = true;

        @Column(nullable = false)
        @CreationTimestamp
        private LocalDateTime createdAt;

        @Column(nullable = false)
        @UpdateTimestamp
        private LocalDateTime updatedAt;

        @ManyToOne
        @JoinColumn(name = "user_id") // внешний ключ на пользователя
        private User user;

    }
