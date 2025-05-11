package com.kamilbaranowski.taskmanager.task.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Entity
@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskCode;

    @Column(nullable = false)
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private TaskVisibility visibility;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
    private LocalDateTime dueDate;

    private Long parentId;

    @Version
    private Long version;

    public void complete() {
        if (this.status != TaskStatus.PENDING) {
            throw new IllegalStateException("Only PENDING task can be completed");
        }
        this.status = TaskStatus.COMPLETED;
        this.modifiedAt = LocalDateTime.now();
    }

    public void expire() {
        if (this.status == TaskStatus.PENDING && this.dueDate.isBefore(LocalDateTime.now())) {
            this.status = TaskStatus.EXPIRED;
            this.modifiedAt = LocalDateTime.now();
        }
    }
}
