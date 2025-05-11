package com.kamilbaranowski.taskmanager.audit;

import java.time.LocalDateTime;
import java.util.Map;

public record AuditEntry(
        Long taskId,
        String operation,
        String userId,
        LocalDateTime timestamp,
        Map<String, Object> changes
) {}
