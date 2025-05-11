package com.kamilbaranowski.taskmanager.audit;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuditService {
    private final NamedParameterJdbcTemplate jdbc;
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final List<AuditEntry> entries = new ArrayList<>();


    public void recordChange(Long taskId, String op, String userId, Map<String, Object> changes) {
        final String sql = "INSERT INTO audit_log (task_id, operation, user_id, timestamp, changes) " +
                "VALUES (:taskId, :operation, :userId, :timestamp, :changes)";

        entries.add(new AuditEntry(taskId, op, userId, LocalDateTime.now(), changes));
        logger.info("AUDIT: {} task={}, user={}, changes={}", op, taskId, userId, changes);


        Map<String, Object> params = new HashMap<>();
        params.put("taskId", taskId);
        params.put("operation", op);
        params.put("userId", userId);
        params.put("timestamp", LocalDateTime.now());
        params.put("changes", changes.toString());

        jdbc.update(sql, params);

    }

    public List<AuditEntry> getChangesForTask(Long taskId) {
        return entries.stream().filter(e -> e.taskId().equals(taskId)).toList();
    }


}
