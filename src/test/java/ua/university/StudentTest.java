package ua.university;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ua.university.domain.Student;
import ua.university.exceptions.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Student Tests")
class StudentTest {

    private Student createStudent(int course, String group) {
        return new Student("Іван", "Шевченко", "Петрович",
                LocalDate.of(2002, 5, 15), "ivan@ukma.edu", "+380501234567",
                "ST-001", course, group, 2022, Student.StudyForm.BUDGET, "CS-101");
    }

    @Test
    @DisplayName("Створення студента з валідними даними")
    void testValidStudentCreation() {
        Student s = createStudent(2, "КН-21");
        assertEquals("Шевченко Іван Петрович", s.getFullName());
        assertEquals(2, s.getCourse());
        assertEquals("КН-21", s.getGroup());
        assertEquals(Student.Status.STUDYING, s.getStatus());
    }

    @Test
    @DisplayName("Кидає виняток при курсі поза межами")
    void testInvalidCourseThrows() {
        assertThrows(ValidationException.class, () -> createStudent(0, "КН-21"));
        assertThrows(ValidationException.class, () -> createStudent(7, "КН-21"));
    }

    @Test
    @DisplayName("Кидає виняток при порожній групі")
    void testEmptyGroupThrows() {
        assertThrows(ValidationException.class, () -> createStudent(1, ""));
        assertThrows(ValidationException.class, () -> createStudent(1, "  "));
    }

    @ParameterizedTest
    @DisplayName("Валідні курси 1-6")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6})
    void testValidCourses(int course) {
        Student s = createStudent(course, "ГР-1");
        assertEquals(course, s.getCourse());
    }

    @ParameterizedTest
    @DisplayName("Зміна статусу студента")
    @CsvSource({"STUDYING", "ACADEMIC_LEAVE", "EXPELLED"})
    void testStatusChange(String statusStr) {
        Student s = createStudent(1, "КН-11");
        Student.Status status = Student.Status.valueOf(statusStr);
        s.setStatus(status);
        assertEquals(status, s.getStatus());
    }

    @Test
    @DisplayName("Унікальний ID для кожного студента")
    void testUniqueIds() {
        Student s1 = createStudent(1, "А-1");
        Student s2 = createStudent(1, "А-1");
        assertNotEquals(s1.getId(), s2.getId());
    }

    @Test
    @DisplayName("Equals за ID")
    void testEqualsById() {
        Student s1 = createStudent(1, "А-1");
        assertEquals(s1, s1);
        Student s2 = createStudent(2, "А-2");
        assertNotEquals(s1, s2);
    }

    @Test
    @DisplayName("Optional email")
    void testOptionalEmail() {
        Student s = createStudent(1, "КН-11");
        assertTrue(s.getEmailOptional().isPresent());
    }

    @Test
    @DisplayName("Форма навчання за замовчуванням - BUDGET")
    void testDefaultStudyForm() {
        Student s = new Student("Олена", "Коваль", "Іванівна",
                LocalDate.of(2003, 3, 20), "olena@ukma.edu", "0991234567",
                "ST-002", 3, "МН-31", 2021, null, "MATH-101");
        assertEquals(Student.StudyForm.BUDGET, s.getStudyForm());
    }

    @Test
    @DisplayName("Перевід студента на іншу кафедру")
    void testTransfer() {
        Student s = createStudent(2, "КН-21");
        s.setDepartmentCode("NEW-DEPT");
        s.setGroup("НГ-21");
        assertEquals("NEW-DEPT", s.getDepartmentCode());
        assertEquals("НГ-21", s.getGroup());
    }
}
