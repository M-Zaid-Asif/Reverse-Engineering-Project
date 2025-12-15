package LMS;

import java.util.*;
import javax.swing.JOptionPane;

public class Borrower extends Person {    

    private ArrayList<Loan> borrowedBooks;      // Books currently borrowed
    private ArrayList<HoldRequest> onHoldBooks; // Books currently on hold

    public Borrower(int id, String name, String address, int phoneNum) {
        super(id, name, address, phoneNum);
        borrowedBooks = new ArrayList<>();
        onHoldBooks = new ArrayList<>();        
    }

    // Printing Borrower's Info
    @Override
    public void printInfo() {
        String info = "ID: " + getID() + "\nName: " + getName() + "\nAddress: " + getAddress() + "\nPhone: " + getPhoneNumber();
        JOptionPane.showMessageDialog(null, info, "Borrower Info", JOptionPane.INFORMATION_MESSAGE);
        printBorrowedBooks();
        printOnHoldBooks();
    }

    // Display borrowed books
    public void printBorrowedBooks() {
        if (!borrowedBooks.isEmpty()) {
            StringBuilder sb = new StringBuilder("Borrowed Books:\nNo.\tTitle\tAuthor\tSubject\n");
            for (int i = 0; i < borrowedBooks.size(); i++) {
                sb.append(i).append("-\t").append(borrowedBooks.get(i).getBook().getTitle())
                  .append("\t").append(borrowedBooks.get(i).getBook().getAuthor())
                  .append("\t").append(borrowedBooks.get(i).getBook().getSubject()).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString(), "Borrowed Books", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No borrowed books.", "Borrowed Books", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Display books on hold
    public void printOnHoldBooks() {
        if (!onHoldBooks.isEmpty()) {
            StringBuilder sb = new StringBuilder("On Hold Books:\nNo.\tTitle\tAuthor\tSubject\n");
            for (int i = 0; i < onHoldBooks.size(); i++) {
                sb.append(i).append("-\t").append(onHoldBooks.get(i).getBook().getTitle())
                  .append("\t").append(onHoldBooks.get(i).getBook().getAuthor())
                  .append("\t").append(onHoldBooks.get(i).getBook().getSubject()).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString(), "On Hold Books", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No On Hold books.", "On Hold Books", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Updating Borrower's Info
    public void updateBorrowerInfo() {
        String choice = JOptionPane.showInputDialog("Do you want to update " + getName() + "'s Name? (y/n)");
        updateBorrowerName(choice);

        choice = JOptionPane.showInputDialog("Do you want to update " + getName() + "'s Address? (y/n)");
        updateBorrowerAddress(choice);

        choice = JOptionPane.showInputDialog("Do you want to update " + getName() + "'s Phone Number? (y/n)");
        updateBorrowerPhoneNumber(choice);

        JOptionPane.showMessageDialog(null, "Borrower is successfully updated.", "Update Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateBorrowerName(String choice) {
        if ("y".equalsIgnoreCase(choice)) {
            String input = JOptionPane.showInputDialog("Type New Name:");
            setName(input);
            JOptionPane.showMessageDialog(null, "The name is successfully updated.", "Update Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateBorrowerAddress(String choice) {
        if ("y".equalsIgnoreCase(choice)) {
            String input = JOptionPane.showInputDialog("Type New Address:");
            setAddress(input);
            JOptionPane.showMessageDialog(null, "The address is successfully updated.", "Update Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateBorrowerPhoneNumber(String choice) {
        if ("y".equalsIgnoreCase(choice)) {
            String input = JOptionPane.showInputDialog("Type New Phone Number:");
            setPhone(Integer.parseInt(input));
            JOptionPane.showMessageDialog(null, "The phone number is successfully updated.", "Update Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /*-- Add/Remove Borrowed Books --*/
    public void addBorrowedBook(Loan iBook) { borrowedBooks.add(iBook); }
    public void removeBorrowedBook(Loan iBook) { borrowedBooks.remove(iBook); }

    /*-- Add/Remove On Hold Books --*/
    public void addHoldRequest(HoldRequest hr) { onHoldBooks.add(hr); }
    public void removeHoldRequest(HoldRequest hr) { onHoldBooks.remove(hr); }

    /*-- Getters --*/
    public ArrayList<Loan> getBorrowedBooks() { return borrowedBooks; }
    public ArrayList<HoldRequest> getOnHoldBooks() { return onHoldBooks; }
}
