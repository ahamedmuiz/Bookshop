package lk.ijse.finalproject.dto;

public class CustomerDto {
    private int cId;
    private String name;
    private String email;
    private String contact;

    public CustomerDto() {}

    public CustomerDto(int cId, String name, String email, String contact) {
        this.cId = cId;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    public int getCId() {
        return cId;
    }
    public void setCId(int cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
}
