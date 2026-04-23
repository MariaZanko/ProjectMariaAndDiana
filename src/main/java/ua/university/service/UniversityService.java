package ua.university.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.domain.*;
import ua.university.exceptions.*;

import java.util.*;
import java.util.stream.*;

public class UniversityService {
    private static final Logger log = LoggerFactory.getLogger(UniversityService.class);
    private final DataStore store;

    public UniversityService(DataStore store) { this.store = store; }

    // --- Faculty CRUD ---
    public void addFaculty(Faculty f) {
        if (store.getFaculties().findById(f.getCode()).isPresent())
            throw new UniversityException("Факультет з кодом " + f.getCode() + " вже існує");
        store.getFaculties().save(f);
        log.info("Faculty added: {}", f.getCode());
    }

    public void deleteFaculty(String code) {
        store.getFaculties().findById(code)
                .orElseThrow(() -> new EntityNotFoundException("Факультет", code));
        store.getDepartments().findByFaculty(code).forEach(d -> deleteDepartment(d.getCode()));
        store.getFaculties().delete(code);
        log.info("Faculty deleted: {}", code);
    }

    public Faculty getFaculty(String code) {
        return store.getFaculties().findById(code)
                .orElseThrow(() -> new EntityNotFoundException("Факультет", code));
    }

    public List<Faculty> getAllFaculties() { return store.getFaculties().findAll(); }

    // --- Department CRUD ---
    public void addDepartment(Department d) {
        getFaculty(d.getFacultyCode()); // validate faculty exists
        if (store.getDepartments().findById(d.getCode()).isPresent())
            throw new UniversityException("Кафедра з кодом " + d.getCode() + " вже існує");
        store.getDepartments().save(d);
        log.info("Department added: {}", d.getCode());
    }

    public void deleteDepartment(String code) {
        store.getDepartments().findById(code)
                .orElseThrow(() -> new EntityNotFoundException("Кафедра", code));
        store.getDepartments().delete(code);
        log.info("Department deleted: {}", code);
    }

    public Department getDepartment(String code) {
        return store.getDepartments().findById(code)
                .orElseThrow(() -> new EntityNotFoundException("Кафедра", code));
    }

    public List<Department> getDepartmentsByFaculty(String facultyCode) {
        return store.getDepartments().findByFaculty(facultyCode);
    }

    // --- Student CRUD ---
    public void addStudent(Student s) {
        getDepartment(s.getDepartmentCode()); // validate dept exists
        store.getStudents().save(s);
        log.info("Student added: {}", s.getFullName());
    }

    public void deleteStudent(String id) {
        store.getStudents().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студент", id));
        store.getStudents().delete(id);
        log.info("Student deleted: {}", id);
    }

    public Student getStudent(String id) {
        return store.getStudents().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студент", id));
    }

    public void transferStudent(String studentId, String newDeptCode, String newGroup) {
        Student s = getStudent(studentId);
        getDepartment(newDeptCode);
        s.setDepartmentCode(newDeptCode);
        if (newGroup != null && !newGroup.isBlank()) s.setGroup(newGroup);
        store.getStudents().update(s);
        log.info("Student {} transferred to dept={}, group={}", studentId, newDeptCode, newGroup);
    }

    // --- Teacher CRUD ---
    public void addTeacher(Teacher t) {
        if (t.getDepartmentCode() != null) getDepartment(t.getDepartmentCode());
        store.getTeachers().save(t);
        log.info("Teacher added: {}", t.getFullName());
    }

    public void deleteTeacher(String id) {
        store.getTeachers().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Викладач", id));
        store.getTeachers().delete(id);
        log.info("Teacher deleted: {}", id);
    }

    public Teacher getTeacher(String id) {
        return store.getTeachers().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Викладач", id));
    }

    // --- Search & Reports (Stream API) ---
    public List<Student> searchStudentsByName(String name) {
        return store.getStudents().findByName(name);
    }

    public List<Teacher> searchTeachersByName(String name) {
        return store.getTeachers().findByName(name);
    }

    public List<Student> getStudentsByCourse(int course) {
        return store.getStudents().findByCourse(course);
    }

    public List<Student> getStudentsByGroup(String group) {
        return store.getStudents().findByGroup(group);
    }

    public Map<Integer, List<Student>> getAllStudentsGroupedByCourse() {
        return store.getStudents().groupByCourse();
    }

    public List<Student> getStudentsByFacultyAlpha(String facultyCode) {
        Set<String> deptCodes = store.getDepartments().findByFaculty(facultyCode)
                .stream().map(Department::getCode).collect(Collectors.toSet());
        return store.getStudents().findAll().stream()
                .filter(s -> deptCodes.contains(s.getDepartmentCode()))
                .sorted(Comparator.comparing(Student::getFullName))
                .collect(Collectors.toList());
    }

    public List<Teacher> getTeachersByFacultyAlpha(String facultyCode) {
        Set<String> deptCodes = store.getDepartments().findByFaculty(facultyCode)
                .stream().map(Department::getCode).collect(Collectors.toSet());
        return store.getTeachers().findAll().stream()
                .filter(t -> deptCodes.contains(t.getDepartmentCode()))
                .sorted(Comparator.comparing(Teacher::getFullName))
                .collect(Collectors.toList());
    }

    public List<Student> getStudentsByDepartmentAlpha(String deptCode) {
        return store.getStudents().findByDepartment(deptCode).stream()
                .sorted(Comparator.comparing(Student::getFullName))
                .collect(Collectors.toList());
    }

    public List<Teacher> getTeachersByDepartmentAlpha(String deptCode) {
        return store.getTeachers().findByDepartment(deptCode);
    }

    public List<Student> getStudentsByDepartmentAndCourse(String deptCode, int course) {
        return store.getStudents().findByDepartmentAndCourse(deptCode, course);
    }

    public Map<String, Long> getStudentCountByDepartment() {
        return store.getStudents().findAll().stream()
                .collect(Collectors.groupingBy(
                    s -> Optional.ofNullable(s.getDepartmentCode()).orElse("Невідома кафедра"),
                    Collectors.counting()));
    }

    public List<Student> getAllStudentsSortedByCourse() {
        return store.getStudents().findAll().stream()
                .sorted(Comparator.comparingInt(Student::getCourse).thenComparing(Student::getFullName))
                .collect(Collectors.toList());
    }
}
