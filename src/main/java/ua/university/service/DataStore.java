package ua.university.service;

import ua.university.domain.*;
import ua.university.repository.*;

public class DataStore {
    private University university;
    private final FacultyRepository faculties = new FacultyRepository();
    private final DepartmentRepository departments = new DepartmentRepository();
    private final StudentRepository students = new StudentRepository();
    private final TeacherRepository teachers = new TeacherRepository();

    public DataStore() {
        university = new University("Національний університет «Києво-Могилянська академія»",
                "НаУКМА", "Київ", "вул. Сковороди, 2");
    }

    public University getUniversity() { return university; }
    public void setUniversity(University u) { this.university = u; }
    public FacultyRepository getFaculties() { return faculties; }
    public DepartmentRepository getDepartments() { return departments; }
    public StudentRepository getStudents() { return students; }
    public TeacherRepository getTeachers() { return teachers; }
}
