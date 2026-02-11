package ua.university.domain;

import java.time.LocalDate;

public class Teacher extends Person {
    private String position;       // посада
    private String scientificDegree; // науковий ступінь
    private double salaryRate;     // ставка

    public Teacher(String fullName, LocalDate birthDate, String email, String phone,
                   String position, String scientificDegree, double salaryRate) {
        // Викликаємо конструктор батьківського класу Person
        super(fullName, birthDate, email, phone);
        this.position = position;
        this.scientificDegree = scientificDegree;
        this.salaryRate = salaryRate;
    }

    @Override
    public String toString() {
        return String.format("Викладач: %s | Посада: %s | Ступінь: %s",
                getFullName(), position, scientificDegree);
    }

    // Геттери
    public String getPosition() { return position; }
    public String getScientificDegree() { return scientificDegree; }
}