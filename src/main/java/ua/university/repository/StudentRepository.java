package ua.university.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import ua.university.domain.Student;
import java.util.*;
import java.util.stream.Collectors;

public class StudentRepository extends AbstractJsonRepository<Student, String> {
    public StudentRepository() {
        super("students.json", Student::getId, new TypeReference<List<Student>>() {});
    }

    public List<Student> findByName(String query) {
        String q = query.toLowerCase();
        return store.values().stream()
                .filter(s -> s.getFullName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Student> findByCourse(int course) {
        return store.values().stream()
                .filter(s -> s.getCourse() == course)
                .sorted(Comparator.comparing(Student::getFullName))
                .collect(Collectors.toList());
    }

    public List<Student> findByGroup(String group) {
        return store.values().stream()
                .filter(s -> s.getGroup().equalsIgnoreCase(group))
                .collect(Collectors.toList());
    }

    public List<Student> findByDepartment(String deptCode) {
        return store.values().stream()
                .filter(s -> deptCode.equals(s.getDepartmentCode()))
                .collect(Collectors.toList());
    }

    public List<Student> findByDepartmentAndCourse(String deptCode, int course) {
        return store.values().stream()
                .filter(s -> deptCode.equals(s.getDepartmentCode()) && s.getCourse() == course)
                .sorted(Comparator.comparing(Student::getFullName))
                .collect(Collectors.toList());
    }

    public Map<Integer, List<Student>> groupByCourse() {
        return store.values().stream()
                .collect(Collectors.groupingBy(Student::getCourse,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.stream().sorted(Comparator.comparing(Student::getFullName)).collect(Collectors.toList()))));
    }
}