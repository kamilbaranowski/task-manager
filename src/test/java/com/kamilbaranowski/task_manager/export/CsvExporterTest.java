package com.kamilbaranowski.task_manager.export;

import com.kamilbaranowski.taskmanager.export.CsvExporter;
import com.kamilbaranowski.taskmanager.task.model.TaskStatus;
import com.kamilbaranowski.taskmanager.task.model.Task;
import com.kamilbaranowski.taskmanager.task.model.TaskVisibility;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvExporterTest {

    @Test
    void shouldExportTasksToCsv() {
        //Given
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test title");
        task.setDescription("Opis, z przecinkiem");
        task.setStatus(TaskStatus.PENDING);
        task.setVisibility(TaskVisibility.PUBLIC);
        task.setCreatedAt(LocalDateTime.of(2025, 5, 10, 12, 0));
        task.setCreatedBy("admin");
        task.setModifiedAt(LocalDateTime.of(2025, 5, 11, 12, 0));
        task.setModifiedBy("admin2");
        task.setDueDate(LocalDateTime.of(2025, 6, 11, 12, 0));
        task.setParentId(null);
        task.setTaskCode("TASK-2025-001");

        //When
        String csv = CsvExporter.getCsv(List.of(task));

        //Then
        assertThat(csv).startsWith("id,title,description");
        assertThat(csv).contains("Test title");
        assertThat(csv).contains("\"Opis, z przecinkiem\"");
        assertThat(csv).contains("TASK-2025-001");
    }

    @Test
    void shouldHandleEmptyList() {
        //Given
        String csv = CsvExporter.getCsv(List.of());

        //When & Then
        assertThat(csv).isEqualTo("id,title,description,status,visibility,createdAt,createdBy,modifiedAt,modifiedBy,dueDate,parentId,taskCode\n");
    }
}

