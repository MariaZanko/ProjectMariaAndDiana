package ua.university.domain;

import java.util.Objects;

public class Faculty {
    private String code;
    private String name;
    private String shortName;
    private String deanId;
    private String contacts;

    public Faculty() {}

    public Faculty(String code, String name, String shortName, String deanId, String contacts) {
        this.code = code;
        this.name = name;
        this.shortName = shortName;
        this.deanId = deanId;
        this.contacts = contacts;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getShortName() { return shortName; }
    public String getDeanId() { return deanId; }
    public String getContacts() { return contacts; }

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public void setDeanId(String deanId) { this.deanId = deanId; }
    public void setContacts(String contacts) { this.contacts = contacts; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Faculty f)) return false;
        return Objects.equals(code, f.code);
    }
    @Override public int hashCode() { return Objects.hash(code); }
    @Override public String toString() {
        return String.format("Факультет [%s]: %s (%s)", code, name, shortName);
    }
}
