package com.kamilbaranowski.taskmanager.task.model;

import java.time.LocalDateTime;

public record TaskCreateRequest(
        String title,
        String description,
        TaskVisibility visibility,
        LocalDateTime createdAt,
        String modifiedBy,
        LocalDateTime dueDate,
        Long parentId,
        String takCode
) { }
