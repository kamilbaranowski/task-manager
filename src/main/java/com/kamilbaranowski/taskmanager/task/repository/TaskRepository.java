package com.kamilbaranowski.taskmanager.task.repository;

import com.kamilbaranowski.taskmanager.task.model.Task;
import com.kamilbaranowski.taskmanager.task.model.TaskStatus;
import com.kamilbaranowski.taskmanager.task.model.TaskVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findAllByParentId(Long parentId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
