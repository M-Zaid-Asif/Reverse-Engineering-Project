package LMS;

import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.swing.JOptionPane;

public class Book {

    /* ===================== CONSTANTS ===================== */
    private static final int AUTO_ASSIGN_ID = -1;

    /* ===================== FIELDS ===================== */
    private int bookID;
    private String title;
    private String subject;
    private String author;
    private boolean isIssued;
    private HoldRequestOperations holdRequestsOperations = new HoldRequestOperations();
    private static int currentIdNumber = 0;

    /* ===================== CONSTRUCTOR ===================== */
    public Book(int id, String t, String s, String a, boolean issued) {
        currentIdNumber++;
        this.bookID = (id == AUTO_ASSIGN_ID) ? currentIdNumber : id;
        this.title = t;
        this.subject = s;
        this.author = a;
        this.isIssued = issued;
    }

    /* ===================== PRINT / DISPLAY ===================== */
    public void printHoldRequests() {
        if (!holdRequestsOperations.holdRequests.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Hold Requests:\n\n");
            sb.append("No.\tBook's Title\tBorrower's Name\tRequest Date\n");
            sb.append("--------------------------------------------------------\n");

            for (int i = 0; i < holdRequestsOperations.holdRequests.size(); i++) {
                sb.append(i).append(" - ");
                sb.append(holdRequestsOperations.holdRequests.get(i).getFormattedInfo()).append("\n");
            }
            showMessage(sb.toString(), "Hold Requests");
        } else {
            showMessage("No Hold Requests", "Hold Requests");
        }
    }

    public void printInfo() {
        String info = String.format("%s\t%s\t%s", title, author, subject);
        showMessage(info, "Book Info");
    }

    public void changeBookInfo() {
        String input;

        input = JOptionPane.showInputDialog("Update Author? (y/n)");
        if ("y".equalsIgnoreCase(input)) {
            author = JOptionPane.showInputDialog("Enter new Author:");
        }

        input = JOptionPane.showInputDialog("Update Subject? (y/n)");
        if ("y".equalsIgnoreCase(input)) {
            subject = JOptionPane.showInputDialog("Enter new Subject:");
        }

        input = JOptionPane.showInputDialog("Update Title? (y/n)");
        if ("y".equalsIgnoreCase(input)) {
            title = JOptionPane.showInputDialog("Enter new Title:");
        }

        showMessage("Book is successfully updated.", "Update Book");
    }

    /* ===================== GETTERS & SETTERS ===================== */
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getAuthor() { return author; }
    public boolean getIssuedStatus() { return isIssued; }
    public void setIssuedStatus(boolean s) { isIssued = s; }
    public int getID() { return bookID; }
    public ArrayList<HoldRequest> getHoldRequests() { return holdRequestsOperations.holdRequests; }
    public static void setIDCount(int n) { currentIdNumber = n; }

    /* ===================== HOLD REQUEST ===================== */
    public void placeBookOnHold(Borrower bor) {
        HoldRequest hr = new HoldRequest(bor, this, new Date());
        holdRequestsOperations.addHoldRequest(hr);
        bor.addHoldRequest(hr);
        showMessage("The book " + title + " has been successfully placed on hold by borrower " + bor.getName() + ".", "Hold Placed");
    }

    public void makeHoldRequest(Borrower borrower) {
        boolean makeRequest = true;

        // Already borrowed?
        for (Loan loan : borrower.getBorrowedBooks()) {
            if (loan.getBook() == this) {
                showMessage("You have already borrowed " + title, "Hold Request Denied");
                return;
            }
        }

        // Already requested?
        for (HoldRequest hr : holdRequestsOperations.holdRequests) {
            if (hr.getBorrower() == borrower) {
                makeRequest = false;
                break;
            }
        }

        if (makeRequest) {
            placeBookOnHold(borrower);
        } else {
            showMessage("You already have one hold request for this book.", "Hold Request Denied");
        }
    }

    public void serviceHoldRequest(HoldRequest hr) {
        holdRequestsOperations.removeHoldRequest();
        hr.getBorrower().removeHoldRequest(hr);
    }

    /* ===================== ISSUE / RETURN ===================== */
    public void issueBook(Borrower borrower, Staff staff) {
        Date today = new Date();

        // Remove expired hold requests
        ArrayList<HoldRequest> hRequests = holdRequestsOperations.holdRequests;
        hRequests.removeIf(hr -> {
            long days = ChronoUnit.DAYS.between(today.toInstant(), hr.getRequestDate().toInstant()) * -1;
            if (days > Library.getInstance().getHoldRequestExpiry()) {
                hr.getBorrower().removeHoldRequest(hr);
                return true;
            }
            return false;
        });

        if (isIssued) {
            String choice = JOptionPane.showInputDialog("The book " + title + " is already issued.\nWould you like to place the book on hold? (y/n)");
            if ("y".equalsIgnoreCase(choice)) makeHoldRequest(borrower);
        } else {
            if (!holdRequestsOperations.holdRequests.isEmpty()) {
                boolean hasRequest = holdRequestsOperations.holdRequests.get(0).getBorrower() == borrower;
                if (hasRequest) {
                    serviceHoldRequest(holdRequestsOperations.holdRequests.get(0));
                } else {
                    String choice = JOptionPane.showInputDialog("Some users have already placed this book on request.\nWould you like to place the book on hold? (y/n)");
                    if ("y".equalsIgnoreCase(choice)) makeHoldRequest(borrower);
                    return;
                }
            }

            // Issue book
            setIssuedStatus(true);
            Loan iHistory = new Loan(borrower, this, staff, null, new Date(), null, false);
            Library.getInstance().addLoan(iHistory);
            borrower.addBorrowedBook(iHistory);

            showMessage("The book " + title + " is successfully issued to " + borrower.getName() + ".\nIssued by: " + staff.getName(), "Book Issued");
        }
    }

    public void returnBook(Borrower borrower, Loan l, Staff staff) {
        setIssuedStatus(false);
        l.setReturnedDate(new Date());
        l.setReceiver(staff);
        borrower.removeBorrowedBook(l);
        l.payFine();
        showMessage("The book " + l.getBook().getTitle() + " is successfully returned by " + borrower.getName() + ".\nReceived by: " + staff.getName(), "Book Returned");
    }

    /* ===================== HELPERS ===================== */
    private void showMessage(String msg, String title) {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
