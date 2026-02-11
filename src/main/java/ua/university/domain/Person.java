package ua.university.domain;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Person {
    private final String id;
    private String fullName;
    private LocalDate birthDate;
    private String email;
    private String phone;

    public Person(String fullName, LocalDate birthDate, String email, String phone) {
        this.id = UUID.randomUUID().toString(); // Унікальний ID
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
    }

    // Геттери та Сеттери (можна згенерувати або використати Lombok)
    public String getId() { return id; }
    public String getFullName() { return fullName; }
}