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
        System.out.print("ПІБ: ");
        String name = scanner.nextLine();
        if (name.isBlank()) {
            System.out.println("Помилка: ПІБ не може бути порожнім!");
            return;
        }

        System.out.print("Група: ");
        String group = scanner.nextLine();

        // Спрощений приклад створення для Checkpoint 1
        Student student = new Student(name, LocalDate.of(2000, 1, 1),
                "email@ukma.edu", "000", 1, group, "ID-123");
        studentRepository.save(student);
        System.out.println("Студента успішно додано!");
    }

    private void showAllStudents() {
        var students = studentRepository.findAll();
        if (students.isEmpty()) {
            System.out.println("Список порожній.");
        } else {
            students.forEach(System.out::println);
        }
    }

    private void searchStudent() {
        System.out.print("Введіть ПІБ для пошуку: ");
        String name = scanner.nextLine();
        var result = studentRepository.findByName(name);
        result.forEach(System.out::println);
    }


}