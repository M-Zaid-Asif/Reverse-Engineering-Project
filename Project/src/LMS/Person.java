package LMS;

import javax.swing.JOptionPane;

public abstract class Person {

    /* ===================== CONSTANTS ===================== */
    private static final int AUTO_ASSIGN_ID = -1;

    /* ===================== FIELDS ===================== */
    protected int id;
    protected String password;
    protected String name;
    protected String address;
    protected int phoneNo;

    private static int currentIdNumber = 0; // Unique ID counter

    /* ===================== CONSTRUCTOR ===================== */
    public Person(int idNum, String name, String address, int phoneNum) {
        assignId(idNum);
        this.password = Integer.toString(id);
        this.name = name;
        this.address = address;
        this.phoneNo = phoneNum;
    }

    /* ===================== PRIVATE HELPERS ===================== */
    private void assignId(int idNum) {
        currentIdNumber++;
        if (idNum == AUTO_ASSIGN_ID) {
            this.id = currentIdNumber;
        } else {
            this.id = idNum;
        }
    }

    /* ===================== PRINT INFO ===================== */
    public void printInfo() {
        JOptionPane.showMessageDialog(
                null,
                getFormattedInfo(),
                "Person Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public String getFormattedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("-----------------------------------------\n")
            .append("The details are:\n\n")
            .append("ID: ").append(id).append("\n")
            .append("Name: ").append(name).append("\n")
            .append("Address: ").append(address).append("\n")
            .append("Phone No: ").append(phoneNo).append("\n");
        return info.toString();
    }

    /* ===================== GETTERS ===================== */
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
    public int getPhoneNumber() { return phoneNo; }
    public int getID() { return id; }

    /* ===================== SETTERS ===================== */
    public void setAddress(String address) { this.address = address; }
    public void setPhone(int phone) { this.phoneNo = phone; }
    public void setName(String name) { this.name = name; }

    /* ===================== STATIC METHODS ===================== */
    public static void setIDCount(int n) { currentIdNumber = n; }
}
