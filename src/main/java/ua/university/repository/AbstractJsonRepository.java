package ua.university.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

/**
 * Абстрактний репозиторій для роботи з JSON-файлами.
 * Дані зберігаються в папці 'data' в корені проєкту, щоб уникнути видалення Maven-ом.
 */
public abstract class AbstractJsonRepository<T, ID> implements Repository<T, ID> {
    private final Path filePath;
    private final ObjectMapper objectMapper;
    protected final Map<ID, T> store = new LinkedHashMap<>();
    private final Function<T, ID> idExtractor;
    private final TypeReference<List<T>> typeReference;

    public AbstractJsonRepository(String fileName, Function<T, ID> idExtractor, TypeReference<List<T>> typeReference) {
        // ВАЖЛИВО: зберігаємо в зовнішній папці 'data', а не в target чи resources
        this.filePath = Paths.get("data", fileName);
        this.idExtractor = idExtractor;
        this.typeReference = typeReference;

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // для роботи з LocalDate
                .enable(SerializationFeature.INDENT_OUTPUT) // гарний вигляд JSON
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        loadData();
    }

    private void loadData() {
        try {
            // Перевіряємо, чи існує файл і чи він не порожній
            if (Files.exists(filePath) && Files.size(filePath) > 0) {
                List<T> entities = objectMapper.readValue(filePath.toFile(), typeReference);
                if (entities != null) {
                    entities.forEach(entity -> store.put(idExtractor.apply(entity), entity));
                    System.out.println("[INFO] Завантажено " + store.size() + " об'єктів з " + filePath.getFileName());
                }
            } else {
                System.out.println("[INFO] Файл " + filePath.getFileName() + " ще не створений або порожній.");
            }
        } catch (IOException e) {
            // Якщо сталася помилка (наприклад, Unrecognized field), ми НЕ очищуємо store,
            // щоб випадково не перезаписати файл порожнім списком при виході.
            System.err.println("[ERROR] Помилка десеріалізації " + filePath.getFileName() + ": " + e.getMessage());
            System.err.println("[FIX] Перевір, чи назви полів у JSON збігаються з полями у Java-класах.");
        }
    }

    protected void saveData() {
        try {
            // Створюємо папку data, якщо вона була видалена
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            // Записуємо поточний стан store у файл
            objectMapper.writeValue(filePath.toFile(), new ArrayList<>(store.values()));
        } catch (IOException e) {
            System.err.println("[ERROR] Не вдалося зберегти дані у " + filePath.getFileName() + ": " + e.getMessage());
        }
    }

    @Override
    public void save(T entity) {
        store.put(idExtractor.apply(entity), entity);
        saveData();
    }

    @Override
    public void update(T entity) {
        store.put(idExtractor.apply(entity), entity);
        saveData();
    }

    @Override
    public void delete(ID id) {
        if (store.containsKey(id)) {
            store.remove(id);
            saveData();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public int count() {
        return store.size();
    }
}