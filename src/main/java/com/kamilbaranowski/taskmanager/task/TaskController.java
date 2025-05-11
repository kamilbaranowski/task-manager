package com.kamilbaranowski.taskmanager.task;

import com.kamilbaranowski.taskmanager.audit.AuditEntry;
import com.kamilbaranowski.taskmanager.audit.AuditService;
import com.kamilbaranowski.taskmanager.dto.PatchTaskRequest;
import com.kamilbaranowski.taskmanager.export.CsvExporter;
import com.kamilbaranowski.taskmanager.task.model.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final AuditService auditService;

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(
            @RequestBody @Valid TaskCreateRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        Task task = taskService.createTask(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<?> patchTask(
            @PathVariable Long id,
            @RequestBody PatchTaskRequest request,
            @RequestHeader("X-User-Id") String userId) {

        taskService.patchTask(id, request, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/tasks/{id}/complete")
    public ResponseEntity<Void> completeTask(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId
    ) {
        taskService.completeTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId
    ) {
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/{id}/changes")
    public ResponseEntity<List<AuditEntry>> getChanges(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getChangesForTask(id));
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskVisibility visibility,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(taskService.getTasks(status, visibility, after, limit));
    }
    @GetMapping("/tasks/export")
    public ResponseEntity<?> exportTasks(@RequestParam(defaultValue = "json") String format) {
        List<Task> tasks = taskService.getAllTasks();

        if (format.equalsIgnoreCase("csv")) {

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks.csv")
                    .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                    .body(CsvExporter.getCsv(tasks));
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(tasks);
    }
}
