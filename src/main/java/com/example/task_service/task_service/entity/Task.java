    package com.example.task_service.task_service.entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "task")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Task {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @Column(nullable = true, columnDefinition = "text")
        private String description;
        private boolean completed;

        @Column(nullable = false)
        @CreationTimestamp
        private LocalDateTime createdAt;

        @Column(nullable = false)
        @UpdateTimestamp
        private LocalDateTime updatedAt;

        @ManyToOne
        @JoinColumn(name = "user_id") // внешний ключ на пользователя
        private User user;


        public Task(String name, String description, boolean completed) {
            this.name = name;
            this.description = description;
            this.completed = completed;
        }

    }

