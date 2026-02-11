package ua.university.domain;

import java.time.LocalDate;

public class Student extends Person {
    private int course;
    private String group;
    private String studentIdCard; // залікова

    public Student(String fullName, LocalDate birthDate, String email, String phone,
                   int course, String group, String studentIdCard) {
        super(fullName, birthDate, email, phone);
        this.course = course;
        this.group = group;
        this.studentIdCard = studentIdCard;
    }

    @Override
    public String toString() {
        return String.format("Студент: %s | Курс: %d | Група: %s | ID: %s",
                getFullName(), course, group, studentIdCard);
    }
    // Додай геттери/сеттери для курсу та групи
}