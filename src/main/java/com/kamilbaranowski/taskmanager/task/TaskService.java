package com.kamilbaranowski.taskmanager.task;

import com.kamilbaranowski.taskmanager.audit.AuditEntry;
import com.kamilbaranowski.taskmanager.audit.AuditService;
import com.kamilbaranowski.taskmanager.dto.PatchTaskRequest;
import com.kamilbaranowski.taskmanager.rule.TaskRules;
import com.kamilbaranowski.taskmanager.task.event.TaskCompletedEvent;
import com.kamilbaranowski.taskmanager.task.event.TaskDeletedEvent;
import com.kamilbaranowski.taskmanager.task.event.TaskUpdatedEvent;
import com.kamilbaranowski.taskmanager.task.model.Task;
import com.kamilbaranowski.taskmanager.task.model.TaskCreateRequest;
import com.kamilbaranowski.taskmanager.task.model.TaskStatus;
import com.kamilbaranowski.taskmanager.task.model.TaskVisibility;
import com.kamilbaranowski.taskmanager.task.repository.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

@Service
public class TaskService {
    private final TaskRules taskRules;
    private final TaskRepository taskRepository;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    public TaskService(TaskRepository taskRepository, AuditService auditService, ApplicationEventPublisher publisher) {
        this.taskRepository = taskRepository;
        this.auditService = auditService;
        this.eventPublisher = publisher;
        this.taskRules = new TaskRules(taskRepository);
    }


    public Task createTask(TaskCreateRequest req, String userId) {
        Task task = new Task();
        task.setTitle(req.title());
        task.setDescription(req.description());
        task.setVisibility(req.visibility());
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setCreatedBy(userId);
        task.setDueDate(LocalDateTime.now().plusDays(14));

        if (req.parentId() != null) {
            taskRules.validateParent(null, req.parentId());
            task.setParentId(req.parentId());
        }

        task.setTaskCode(generateTaskCode());
        Optional.ofNullable(taskRepository.save(task))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent task with ID " + req.parentId() + " not found"));
        auditService.recordChange(task.getId(), "CREATED", userId, Map.of());

        return task;
    }
    public void patchTask(Long id, PatchTaskRequest req, String userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (task.getStatus() != TaskStatus.PENDING)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PENDING tasks can be edited");

        Map<String, Object> changes = new HashMap<>();

        if (req.title() != null && !req.title().equals(task.getTitle())) {
            changes.put("title", req.title());
            task.setTitle(req.title());
        }

        if (req.description() != null && !Objects.equals(req.description(), task.getDescription())) {
            changes.put("description", req.description());
            task.setDescription(req.description());
        }

        if (req.parentId() != null && !Objects.equals(req.parentId(), task.getParentId())) {
            changes.put("parentId", req.parentId());
            task.setParentId(req.parentId());
        }

        task.setModifiedAt(LocalDateTime.now());
        task.setModifiedBy(userId);
        taskRepository.save(task);

        auditService.recordChange(task.getId(), "UPDATED", userId, changes);
        eventPublisher.publishEvent(new TaskUpdatedEvent(task.getId()));
    }

    public void completeTask(Long id, String userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (task.getStatus() != TaskStatus.PENDING)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        String lowerTitle = task.getTitle().toLowerCase();
        if (lowerTitle.contains("wip") || lowerTitle.contains("draft"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title contains 'WIP' or 'Draft'");

        List<Task> subtasks = taskRepository.findAllByParentId(id);
        if (subtasks.stream().anyMatch(t -> t.getStatus() != TaskStatus.COMPLETED))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subtasks not completed");

        task.setStatus(TaskStatus.COMPLETED);
        task.setModifiedAt(LocalDateTime.now());
        task.setModifiedBy(userId);
        taskRepository.save(task);

        auditService.recordChange(task.getId(), "COMPLETED", userId, Map.of());
        eventPublisher.publishEvent(new TaskCompletedEvent(task.getId()));
    }

    public void deleteTask(Long id, String userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (task.getStatus() != TaskStatus.PENDING)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        deleteRecursive(task, userId);
    }

    private void deleteRecursive(Task task, String userId) {
        List<Task> subtasks = taskRepository.findAllByParentId(task.getId());
        for (Task sub : subtasks) {
            deleteRecursive(sub, userId);
        }
        taskRepository.delete(task);
        auditService.recordChange(task.getId(), "DELETED", userId, Map.of());
        eventPublisher.publishEvent(new TaskDeletedEvent(task.getId()));
    }

    public List<AuditEntry> getChangesForTask(Long taskId) {
        return auditService.getChangesForTask(taskId);
    }
    private synchronized String generateTaskCode() {
        int year = Year.now().getValue();
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year + 1, 1, 1, 0, 0);
        long count = taskRepository.countByCreatedAtBetween(startOfYear, endOfYear);
        return "TASK-" + year + "-" + (count + 1);
    }

    public List<Task> getTasks(TaskStatus status, TaskVisibility vis, LocalDateTime after, int limit) {
        Specification<Task> spec = Specification.where(null);

        if (status != null) spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        if (vis != null) spec = spec.and((root, q, cb) -> cb.equal(root.get("visibility"), vis));
        if (after != null) spec = spec.and((root, q, cb) -> cb.lessThan(root.get("createdAt"), after));

        return taskRepository.findAll(spec, PageRequest.of(0, limit, Sort.by("createdAt").descending())).getContent();
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
