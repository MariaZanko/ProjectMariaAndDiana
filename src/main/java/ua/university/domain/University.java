package ua.university.domain;

import java.util.ArrayList;
import java.util.List;

public class University {
    private String fullName;
    private String shortName;
    private List<Faculty> faculties = new ArrayList<>();

    public University(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public void addFaculty(Faculty faculty) {
        faculties.add(faculty);
    }

    public List<Faculty> getFaculties() { return faculties; }
}