package ua.university.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ua.university.exceptions.ValidationException;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Student.class, name = "student"),
    @JsonSubTypes.Type(value = Teacher.class, name = "teacher")
})
public abstract class Person {
    private final String id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private LocalDate birthDate;
    private String email;
    private String phone;

    protected Person() { this.id = UUID.randomUUID().toString(); }

    public Person(String firstName, String lastName, String patronymic,
                  LocalDate birthDate, String email, String phone) {
        if (firstName == null || firstName.isBlank()) throw new ValidationException("Ім'я не може бути порожнім");
        if (lastName == null || lastName.isBlank()) throw new ValidationException("Прізвище не може бути порожнім");
        if (email != null && !email.contains("@")) throw new ValidationException("Невірний формат email: " + email);
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.patronymic = patronymic != null ? patronymic.trim() : "";
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPatronymic() { return patronymic; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFullName() {
        return lastName + " " + firstName + (patronymic.isBlank() ? "" : " " + patronymic);
    }

    public int getAge() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person p)) return false;
        return Objects.equals(id, p.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
