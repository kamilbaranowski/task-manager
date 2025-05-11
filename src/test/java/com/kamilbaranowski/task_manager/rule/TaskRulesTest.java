package com.kamilbaranowski.task_manager.rule;


import com.kamilbaranowski.taskmanager.rule.TaskRules;
import com.kamilbaranowski.taskmanager.task.model.Task;
import com.kamilbaranowski.taskmanager.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskRulesTest {

    private TaskRepository taskRepository;
    private TaskRules taskRules;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskRules = new TaskRules(taskRepository);
    }

    @Test
    void shouldPassWhenNoParentId() {
        assertDoesNotThrow(() -> taskRules.validateParent(1L, null));
    }

    @Test
    void shouldThrowWhenParentIsSameAsCurrent() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> taskRules.validateParent(1L, 1L));
        assertTrue(ex.getReason().contains("its own parent"));
    }

    @Test
    void shouldThrowWhenParentDoesNotExist() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> taskRules.validateParent(1L, 999L));
        assertTrue(ex.getReason().contains("not found"));
    }

    @Test
    void shouldThrowWhenCycleDetected() {
        //Given
        Task t1 = new Task();
        t1.setId(1L);
        t1.setParentId(2L);

        Task t2 = new Task();
        t2.setId(2L);
        t2.setParentId(1L); // -> cykl

        //When
        when(taskRepository.findById(2L)).thenReturn(Optional.of(t2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(t1));

        //Then
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> taskRules.validateParent(3L, 2L));
        assertTrue(ex.getReason().contains("cycle"));
    }

    @Test
    void shouldPassWhenValidParentHierarchy() {
        //Given
        Task parent = new Task();
        parent.setId(2L);
        parent.setParentId(null);

        //When
        when(taskRepository.findById(2L)).thenReturn(Optional.of(parent));

        //Then
        assertDoesNotThrow(() -> taskRules.validateParent(1L, 2L));
    }
}