package com.kamilbaranowski.taskmanager.rule;

import com.kamilbaranowski.taskmanager.task.model.Task;
import com.kamilbaranowski.taskmanager.task.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class TaskRules {
    private final TaskRepository taskRepository;

    public void validateParent(Long currentTaskId, Long newParentId) {
        if (newParentId == null) return;

        if (currentTaskId != null && currentTaskId.equals(newParentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task cannot be its own parent");
        }

        Set<Long> visited = new HashSet<>();
        Long parent = newParentId;

        while (parent != null) {
            if (!visited.add(parent)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent hierarchy contains a cycle");
            }

            Task t = taskRepository.findById(parent)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent with ID: " + newParentId + " not found"));

            parent = t.getParentId();
            if (parent != null && parent.equals(currentTaskId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent hierarchy contains a cycle");
            }
        }
    }
}
