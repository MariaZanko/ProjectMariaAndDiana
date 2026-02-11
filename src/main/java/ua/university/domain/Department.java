package ua.university.domain;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private String code;
    private String name;
    private Teacher head; // Завідувач
    private List<Student> students = new ArrayList<>();

    public Department(String code, String name, Teacher head) {
        this.code = code;
        this.name = name;
        this.head = head;
    }

    // Геттери
    public String getName() { return name; }
    public List<Student> getStudents() { return students; }

    public void addStudent(Student student) {
        this.students.add(student);
    }
}