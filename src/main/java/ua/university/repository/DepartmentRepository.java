package ua.university.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import ua.university.domain.Department;
import java.util.*;
import java.util.stream.Collectors;

public class DepartmentRepository extends AbstractJsonRepository<Department, String> {
    public DepartmentRepository() {
        super("departments.json", Department::getCode, new TypeReference<List<Department>>() {});
    }

    public List<Department> findByFaculty(String facultyCode) {
        return store.values().stream()
                .filter(d -> facultyCode.equals(d.getFacultyCode()))
                .collect(Collectors.toList());
    }
    public Optional<Department> findByCode(String code) {
        if (code == null) return Optional.empty();
        return store.values().stream()
                .filter(d -> code.equalsIgnoreCase(d.getCode().trim()))
                .findFirst();
    }
}

