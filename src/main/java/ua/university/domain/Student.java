package ua.university.domain;

import ua.university.exceptions.ValidationException;
import java.time.LocalDate;
import java.util.Optional;

public class Student extends Person {
    public enum Status { STUDYING, ACADEMIC_LEAVE, EXPELLED }
    public enum StudyForm { BUDGET, CONTRACT }

    private String studentId;
    private int course;
    private String group;
    private int enrollYear;
    private StudyForm studyForm;
    private Status status;
    private String departmentCode;

    public Student() { super(); }

    public Student(String firstName, String lastName, String patronymic,
                   LocalDate birthDate, String email, String phone,
                   String studentId, int course, String group,
                   int enrollYear, StudyForm studyForm, String departmentCode) {
        super(firstName, lastName, patronymic, birthDate, email, phone);
        if (course < 1 || course > 6) throw new ValidationException("Курс має бути від 1 до 6");
        if (group == null || group.isBlank()) throw new ValidationException("Група не може бути порожньою");
        this.studentId = studentId;
        this.course = course;
        this.group = group;
        this.enrollYear = enrollYear;
        this.studyForm = studyForm != null ? studyForm : StudyForm.BUDGET;
        this.status = Status.STUDYING;
        this.departmentCode = departmentCode;
    }

    public String getStudentId() { return studentId; }
    public int getCourse() { return course; }
    public String getGroup() { return group; }
    public int getEnrollYear() { return enrollYear; }
    public StudyForm getStudyForm() { return studyForm; }
    public Status getStatus() { return status; }
    public String getDepartmentCode() { return departmentCode; }

    public void setCourse(int course) { this.course = course; }
    public void setGroup(String group) { this.group = group; }
    public void setStatus(Status status) { this.status = status; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setEnrollYear(int enrollYear) { this.enrollYear = enrollYear; }
    public void setStudyForm(StudyForm studyForm) { this.studyForm = studyForm; }

    public Optional<String> getEmailOptional() {
        return Optional.ofNullable(getEmail());
    }

    @Override
    public String toString() {
        return String.format("Студент: %s | Курс: %d | Група: %s | Форма: %s | Статус: %s",
                getFullName(), course, group, studyForm, status);
    }
}
