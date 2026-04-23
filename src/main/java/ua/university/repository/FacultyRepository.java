package ua.university.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import ua.university.domain.Faculty;
import java.util.List;

public class FacultyRepository extends AbstractJsonRepository<Faculty, String> {
    public FacultyRepository() {
        super("faculties.json", Faculty::getCode, new TypeReference<List<Faculty>>() {});
    }
}