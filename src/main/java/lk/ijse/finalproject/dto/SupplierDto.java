package lk.ijse.finalproject.dto;

public class SupplierDto {
    private int supId;
    private String name;
    private String contact;
    private String address;

    // Default constructor
    public SupplierDto() {}

    // Parameterized constructor
    public SupplierDto(int supId, String name, String contact, String address) {
        this.supId = supId;
        this.name = name;
        this.contact = contact;
        this.address = address;
    }

    // Getters and Setters
    public int getSupId() {
        return supId;
    }

    public void setSupId(int supId) {
        this.supId = supId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
