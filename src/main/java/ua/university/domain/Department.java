package ua.university.domain;

import java.util.Objects;

public class Department {
    private String code;
    private String name;
    private String headTeacherId;
    private String location;
    private String facultyCode;

    public Department() {}

    public Department(String code, String name, String headTeacherId, String location, String facultyCode) {
        this.code = code;
        this.name = name;
        this.headTeacherId = headTeacherId;
        this.location = location;
        this.facultyCode = facultyCode;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getHeadTeacherId() { return headTeacherId; }
    public String getLocation() { return location; }
    public String getFacultyCode() { return facultyCode; }

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setHeadTeacherId(String headTeacherId) { this.headTeacherId = headTeacherId; }
    public void setLocation(String location) { this.location = location; }
    public void setFacultyCode(String facultyCode) { this.facultyCode = facultyCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department d)) return false;
        return Objects.equals(code, d.code);
    }
    @Override public int hashCode() { return Objects.hash(code); }
    @Override public String toString() {
        return String.format("Кафедра [%s]: %s (корп. %s)", code, name, location);
    }
}
