# DigiUni Registry — НаУКМА

Консольна інформаційна система обліку студентів і викладачів університету.

## Вимоги

- Java 17+
- Maven 3.8+

## Збірка та запуск

```bash
mvn compile
mvn exec:java -Dexec.mainClass="ua.university.Main"
```

## Запуск тестів

```bash
mvn test
```

## Акаунти за замовчуванням

| Логін    | Пароль      | Роль    |
|----------|-------------|---------|
| admin    | admin123    | ADMIN   |
| manager  | manager123  | MANAGER |
| user     | user123     | USER    |

## Структура проекту

```
src/
  main/java/ua/university/
    domain/         — сутності (Person, Student, Teacher, Faculty, Department, University)
    repository/     — репозиторії з дженеріками (Repository<T,ID>)
    service/        — бізнес-логіка (UniversityService, PersistenceService, AutoSaveService)
    auth/           — авторизація і ролі (AuthService, User, Role)
    exceptions/     — власні винятки
    ui/             — консольне меню (ConsoleMenu)
  test/java/ua/university/
    StudentTest.java
    TeacherTest.java
    StudentRepositoryTest.java
    UniversityServiceTest.java
    AuthServiceTest.java
```

## Покриття тем курсу

### Checkpoint 2
- ✅ Репозиторії на дженеріках `Repository<T, ID>` з використанням `List`, `Set`, `Map`
- ✅ `Optional` і власні винятки (`ValidationException`, `EntityNotFoundException`, `AccessDeniedException`)
- ✅ Лямбди / Stream API: `filter`, `sorted`, `groupingBy`, `Collectors`
- ✅ Java Time API: `LocalDate`, `Period` (вік особи, стаж викладача)
- ✅ 22 JUnit-тести (параметризовані включно)
- ✅ Авторизація: ролі USER / MANAGER / ADMIN з розмежуванням доступу

### Checkpoint 3
- ✅ Stream API для 2+ звітів і статистики
- ✅ `record` — `SearchResult<T>` у домені
- ✅ Збереження/завантаження даних (JSON + NIO.2 `Path/Files`)
- ✅ Повна рольова модель + управління користувачами (блокування, додавання)
- ✅ Багатопоточність — `AutoSaveService` (фонове автозбереження через `ScheduledExecutorService`)

## Дані зберігаються у

```
data/
  university.json
  faculties.json
  departments.json
  students.json
  teachers.json
logs/
  university.log
```

## Теги Git

- `checkpoint-1` — базова структура, CRUD студентів, ConsoleMenu
- `checkpoint-2` — репозиторії, тести, лямбди, авторизація
- `checkpoint-3` — I/O, Stream-звіти, автозбереження, повна рольова модель
