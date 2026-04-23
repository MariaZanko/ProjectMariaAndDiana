package ua.university.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import ua.university.domain.Teacher;
import java.util.*;
import java.util.stream.Collectors;

public class TeacherRepository extends AbstractJsonRepository<Teacher, String> {
    public TeacherRepository() {
        super("teachers.json", Teacher::getId, new TypeReference<List<Teacher>>() {});
    }

    public List<Teacher> findByName(String query) {
        String q = query.toLowerCase();
        return store.values().stream()
                .filter(t -> t.getFullName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Teacher> findByDepartment(String deptCode) {
        return store.values().stream()
                .filter(t -> deptCode.equals(t.getDepartmentCode()))
                .sorted(Comparator.comparing(Teacher::getFullName))
                .collect(Collectors.toList());
    }
}