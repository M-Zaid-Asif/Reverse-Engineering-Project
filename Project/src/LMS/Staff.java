package LMS;

import javax.swing.JOptionPane;

public class Staff extends Person {

    protected double salary;

    /* ===================== CONSTRUCTOR ===================== */
    public Staff(int id, String name, String address, int phone, double salary) {
        super(id, name, address, phone);
        this.salary = salary;
    }

    /* ===================== OVERRIDDEN METHODS ===================== */
    @Override
    public void printInfo() {
        StringBuilder info = new StringBuilder();
        info.append(getFormattedInfo());
        info.append("\nSalary: ").append(salary);

        JOptionPane.showMessageDialog(
                null,
                info.toString(),
                "Staff Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /* ===================== HELPER METHOD ===================== */
    public String getFormattedInfo() {
        // Reuse Person info
        return super.getFormattedInfo();
    }

    /* ===================== GETTER ===================== */
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
    this.salary = salary;
}
}
