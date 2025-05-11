INSERT INTO task (title, description, status, visibility, created_at, created_by, due_date, task_code, version)
VALUES
('Zadanie testowe 1', 'Opis 1', 'PENDING', 'PUBLIC', CURRENT_TIMESTAMP, 'admin', CURRENT_DATE + 10, 'TASK-2025-1', 0),
('Zadanie testowe 2', 'Opis 2', 'PENDING', 'PRIVATE', CURRENT_TIMESTAMP, 'admin', CURRENT_DATE + 10, 'TASK-2025-2', 0);
