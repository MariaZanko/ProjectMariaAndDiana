package ua.university.domain;

public class Specialty {
    private String id;
    private String name;
    private String code; // Наприклад, "121" для Інженерії ПЗ

    public Specialty(String id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    // Геттери та сетери
    public String getName() { return name; }
    public String getCode() { return code; }

    @Override
    public String toString() {
        return code + " " + name;
    }
}