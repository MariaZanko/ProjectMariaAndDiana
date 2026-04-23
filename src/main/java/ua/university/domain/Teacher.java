package ua.university.domain;

import java.time.LocalDate;
import java.time.Period;

public class Teacher extends Person {
    private String position;
    private String scientificDegree;
    private String academicTitle;
    private LocalDate hireDate;
    private double salaryRate;
    private String departmentCode;

    public Teacher() { super(); }

    public Teacher(String firstName, String lastName, String patronymic,
                   LocalDate birthDate, String email, String phone,
                   String position, String scientificDegree, String academicTitle,
                   LocalDate hireDate, double salaryRate, String departmentCode) {
        super(firstName, lastName, patronymic, birthDate, email, phone);
        this.position = position;
        this.scientificDegree = scientificDegree;
        this.academicTitle = academicTitle;
        this.hireDate = hireDate;
        this.salaryRate = salaryRate;
        this.departmentCode = departmentCode;
    }

    public String getPosition() { return position; }
    public String getScientificDegree() { return scientificDegree; }
    public String getAcademicTitle() { return academicTitle; }
    public LocalDate getHireDate() { return hireDate; }
    public double getSalaryRate() { return salaryRate; }
    public String getDepartmentCode() { return departmentCode; }

    public void setPosition(String position) { this.position = position; }
    public void setScientificDegree(String scientificDegree) { this.scientificDegree = scientificDegree; }
    public void setAcademicTitle(String academicTitle) { this.academicTitle = academicTitle; }
    public void setSalaryRate(double salaryRate) { this.salaryRate = salaryRate; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public int getExperienceYears() {
        if (hireDate == null) return 0;
        return Period.between(hireDate, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return String.format("Викладач: %s | Посада: %s | Ступінь: %s | Стаж: %d р.",
                getFullName(), position, scientificDegree, getExperienceYears());
    }
}
