package com.kamilbaranowski.taskmanager.task.exception;

public class TaskCompletionException extends RuntimeException {
    public TaskCompletionException(String message) {
        super(message);
    }

    public TaskCompletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
