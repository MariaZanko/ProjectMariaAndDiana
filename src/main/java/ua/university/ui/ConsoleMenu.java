package ua.university.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.auth.*;
import ua.university.domain.*;
import ua.university.exceptions.*;
import ua.university.service.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ConsoleMenu {
    private static final Logger log = LoggerFactory.getLogger(ConsoleMenu.class);
    private final Scanner scanner = new Scanner(System.in);
    private final DataStore store = new DataStore();
    private final UniversityService service = new UniversityService(store);
    private final PersistenceService persistence = new PersistenceService(store);
    private final AutoSaveService autoSave = new AutoSaveService(persistence);
    private final AuthService auth = new AuthService();

    public void start() {
        persistence.loadAll();
        autoSave.start(120); // auto-save every 2 minutes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            persistence.saveAll();
            autoSave.stop();
        }));

        System.out.println("=== DigiUni Registry - НаУКМА ===");
        if (!doLogin()) return;

        while (true) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            try {
                if (!handleMain(choice)) break;
            } catch (AccessDeniedException e) {
                System.out.println("Доступ заборонено: " + e.getMessage());
            } catch (UniversityException e) {
                System.out.println("Помилка: " + e.getMessage());
                log.warn("University exception: {}", e.getMessage());
            } catch (Exception e) {
                System.out.println("Системна помилка: " + e.getMessage());
                log.error("Unexpected error", e);
            }
        }
        persistence.saveAll();
        autoSave.stop();
        System.out.println("До побачення!");
    }

    private boolean doLogin() {
        for (int i = 0; i < 3; i++) {
            System.out.print("Логін: ");
            String login = scanner.nextLine().trim();
            System.out.print("Пароль: ");
            String pass = scanner.nextLine().trim();
            if (auth.login(login, pass)) {
                System.out.println("Вітаємо, " + auth.getCurrentUser().getUsername() +
                        "! Роль: " + auth.getCurrentUser().getRole());
                return true;
            }
            System.out.println("Невірний логін/пароль. Спробуйте ще раз.");
        }
        System.out.println("Перевищено кількість спроб.");
        return false;
    }

    private void printMainMenu() {
        Role role = auth.getCurrentUser().getRole();
        System.out.println("\n--- Головне меню (" + role + ") ---");
        System.out.println("1. Факультети і кафедри");
        System.out.println("2. Студенти");
        System.out.println("3. Викладачі");

        // Тільки для персоналу (Manager/Admin)
        if (role != Role.USER) {
            System.out.println("4. Пошук і звіти");
            System.out.println("5. Управління даними");
        }

        // Тільки для адміна
        if (role == Role.ADMIN) {
            System.out.println("6. Адміністрування");
        }

        System.out.println("7. Зберегти дані");
        System.out.println("0. Вихід");
        System.out.print(">> ");
    }

    private boolean handleMain(String c) {
        switch (c) {
            case "1" -> facultyMenu();
            case "2" -> studentMenu();
            case "3" -> teacherMenu();
            case "4" -> reportsMenu();
            case "5" -> { auth.requireRole(Role.MANAGER); dataManagementMenu(); }
            case "6" -> { auth.requireRole(Role.ADMIN); adminMenu(); }
            case "7" -> persistence.saveAll();
            case "0" -> { return false; }
            default -> System.out.println("Невірний вибір.");
        }
        return true;
    }

    // ========== FACULTY ==========
    private void facultyMenu() {
        Role role = auth.getCurrentUser().getRole();
        System.out.println("\n--- Факультети і кафедри ---");
        System.out.println("1. Список факультетів");
        System.out.println("3. Кафедри факультету");

        if (role != Role.USER) {
            System.out.println("2. Додати факультет");
            System.out.println("4. Додати кафедру");
        }
        System.out.println("0. Назад");
        System.out.print(">> ");

        String choice = scanner.nextLine().trim();
        if (role == Role.USER && (choice.equals("2") || choice.equals("4"))) {
            System.out.println("Невірний вибір.");
            return;
        }

        switch (choice) {
            case "1" -> listFaculties();
            case "2" -> addFaculty();
            case "3" -> listDepartments();
            case "4" -> addDepartment();
        }
    }

    private void listFaculties() {
        List<Faculty> all = service.getAllFaculties();
        if (all.isEmpty()) { System.out.println("Факультетів немає."); return; }
        all.forEach(f -> System.out.printf("  [%s] %s%n", f.getCode(), f.getName()));
    }

    private void addFaculty() {
        System.out.print("Код факультету: "); String code = scanner.nextLine().trim();
        System.out.print("Назва: "); String name = scanner.nextLine().trim();
        System.out.print("Скорочена назва: "); String sname = scanner.nextLine().trim();
        System.out.print("Контакти: "); String contacts = scanner.nextLine().trim();
        service.addFaculty(new Faculty(code, name, sname, null, contacts));
        System.out.println("Факультет додано.");
    }

    private void listDepartments() {
        System.out.print("Код факультету: "); String code = scanner.nextLine().trim();
        service.getDepartmentsByFaculty(code).forEach(d -> System.out.println("  " + d));
    }

    private void addDepartment() {
        System.out.print("Код кафедри: "); String code = scanner.nextLine().trim();
        System.out.print("Назва: "); String name = scanner.nextLine().trim();
        System.out.print("Код факультету: "); String fCode = scanner.nextLine().trim();
        System.out.print("Локація (кабінет): "); String loc = scanner.nextLine().trim();
        service.addDepartment(new Department(code, name, null, loc, fCode));
        System.out.println("Кафедру додано.");
    }

    // ========== STUDENTS ==========
    private void studentMenu() {
        Role role = auth.getCurrentUser().getRole();
        System.out.println("\n--- Студенти ---");

        if (role != Role.USER) System.out.println("1. Додати студента");

        System.out.println("2. Показати всіх (за курсами)");
        System.out.println("3. Пошук за ПІБ");
        System.out.println("4. Пошук за групою");

        if (role != Role.USER) {
            System.out.println("5. Перевести студента");
            System.out.println("6. Видалити студента");
        }
        System.out.println("0. Назад");
        System.out.print(">> ");

        String choice = scanner.nextLine().trim();

        // Блокуємо "невидимі" пункти, щоб вони видавали "Невірний вибір"
        boolean isManagerOp = choice.equals("1") || choice.equals("5") || choice.equals("6");
        if (role == Role.USER && isManagerOp) {
            System.out.println("Невірний вибір.");
            return;
        }

        switch (choice) {
            case "1" -> addStudent();
            case "2" -> showAllStudentsByCourse();
            case "3" -> searchStudentByName();
            case "4" -> searchStudentByGroup();
            case "5" -> transferStudent();
            case "6" -> deleteStudent();
            case "0" -> {}
            default -> System.out.println("Невірний вибір.");
        }
    }

    private void addStudent() {
        System.out.println("\n--- Реєстрація студента ---");

        // Циклічна валідація ПІБ
        String last = promptUntilValid("Прізвище: ", s -> !s.isBlank(), "Прізвище не може бути порожнім");
        String first = promptUntilValid("Ім'я: ", s -> !s.isBlank(), "Ім'я не може бути порожнім");
        String pat = promptUntilValid("По батькові: ", s -> !s.isBlank(), "По батькові не може бути порожнім");

        // Валідація телефону через Regex
        String phone = promptUntilValid("Телефон (+380...): ",
                s -> s.matches("^\\+380\\d{9}$"),
                "Невірний формат! Введіть +380 та 9 цифр");

        String email = promptUntilValid("Email: ", s -> s.contains("@"), "Email повинен містити @");

        // Використовуємо твій існуючий readInt [cite: 2]
        int course = readInt("Курс (1-6): ", 1, 6);

        System.out.print("Група: ");
        String group = scanner.nextLine().trim();

        // Циклічний пошук кафедри через Stream API
        Department dept = null;
        while (dept == null) {
            System.out.print("Код кафедри (або 'list'): ");
            String deptCode = scanner.nextLine().trim();

            if (deptCode.equalsIgnoreCase("list")) {
                store.getDepartments().findAll().forEach(d ->
                        System.out.println("  - " + d.getCode() + ": " + d.getName()));
                continue;
            }

            var found = store.getDepartments().findByCode(deptCode);
            if (found.isPresent()) {
                dept = found.get();
            } else {
                System.out.println(" Кафедру '" + deptCode + "' не знайдено!");
            }
        }

        System.out.print("Форма навчання (BUDGET/CONTRACT): ");
        String formStr = scanner.nextLine().trim().toUpperCase();
        Student.StudyForm form = formStr.equals("CONTRACT") ? Student.StudyForm.CONTRACT : Student.StudyForm.BUDGET;

        String sid = "ST-" + System.currentTimeMillis() % 100000;

        // Створюємо об'єкт з валідованими даними
        Student s = new Student(first, last, pat, LocalDate.of(2005, 1, 1), email, phone,
                sid, course, group, LocalDate.now().getYear(), form, dept.getCode());

        service.addStudent(s);
        System.out.println("✅ Студента додано до кафедри: " + dept.getName());
    }

    private void showAllStudentsByCourse() {
        Map<Integer, List<Student>> grouped = service.getAllStudentsGroupedByCourse();
        if (grouped.isEmpty()) { System.out.println("Студентів немає."); return; }
        new TreeMap<>(grouped).forEach((course, students) -> {
            System.out.println("\n-- Курс " + course + " --");
            students.forEach(st -> System.out.println("  " + st));
        });
    }

    private void searchStudentByName() {
        System.out.print("Пошук за ПІБ: "); String q = scanner.nextLine().trim();
        List<Student> res = service.searchStudentsByName(q);
        if (res.isEmpty()) System.out.println("Не знайдено.");
        else res.forEach(s -> System.out.println("  [" + s.getId() + "] " + s));
    }

    private void searchStudentByGroup() {
        System.out.print("Група: "); String g = scanner.nextLine().trim();
        service.getStudentsByGroup(g).forEach(s -> System.out.println("  " + s));
    }

    private void transferStudent() {
        System.out.println("\n--- Переведення студента ---");

        // 1. Пошук студента
        Student student = null;
        while (student == null) {
            System.out.print("ID студента (або 'search' для пошуку, або 'back'): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("back")) return;

            // Допомога менеджеру, який не знає ID напам'ять
            if (input.equalsIgnoreCase("search")) {
                System.out.print("Введіть Прізвище або частину ПІБ: ");
                String query = scanner.nextLine().trim();
                var results = service.searchStudentsByName(query);
                if (results.isEmpty()) {
                    System.out.println("⚠️ Нікого не знайдено.");
                } else {
                    System.out.println("Знайдені студенти:");
                    results.forEach(s -> System.out.printf("  ID: %s | %s %s (Курс: %d, Група: %s)%n",
                            s.getId(), s.getLastName(), s.getFirstName(), s.getCourse(), s.getGroup()));
                }
                continue; // Повертаємось до запиту ID
            }

            // Пошук за конкретним ID
            var found = store.getStudents().findById(input);
            if (found.isPresent()) {
                student = found.get();
                System.out.println("✅ Обрано студента: " + student.getLastName() + " " + student.getFirstName());
            } else {
                System.out.println("❌ Помилка: Студента з ID '" + input + "' не знайдено.");
            }
        }

        // 2. Валідація нової кафедри
        Department newDept = null;
        while (newDept == null) {
            System.out.print("Новий код кафедри (або 'list'): ");
            String deptCode = scanner.nextLine().trim();

            if (deptCode.equalsIgnoreCase("list")) {
                store.getDepartments().findAll().forEach(d ->
                        System.out.println("  - " + d.getCode() + ": " + d.getName()));
                continue;
            }

            var found = store.getDepartments().findByCode(deptCode);
            if (found.isPresent()) {
                newDept = found.get();
            } else {
                System.out.println("❌ Кафедру '" + deptCode + "' не знайдено!");
            }
        }

        // 3. Валідація групи (вимоги: цифра 1-6)
        String newGroup = promptUntilValid("Нова група (1-6): ",
                s -> s.matches("[1-6]"),
                "Група повинна бути цифрою від 1 до 6");

        // 4. Фінальна операція
        try {
            service.transferStudent(student.getId(), newDept.getCode(), newGroup);
            System.out.println("✅ Успішно! Студент переведений на кафедру " + newDept.getName() + " (Група: " + newGroup + ")");
        } catch (Exception e) {
            System.out.println("❌ Помилка під час виконання: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        System.out.print("ID студента: "); String id = scanner.nextLine().trim();
        service.deleteStudent(id);
        System.out.println("Студента видалено.");
    }

    // ========== TEACHERS ==========
    private void teacherMenu() {
        Role role = auth.getCurrentUser().getRole();
        System.out.println("\n--- Викладачі ---");

        if (role != Role.USER) {
            System.out.println("1. Додати викладача");
        }
        System.out.println("2. Список всіх викладачів");
        System.out.println("3. Пошук за ПІБ");
        System.out.println("0. Назад");
        System.out.print(">> ");

        String choice = scanner.nextLine().trim();

        if (role == Role.USER && choice.equals("1")) {
            System.out.println("Невірний вибір.");
            return;
        }

        switch (choice) {
            case "1" -> { auth.requireRole(Role.MANAGER); addTeacher(); }
            case "2" -> store.getTeachers().findAll().forEach(t -> System.out.println("  [" + t.getId() + "] " + t));
            case "3" -> {
                System.out.print("Пошук: ");
                service.searchTeachersByName(scanner.nextLine().trim()).forEach(t -> System.out.println("  " + t));
            }
            case "0" -> {}
            default -> System.out.println("Невірний вибір.");
        }
    }

    private void addTeacher() {
        System.out.print("Прізвище: "); String last = scanner.nextLine().trim();
        System.out.print("Ім'я: "); String first = scanner.nextLine().trim();
        System.out.print("По батькові: "); String pat = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Телефон: "); String phone = scanner.nextLine().trim();
        System.out.print("Посада: "); String pos = scanner.nextLine().trim();
        System.out.print("Науковий ступінь: "); String deg = scanner.nextLine().trim();
        System.out.print("Вчене звання: "); String title = scanner.nextLine().trim();
        System.out.print("Код кафедри: "); String dept = scanner.nextLine().trim();
        Teacher t = new Teacher(first, last, pat, LocalDate.of(1980, 1, 1), email, phone,
                pos, deg, title, LocalDate.now(), 1.0, dept);
        service.addTeacher(t);
        System.out.println("Викладача додано. ID: " + t.getId());
    }

    // ========== REPORTS ==========
    private void reportsMenu() {
        System.out.println("\n--- Звіти ---");
        System.out.println("1. Студенти факультету (алфавіт)");
        System.out.println("2. Викладачі факультету (алфавіт)");
        System.out.println("3. Студенти кафедри (алфавіт)");
        System.out.println("4. Студенти кафедри за курсами");
        System.out.println("5. Студенти кафедри конкретного курсу");
        System.out.println("6. Статистика по кафедрах");
        System.out.print(">> ");
            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Код факультету: "); String fc = scanner.nextLine().trim();
                    var list = service.getStudentsByFacultyAlpha(fc);
                    if (list.isEmpty()) System.out.println("⚠️ На факультеті '" + fc + "' студентів не знайдено.");
                    else list.forEach(s -> System.out.println("  " + s));
                }
                case "2" -> {
                    System.out.print("Код факультету: "); String fc = scanner.nextLine().trim();
                    var list = service.getTeachersByFacultyAlpha(fc);
                    if (list.isEmpty()) System.out.println("⚠️ На факультеті '" + fc + "' викладачів не знайдено.");
                    else list.forEach(t -> System.out.println("  " + t));
                }
                case "3" -> {
                    System.out.print("Код кафедри: "); String dc = scanner.nextLine().trim();
                    var list = service.getStudentsByDepartmentAlpha(dc);
                    if (list.isEmpty()) System.out.println("⚠️ На кафедрі '" + dc + "' студентів не знайдено.");
                    else list.forEach(s -> System.out.println("  " + s));
                }
                case "4" -> {
                    System.out.print("Код кафедри: "); String dc = scanner.nextLine().trim();
                    var list = service.getAllStudentsSortedByCourse().stream()
                            .filter(s -> dc.equalsIgnoreCase(s.getDepartmentCode())) // Додав ignoreCase про всяк випадок
                            .collect(Collectors.toList());

                    if (list.isEmpty()) System.out.println("⚠️ На кафедрі '" + dc + "' немає жодного студента.");
                    else list.forEach(s -> System.out.println("  " + s));
                }
                case "5" -> {
                    System.out.print("Код кафедри: "); String dc = scanner.nextLine().trim();
                    int c = readInt("Курс: ", 1, 6);
                    var list = service.getStudentsByDepartmentAndCourse(dc, c);
                    if (list.isEmpty()) System.out.println("⚠️ Студентів " + c + "-го курсу на кафедрі '" + dc + "' не знайдено.");
                    else list.forEach(s -> System.out.println("  " + s));
                }
                case "6" -> {
                    var stats = service.getStudentCountByDepartment();
                    if (stats.isEmpty()) {
                        System.out.println("⚠️ Статистика недоступна: дані про студентів та кафедри відсутні.");
                    } else {
                        stats.forEach((dept, cnt) ->
                                System.out.printf("  Кафедра [%s]: %d студ.%n", dept, cnt));
                    }
                }
            }
        }

    // ========== DATA MANAGEMENT ==========
    private void dataManagementMenu() {
        System.out.println("\n--- Управління ---");
        System.out.println("1. Редагувати факультет");
        System.out.println("2. Видалити факультет");
        System.out.println("3. Видалити кафедру");
        System.out.print(">> ");
        switch (scanner.nextLine().trim()) {
            case "1" -> editFaculty();
            case "2" -> { System.out.print("Код факультету: "); service.deleteFaculty(scanner.nextLine().trim()); System.out.println("Видалено."); }
            case "3" -> { System.out.print("Код кафедри: "); service.deleteDepartment(scanner.nextLine().trim()); System.out.println("Видалено."); }
        }
    }

    private void editFaculty() {
        System.out.print("Код факультету: "); String code = scanner.nextLine().trim();
        Faculty f = service.getFaculty(code);
        System.out.print("Нова назва (Enter = пропустити): "); String name = scanner.nextLine().trim();
        if (!name.isBlank()) f.setName(name);
        System.out.print("Нові контакти (Enter = пропустити): "); String contacts = scanner.nextLine().trim();
        if (!contacts.isBlank()) f.setContacts(contacts);
        store.getFaculties().update(f);
        System.out.println("Факультет оновлено.");
    }

    // ========== ADMIN ==========
    private void adminMenu() {
        System.out.println("\n--- Адміністрування ---");
        System.out.println("1. Список користувачів");
        System.out.println("2. Додати користувача");
        System.out.println("3. Заблокувати користувача");
        System.out.println("4. Розблокувати користувача");
        System.out.print(">> ");
        switch (scanner.nextLine().trim()) {
            case "1" -> auth.getAllUsers().forEach(u -> System.out.println("  " + u));
            case "2" -> {
                System.out.print("Логін: "); String u = scanner.nextLine().trim();
                System.out.print("Пароль: "); String p = scanner.nextLine().trim();
                System.out.print("Роль (USER/MANAGER/ADMIN): ");
                Role r = Role.valueOf(scanner.nextLine().trim().toUpperCase());
                auth.addUser(u, p, r);
                System.out.println("Користувача додано.");
            }
            case "3" -> { System.out.print("Логін: "); auth.blockUser(scanner.nextLine().trim()); System.out.println("Заблоковано."); }
            case "4" -> { System.out.print("Логін: "); auth.unblockUser(scanner.nextLine().trim()); System.out.println("Розблоковано."); }
        }
    }

    // ========== UTILS ==========
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(scanner.nextLine().trim());
                if (v >= min && v <= max) return v;
                System.out.printf("Введіть число від %d до %d%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Невірне число.");
            }
        }
    }

    private String promptUntilValid(String prompt, java.util.function.Predicate<String> validator, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.println("⚠️ " + errorMsg);
        }
    }


}
