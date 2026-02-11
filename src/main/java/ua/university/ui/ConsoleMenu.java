package ua.university.ui;

import ua.university.domain.Student;
import ua.university.repository.StudentRepository;

import java.time.LocalDate;
import java.util.Scanner;

public class ConsoleMenu {
    private final StudentRepository studentRepository = new StudentRepository();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n--- DigiUni Registry: Checkpoint 1 ---");
            System.out.println("1. Додати студента");
            System.out.println("2. Показати всіх студентів");
            System.out.println("3. Пошук студента за ПІБ");
            System.out.println("0. Вихід");
            System.out.print("Виберіть опцію: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addStudent();
                case "2" -> showAllStudents();
                case "3" -> searchStudent();
                case "0" -> {
                    System.out.println("Вихід з програми...");
                    return;
                }
                default -> System.out.println("Помилка: невірний вибір!");
            }
        }
    }

    private void addStudent() {
        // --- ВАЛІДАЦІЯ ПІБ ---
        String name;
        while (true) {
            System.out.print("ПІБ (Прізвище Ім'я По батькові): ");
            name = scanner.nextLine().trim();

            if (name.isBlank()) {
                System.out.println("Помилка: ПІБ не може бути порожнім!");
                continue;
            }

            String[] parts = name.split("\\s+");
            if (parts.length < 3) {
                System.out.println("Помилка: Введіть ПІБ повністю (мінімум 3 слова)!");
                continue;
            }
            break;
        }

        // --- ВАЛІДАЦІЯ ГРУПИ ---
        String group;
        while (true) {
            System.out.print("Група (1-6): ");
            group = scanner.nextLine().trim();
            try {
                int groupNum = Integer.parseInt(group);
                if (groupNum < 1 || groupNum > 6) {
                    System.out.println("Помилка: номер групи має бути від 1 до 6!");
                    continue;
                }
                break; // вихід з циклу, якщо число 1-6
            } catch (NumberFormatException e) {
                System.out.println("Помилка: введіть число (цифру від 1 до 6)!");
            }
        }

        // --- ВАЛІДАЦІЯ КУРСУ ---
        int course = 1;
        while (true) {
            System.out.print("Курс (1-6): ");
            try {
                course = Integer.parseInt(scanner.nextLine());
                if (course < 1 || course > 6) {
                    System.out.println("Помилка: курс має бути від 1 до 6!");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Помилка: введіть число!");
            }
        }

        // Створення студента з валідованими даними
        Student student = new Student(name, LocalDate.of(2005, 1, 1),
                "student@ukma.edu", "00000", course, group, "ID-" + (int)(Math.random()*1000));

        studentRepository.save(student);
        System.out.println("Студента успішно додано до групи " + group + "!");
    }

    private void showAllStudents() {
        var students = studentRepository.findAll();
        if (students.isEmpty()) {
            System.out.println("Список порожній.");
        } else {
            System.out.println("--- Список всіх студентів ---");
            students.forEach(System.out::println);
        }
    }

    private void searchStudent() {
        System.out.print("Введіть повне ПІБ для пошуку: ");
        String name = scanner.nextLine().trim();

        // Додаткова перевірка: ПІБ для пошуку має бути мінімум 3 слова
        String[] parts = name.split("\\s+");
        if (parts.length < 3) {
            System.out.println("Помилка: Для точного пошуку введіть повне ПІБ (3 слова)!");
            return;
        }

        var result = studentRepository.findByName(name);

        if (result.isEmpty()) {
            System.out.println("Студентів з таким ПІБ не знайдено (перевірте правильність написання).");
        } else {
            System.out.println("Результати пошуку:");
            result.forEach(System.out::println);
        }

    }
}