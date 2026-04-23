package ua.university;

import org.junit.jupiter.api.*;
import ua.university.domain.*;
import ua.university.exceptions.*;
import ua.university.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UniversityService Tests")
class UniversityServiceTest {

    private DataStore store;
    private UniversityService service;

    @BeforeEach
    void setup() {
        store = new DataStore();
        service = new UniversityService(store);

        service.addFaculty(new Faculty("FAC-01", "Факультет інформатики", "ФІ", null, "info@ukma.edu"));
        service.addDepartment(new Department("DEPT-01", "Кафедра КН", null, "Корп. 1, 101", "FAC-01"));
    }

    private Student makeStudent(int course, String dept) {
        return new Student("Тест", "Студент", "Ович",
                LocalDate.of(2002, 1, 1), "t@ukma.edu", "000",
                "ST-" + System.nanoTime(), course, "ГР-" + course + "1",
                2022, Student.StudyForm.BUDGET, dept);
    }

    @Test
    @DisplayName("Додавання факультету — дублікат кидає виняток")
    void testAddFacultyDuplicate() {
        assertThrows(UniversityException.class, () ->
            service.addFaculty(new Faculty("FAC-01", "Інший", "ін", null, "")));
    }

    @Test
    @DisplayName("Додавання кафедри до неіснуючого факультету")
    void testAddDeptToNonExistentFaculty() {
        assertThrows(EntityNotFoundException.class, () ->
            service.addDepartment(new Department("X", "X", null, "X", "NO-FACULTY")));
    }

    @Test
    @DisplayName("Додавання та пошук студента")
    void testAddAndFindStudent() {
        Student s = makeStudent(1, "DEPT-01");
        service.addStudent(s);
        assertEquals(s, service.getStudent(s.getId()));
    }

    @Test
    @DisplayName("Студент на неіснуючій кафедрі — виняток")
    void testAddStudentBadDept() {
        Student s = makeStudent(1, "NO-DEPT");
        assertThrows(EntityNotFoundException.class, () -> service.addStudent(s));
    }

    @Test
    @DisplayName("Перевід студента")
    void testTransferStudent() {
        service.addDepartment(new Department("DEPT-02", "Кафедра 2", null, "Корп. 2", "FAC-01"));
        Student s = makeStudent(2, "DEPT-01");
        service.addStudent(s);
        service.transferStudent(s.getId(), "DEPT-02", "НГ-21");
        Student updated = service.getStudent(s.getId());
        assertEquals("DEPT-02", updated.getDepartmentCode());
        assertEquals("НГ-21", updated.getGroup());
    }

    @Test
    @DisplayName("Групування студентів за курсами")
    void testGroupByCourse() {
        service.addStudent(makeStudent(1, "DEPT-01"));
        service.addStudent(makeStudent(2, "DEPT-01"));
        service.addStudent(makeStudent(1, "DEPT-01"));
        Map<Integer, List<Student>> grouped = service.getAllStudentsGroupedByCourse();
        assertEquals(2, grouped.get(1).size());
        assertEquals(1, grouped.get(2).size());
    }

    @Test
    @DisplayName("Видалення факультету видаляє кафедру")
    void testDeleteFacultyDeletesDept() {
        service.deleteFaculty("FAC-01");
        assertThrows(EntityNotFoundException.class, () -> service.getDepartment("DEPT-01"));
    }

    @Test
    @DisplayName("Пошук за ім'ям")
    void testSearchByName() {
        service.addStudent(makeStudent(3, "DEPT-01"));
        List<Student> res = service.searchStudentsByName("Студент");
        assertFalse(res.isEmpty());
    }

    @Test
    @DisplayName("Статистика студентів по кафедрах")
    void testStudentCountByDept() {
        service.addStudent(makeStudent(1, "DEPT-01"));
        service.addStudent(makeStudent(2, "DEPT-01"));
        Map<String, Long> stats = service.getStudentCountByDepartment();
        assertEquals(2L, stats.get("DEPT-01"));
    }
}
