package lk.ijse.finalproject.dto;

public class EmployeeDto {
    private int E_ID;
    private String E_Name;
    private String E_Email;
    private String E_Contact;

    public EmployeeDto(int E_ID, String E_Name, String E_Email, String E_Contact) {
        this.E_ID = E_ID;
        this.E_Name = E_Name;
        this.E_Email = E_Email;
        this.E_Contact = E_Contact;
    }

    public int getE_ID() {
        return E_ID;
    }

    public void setE_ID(int E_ID) {
        this.E_ID = E_ID;
    }

    public String getE_Name() {
        return E_Name;
    }

    public void setE_Name(String E_Name) {
        this.E_Name = E_Name;
    }

    public String getE_Email() {
        return E_Email;
    }

    public void setE_Email(String E_Email) {
        this.E_Email = E_Email;
    }

    public String getE_Contact() {
        return E_Contact;
    }

    public void setE_Contact(String E_Contact) {
        this.E_Contact = E_Contact;
    }
}
