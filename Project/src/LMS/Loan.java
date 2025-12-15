package LMS;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.swing.JOptionPane;

public class Loan {
    
    private Borrower borrower;      
    private Book book;
    private Staff issuer;
    private Staff receiver;
    private Date issuedDate;
    private Date dateReturned;
    private boolean finePaid;
       
    public Loan(Borrower borrower, Book book, Staff issuer, Staff receiver, Date issuedDate, Date dateReturned, boolean finePaid) {
        this.borrower = borrower;
        this.book = book;
        this.issuer = issuer;
        this.receiver = receiver;
        this.issuedDate = issuedDate;
        this.dateReturned = dateReturned;
        this.finePaid = finePaid;
    }
    
    /*----- Getter Methods ------------*/
    public Book getBook() { return book; }
    public Staff getIssuer() { return issuer; }
    public Staff getReceiver() { return receiver; }
    public Date getIssuedDate() { return issuedDate; } 
    public Date getReturnDate() { return dateReturned; }
    public Borrower getBorrower() { return borrower; }
    public boolean getFineStatus() { return finePaid; }
    
    /*---------- Setter Methods ----------------*/
    public void setReturnedDate(Date dateReturned) { this.dateReturned = dateReturned; }
    public void setFineStatus(boolean finePaid) { this.finePaid = finePaid; }    
    public void setReceiver(Staff receiver) { this.receiver = receiver; }
    
    // Computes fine for a particular loan only
    public double computeFine1() {
        double totalFine = 0;
        if (!finePaid) {    
            long days = ChronoUnit.DAYS.between(new Date().toInstant(), issuedDate.toInstant());
            days = -days;
            days = days - Library.getInstance().book_return_deadline;

            if (days > 0)
                totalFine = days * Library.getInstance().per_day_fine;
        }
        return totalFine;
    }
    
    public void payFine() {
        double totalFine = computeFine1();
                
        if (totalFine > 0) {
            String choice = JOptionPane.showInputDialog(
                    "Total Fine generated: Rs " + totalFine + "\nDo you want to pay? (y/n)"
            );

            if ("y".equalsIgnoreCase(choice)) {
                finePaid = true; 
                JOptionPane.showMessageDialog(null, "Fine paid successfully.", "Fine Payment", JOptionPane.INFORMATION_MESSAGE);
            } else {
                finePaid = false; 
                JOptionPane.showMessageDialog(null, "Fine not paid.", "Fine Payment", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No fine is generated.", "Fine Payment", JOptionPane.INFORMATION_MESSAGE);
            finePaid = true;
        }        
    }

    // Extends issued date
    public void renewIssuedBook(Date newIssuedDate) {        
        issuedDate = newIssuedDate;
        JOptionPane.showMessageDialog(
                null,
                "The deadline of the book " + getBook().getTitle() + " has been extended.\nIssued Book is successfully renewed!",
                "Book Renewed",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
