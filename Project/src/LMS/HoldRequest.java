package LMS;

import java.util.Date;
import javax.swing.JOptionPane;

public class HoldRequest {

    private Borrower borrower;
    private Book book;
    private Date requestDate;

    // Parameterized constructor
    public HoldRequest(Borrower bor, Book b, Date reqDate) {
        borrower = bor;
        book = b;
        requestDate = reqDate;
    }

    /* ===== Getters ===== */
    public Borrower getBorrower() { return borrower; }
    public Book getBook() { return book; }
    public Date getRequestDate() { return requestDate; }

    /* ===== Print / Display Hold Request Info ===== */
    public void print() {
        String info = book.getTitle() + "\t\t\t" + borrower.getName() + "\t\t\t" + requestDate;
        JOptionPane.showMessageDialog(null, info, "Hold Request Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // Optional: formatted string for list displays
    public String getFormattedInfo() {
        return book.getTitle() + "\t" + borrower.getName() + "\t" + requestDate;
    }
}
