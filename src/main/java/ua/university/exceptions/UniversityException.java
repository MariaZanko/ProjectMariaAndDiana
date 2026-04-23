package ua.university.exceptions;
public class UniversityException extends RuntimeException {
    public UniversityException(String message) { super(message); }
    public UniversityException(String message, Throwable cause) { super(message, cause); }
}
