package ua.university.domain;

public class University {
    private String fullName;
    private String shortName;
    private String city;
    private String address;

    public University() {}
    public University(String fullName, String shortName, String city, String address) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.city = city;
        this.address = address;
    }

    public String getFullName() { return fullName; }
    public String getShortName() { return shortName; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public void setCity(String city) { this.city = city; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return String.format("%s (%s), %s, %s", fullName, shortName, city, address);
    }
}
