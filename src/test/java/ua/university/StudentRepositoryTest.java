package ua.university;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ua.university.domain.Student;
import ua.university.repository.StudentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StudentRepository Tests")
class StudentRepositoryTest {

    private StudentRepository repo;

    @BeforeEach
    void setup() {
        repo = new StudentRepository();
        for (int i = 1; i <= 3; i++) {
            Student s = new Student("Іван", "Студент" + i, "По",
                    LocalDate.of(2002, 1, 1), "s" + i + "@ukma.edu", "000",
                    "ST-00" + i, i, "ГР-" + i + "1", 2022,
                    Student.StudyForm.BUDGET, "DEPT-A");
            repo.save(s);
        }
        Student s4 = new Student("Олена", "Коваль", "Іванівна",
                LocalDate.of(2003, 3, 1), "olena@ukma.edu", "111",
                "ST-004", 2, "ГР-21", 2022,
                Student.StudyForm.CONTRACT, "DEPT-B");
        repo.save(s4);
    }

    @Test
    @DisplayName("Збереження та пошук за ID")
    void testSaveAndFindById() {
        List<Student> all = repo.findAll();
        assertFalse(all.isEmpty());
        String id = all.get(0).getId();
        assertTrue(repo.findById(id).isPresent());
    }

    @Test
    @DisplayName("Кількість студентів")
    void testCount() {
        assertEquals(4, repo.count());
    }

    @Test
    @DisplayName("Пошук за ім'ям")
    void testFindByName() {
        List<Student> res = repo.findByName("Коваль");
        assertEquals(1, res.size());
        assertEquals("Коваль Олена Іванівна", res.get(0).getFullName());
    }

    @ParameterizedTest
    @DisplayName("Пошук за курсом 1-3")
    @ValueSource(ints = {1, 2, 3})
    void testFindByCourse(int course) {
        List<Student> res = repo.findByCourse(course);
        assertFalse(res.isEmpty());
        res.forEach(s -> assertEquals(course, s.getCourse()));
    }

    @Test
    @DisplayName("Групування за курсами")
    void testGroupByCourse() {
        Map<Integer, List<Student>> grouped = repo.groupByCourse();
        assertTrue(grouped.containsKey(1));
        assertTrue(grouped.containsKey(2));
        assertTrue(grouped.containsKey(3));
        // course 2 has 2 students (Студент2 + Коваль)
        assertEquals(2, grouped.get(2).size());
    }

    @Test
    @DisplayName("Видалення студента")
    void testDelete() {
        String id = repo.findAll().get(0).getId();
        repo.delete(id);
        assertFalse(repo.findById(id).isPresent());
        assertEquals(3, repo.count());
    }

    @Test
    @DisplayName("Пошук за кафедрою")
    void testFindByDepartment() {
        List<Student> res = repo.findByDepartment("DEPT-A");
        assertEquals(3, res.size());
    }

    @Test
    @DisplayName("Пошук за кафедрою і курсом")
    void testFindByDeptAndCourse() {
        List<Student> res = repo.findByDepartmentAndCourse("DEPT-A", 2);
        assertEquals(1, res.size());
    }
}
