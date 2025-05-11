package com.kamilbaranowski.taskmanager.dto;

public record PatchTaskRequest(String title, String description, Long parentId) {}
