package ua.university.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.domain.*;

import java.nio.file.*;
import java.util.List;

public class PersistenceService {
    private static final Logger log = LoggerFactory.getLogger(PersistenceService.class);
    private static final Path DATA_DIR = Paths.get("data");
    private final ObjectMapper mapper;
    private final DataStore store;

    public PersistenceService(DataStore store) {
        this.store = store;
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveAll() {
        try {
            Files.createDirectories(DATA_DIR);
            mapper.writeValue(DATA_DIR.resolve("university.json").toFile(), store.getUniversity());
            mapper.writeValue(DATA_DIR.resolve("faculties.json").toFile(), store.getFaculties().findAll());
            mapper.writeValue(DATA_DIR.resolve("departments.json").toFile(), store.getDepartments().findAll());
            mapper.writeValue(DATA_DIR.resolve("students.json").toFile(), store.getStudents().findAll());
            mapper.writeValue(DATA_DIR.resolve("teachers.json").toFile(), store.getTeachers().findAll());
            log.info("Data saved successfully to {}", DATA_DIR.toAbsolutePath());
            System.out.println("Дані збережено успішно.");
        } catch (Exception e) {
            log.error("Failed to save data", e);
            System.out.println("Помилка збереження: " + e.getMessage());
        }
    }

    public void loadAll() {
        try {
            if (!Files.exists(DATA_DIR)) {
                log.info("No data directory found, starting fresh");
                return;
            }
            Path univFile = DATA_DIR.resolve("university.json");
            if (Files.exists(univFile)) {
                University u = mapper.readValue(univFile.toFile(), University.class);
                store.setUniversity(u);
            }
            Path facFile = DATA_DIR.resolve("faculties.json");
            if (Files.exists(facFile)) {
                List<Faculty> facs = mapper.readValue(facFile.toFile(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Faculty.class));
                facs.forEach(store.getFaculties()::save);
            }
            Path deptFile = DATA_DIR.resolve("departments.json");
            if (Files.exists(deptFile)) {
                List<Department> depts = mapper.readValue(deptFile.toFile(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Department.class));
                depts.forEach(store.getDepartments()::save);
            }
            Path studFile = DATA_DIR.resolve("students.json");
            if (Files.exists(studFile)) {
                List<Student> studs = mapper.readValue(studFile.toFile(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Student.class));
                studs.forEach(store.getStudents()::save);
            }
            Path teachFile = DATA_DIR.resolve("teachers.json");
            if (Files.exists(teachFile)) {
                List<Teacher> teachs = mapper.readValue(teachFile.toFile(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Teacher.class));
                teachs.forEach(store.getTeachers()::save);
            }
            log.info("Data loaded from {}", DATA_DIR.toAbsolutePath());
            System.out.println("Дані завантажено успішно.");
        } catch (Exception e) {
            log.error("Failed to load data", e);
            System.out.println("Помилка завантаження: " + e.getMessage());
        }
    }
}
