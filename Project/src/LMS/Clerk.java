package LMS;

import javax.swing.JOptionPane;

public class Clerk extends Staff {

    /* ===================== CONSTANTS ===================== */
    private static final int AUTO_ASSIGN_DESK = -1;

    /* ===================== FIELDS ===================== */
    int deskNo;                  // Desk Number of the Clerk
    private static int currentDeskNumber = 0;

    /* ===================== CONSTRUCTOR ===================== */
    public Clerk(int id, String name, String address, int phone, double salary, int desk) {
        super(id, name, address, phone, salary);
        assignDeskNumber(desk);
    }

    /* ===================== PRIVATE HELPERS ===================== */
    private void assignDeskNumber(int desk) {
        if (desk == AUTO_ASSIGN_DESK) {
            this.deskNo = currentDeskNumber;
        } else {
            this.deskNo = desk;
        }
        currentDeskNumber++;
    }

    /* ===================== OVERRIDDEN METHODS ===================== */
    @Override
    public void printInfo() {
        StringBuilder info = new StringBuilder();

        info.append(super.getFormattedInfo());
        info.append("\nDesk Number: ").append(deskNo);

        JOptionPane.showMessageDialog(
                null,
                info.toString(),
                "Clerk Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /* ===================== UPDATE METHODS ===================== */
    public void updateClerkInfo() {
        String choice;

        choice = JOptionPane.showInputDialog("Do you want to update " + getName() + "'s Name? (y/n)");
        if ("y".equalsIgnoreCase(choice)) {
            String input = JOptionPane.showInputDialog("Enter new Name:");
            setName(input);
        }

        choice = JOptionPane.showInputDialog("Do you want to update " + getName() + "'s Address? (y/n)");
        if ("y".equalsIgnoreCase(choice)) {
            String input = JOptionPane.showInputDialog("Enter new Address:");
            setAddress(input);
        }

        choice = JOptionPane.showInputDialog("Do you want to update " + getName() + "'s Phone Number? (y/n)");
        if ("y".equalsIgnoreCase(choice)) {
            String input = JOptionPane.showInputDialog("Enter new Phone Number:");
            setPhone(Integer.parseInt(input));
        }

        choice = JOptionPane.showInputDialog("Do you want to update " + getName() + "'s Salary? (y/n)");
        if ("y".equalsIgnoreCase(choice)) {
            String input = JOptionPane.showInputDialog("Enter new Salary:");
            setSalary(Double.parseDouble(input));
        }

        choice = JOptionPane.showInputDialog("Do you want to update " + getName() + "'s Desk Number? (y/n)");
        if ("y".equalsIgnoreCase(choice)) {
            String input = JOptionPane.showInputDialog("Enter new Desk Number:");
            deskNo = Integer.parseInt(input);
        }

        JOptionPane.showMessageDialog(null, "Clerk info successfully updated.", "Update Complete", JOptionPane.INFORMATION_MESSAGE);
    }

}
