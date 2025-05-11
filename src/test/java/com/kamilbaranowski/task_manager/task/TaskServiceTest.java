package com.kamilbaranowski.task_manager.task;

import com.kamilbaranowski.taskmanager.audit.AuditService;
import com.kamilbaranowski.taskmanager.dto.PatchTaskRequest;
import com.kamilbaranowski.taskmanager.task.TaskService;
import com.kamilbaranowski.taskmanager.task.model.Task;
import com.kamilbaranowski.taskmanager.task.model.TaskStatus;
import com.kamilbaranowski.taskmanager.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;

public class TaskServiceTest {
    private TaskRepository repository;
    private AuditService auditService;
    private ApplicationEventPublisher publisher;
    private TaskService service;

    @BeforeEach
    void setUp() {
        repository = mock(TaskRepository.class);
        auditService = mock(AuditService.class);
        publisher = mock(ApplicationEventPublisher.class);
        service = new TaskService(repository, auditService, publisher);
    }
        @Test
        void shouldPatchTaskFields() {
            //Given
            Task task = new Task();
            task.setId(1L);
            task.setTitle("Old");
            task.setDescription("Old desc");
            task.setStatus(TaskStatus.PENDING);

            PatchTaskRequest req = new PatchTaskRequest("New", "New desc", null);

            //When
            when(repository.findById(1L)).thenReturn(Optional.of(task));

            service.patchTask(1L, req, "user");

            //Then
            verify(repository).save(any(Task.class));
            verify(auditService).recordChange(eq(1L), eq("UPDATED"), eq("user"), argThat(map ->
                    map.containsKey("title") && map.containsKey("description")));
        }

        @Test
        void shouldDeleteTaskAndSubtasks() {
            //Given
            Task parent = new Task();
            parent.setId(1L);
            parent.setStatus(TaskStatus.PENDING);

            Task child = new Task();
            child.setId(2L);
            child.setParentId(1L);
            child.setStatus(TaskStatus.PENDING);

            //When
            when(repository.findById(1L)).thenReturn(Optional.of(parent));
            when(repository.findAllByParentId(1L)).thenReturn(List.of(child));
            when(repository.findAllByParentId(2L)).thenReturn(List.of());

            service.deleteTask(1L, "admin");

            //Then
            verify(repository, times(1)).delete(child);
            verify(repository, times(1)).delete(parent);
            verify(auditService, times(2)).recordChange(anyLong(), eq("DELETED"), eq("admin"), anyMap());
        }
}
