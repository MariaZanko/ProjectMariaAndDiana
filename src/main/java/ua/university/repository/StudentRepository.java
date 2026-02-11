package ua.university.repository;

import ua.university.domain.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentRepository {
    private final List<Student> students = new ArrayList<>();

    public void save(Student student) {
        students.add(student);
    }

    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    public List<Student> findByName(String name) {
        return students.stream()
                .filter(s -> s.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}