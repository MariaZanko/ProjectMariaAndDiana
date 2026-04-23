package ua.university;

import org.junit.jupiter.api.*;
import ua.university.domain.Teacher;
import ua.university.exceptions.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teacher Tests")
class TeacherTest {

    private Teacher makeTeacher() {
        return new Teacher("Марія", "Петренко", "Олексіївна",
                LocalDate.of(1975, 6, 10), "maria@ukma.edu", "+380671234567",
                "Доцент", "к.т.н.", "Доцент",
                LocalDate.of(2010, 9, 1), 1.0, "CS-101");
    }

    @Test
    @DisplayName("Створення викладача")
    void testCreate() {
        Teacher t = makeTeacher();
        assertEquals("Петренко Марія Олексіївна", t.getFullName());
        assertEquals("Доцент", t.getPosition());
    }

    @Test
    @DisplayName("Розрахунок стажу роботи")
    void testExperienceYears() {
        Teacher t = makeTeacher();
        assertTrue(t.getExperienceYears() >= 14);
    }

    @Test
    @DisplayName("Кидає виняток при порожньому імені")
    void testEmptyNameThrows() {
        assertThrows(ValidationException.class, () ->
            new Teacher("", "Петренко", "Олексіївна",
                    LocalDate.of(1975, 1, 1), "t@ukma.edu", "111",
                    "Доцент", "к.т.н.", "Доцент",
                    LocalDate.now(), 1.0, "CS-101"));
    }

    @Test
    @DisplayName("Невалідний email кидає виняток")
    void testInvalidEmail() {
        assertThrows(ValidationException.class, () ->
            new Teacher("Ганна", "Сидоренко", "",
                    LocalDate.of(1980, 1, 1), "not-an-email", "222",
                    "Асистент", "", "",
                    LocalDate.now(), 0.5, "CS-101"));
    }

    @Test
    @DisplayName("toString містить ПІБ")
    void testToString() {
        Teacher t = makeTeacher();
        assertTrue(t.toString().contains("Петренко"));
    }
}
