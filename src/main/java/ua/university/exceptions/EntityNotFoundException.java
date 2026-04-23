package ua.university.exceptions;
public class EntityNotFoundException extends UniversityException {
    public EntityNotFoundException(String entity, String id) {
        super(entity + " з ID '" + id + "' не знайдено");
    }
}
