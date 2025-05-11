# Task Manager API

## Aplikacja do zarządzana zadaniami
    - Screaming Architecture 
    - tworzenia, edycji, usuwania i kończenia zadań
    - relacji nadrzędny-podrzędny
    - walidacji reguł biznesowych (np. tylko zadania `PENDING` mogą być edytowane lub zakończone)
    - zdarzeń domenowych (np. `TaskCompletedEvent`)
    - rejestrowania audytu wszystkich operacji na zadaniach
    - eksportu zadań do CSV
    - filtrowania, sortowania i paginacji (keyset) zadań

## Wykorzystywane technolgie
    - Java 21
    - Maven 3.8+
    - Baza danych H2 in - memory (ze względy na łatwość uruchomienia)

## 🧠 Decyzje projektowe

- **H2** — lekka baza w pamięci na potrzeby testów
- **DTO + walidacja** — `@Valid`, `@RequestBody`, `record` DTO
- **Własne reguły walidacji** w klasie `TaskRules` (np. cykle, parentId, itp.)
- **Zdarzenia domenowe** — użycie `ApplicationEventPublisher` do publikowania zmian (`TaskCompletedEvent`, `TaskUpdatedEvent`, `TaskDeletedEvent`)
- **Audyt** — operacje zapisywane do tabeli `audit_log` przez `AuditService`
- **Specyfikacja (JPA Criteria API)** — filtrowanie po statusie, widoczności i dacie
- **Eksport do CSV** — przez `CsvExporter`

## Sposób uruchomienia
    - mvn spring-boot:run
    - API można testować z wykorzystaniem Swagger bądź Postman
    - Baza danych działa na localhost port 8080 (szczegóły w pliku application.properties)
## Testowanie 
    - mvn test
