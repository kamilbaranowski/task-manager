package com.kamilbaranowski.taskmanager.task.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskEventListener {

    private static final Logger logger = LoggerFactory.getLogger(TaskEventListener.class);

    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        logger.debug("[EVENT] Task created with ID: " + event.taskId());
    }

    @EventListener
    public void onTaskCompleted(TaskCompletedEvent event) {
        logger.debug(("[EVENT] Task completed with ID: " + event.taskId()));
    }

    @EventListener
    public void onTaskUpdated(TaskUpdatedEvent event) {
        logger.debug(("[EVENT] Task updated with ID: " + event.taskId()));
    }

    @EventListener
    public void onTaskDeleted(TaskDeletedEvent event) {
        logger.debug(("[EVENT] Task deleted with ID: " + event.taskId()));
    }

    @EventListener
    public void handleTaskCreated(TaskCreatedEvent event) {
        logger.debug(("Task created with ID: " + event.taskId()));
    }

    @EventListener
    public void handleTaskCompleted(TaskCompletedEvent event) {
        logger.debug(("Task completed with ID: " + event.taskId()));
    }
}
