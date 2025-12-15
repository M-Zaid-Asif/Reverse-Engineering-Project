package LMS;

import java.io.IOException;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainUI {

    /* ===================== CONSTANTS (Magic Numbers Removed) ===================== */

    private static final int ADMIN_PASSWORD_ATTEMPTS = 1;
    private static final String ADMIN_PASSWORD = "lib";

    /* ===================== GENERIC UI HELPERS ===================== */

    private static void info(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private static void error(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static int menu(String title, String[] options) {
        return JOptionPane.showOptionDialog(
                null,
                title,
                "Library Management System",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    private static String input(String message) {
        return JOptionPane.showInputDialog(message);
    }

    /* ===================== FUNCTIONALITIES (UNCHANGED LOGIC) ===================== */

    private static void performFunctionality(Person person, int choice) throws IOException {

        Library lib = Library.getInstance();

        switch (choice) {

            case 0: // Search Book
                lib.searchForBooks();
                break;

            case 1: // Place Hold
                ArrayList<Book> books = lib.searchForBooks();
                if (books != null) {
                    int index = getIndex(books.size());
                    Book b = books.get(index);

                    if (person instanceof Clerk || person instanceof Librarian) {
                        Borrower bor = lib.findBorrower();
                        if (bor != null) b.makeHoldRequest(bor);
                    } else {
                        b.makeHoldRequest((Borrower) person);
                    }
                }
                break;

            case 2: // View Borrower Info
                if (person instanceof Clerk || person instanceof Librarian) {
                    Borrower bor = lib.findBorrower();
                    if (bor != null) bor.printInfo();
                } else {
                    person.printInfo();
                }
                break;

            case 3: // Compute Fine
                Borrower borrower =
                        (person instanceof Borrower) ? (Borrower) person : lib.findBorrower();

                if (borrower != null) {
                    double fine = lib.computeFine2(borrower);
                    info("Total Fine: Rs " + fine);
                }
                break;

            case 4: // Hold Queue
                ArrayList<Book> holdBooks = lib.searchForBooks();
                if (holdBooks != null) {
                    int idx = getIndex(holdBooks.size());
                    holdBooks.get(idx).printHoldRequests();
                }
                break;

            case 5: // Issue Book
                issueBook(lib, person);
                break;

            case 6: // Return Book
                returnBook(lib, person);
                break;

            case 7: // Renew Book
                renewBook(lib);
                break;

            case 8: // Add Borrower
                lib.createPerson('b');
                break;

            case 9: // Update Borrower
                Borrower bor = lib.findBorrower();
                if (bor != null) bor.updateBorrowerInfo();
                break;

            case 10: // Add Book
                addBook(lib);
                break;

            case 11: // Remove Book
                removeBook(lib);
                break;

            case 12: // Change Book Info
                changeBookInfo(lib);
                break;

            case 13: // View Clerk Info
                Clerk clerk = lib.findClerk();
                if (clerk != null) clerk.printInfo();
                break;
        }
    }

    /* ===================== EXTRACTED HELPERS (Long Method Fixed) ===================== */

    private static int getIndex(int size) {
        String input = JOptionPane.showInputDialog("Enter index (0 to " + (size - 1) + ")");
        return Integer.parseInt(input);
    }

    private static void issueBook(Library lib, Person person) throws IOException {
        ArrayList<Book> books = lib.searchForBooks();
        if (books != null) {
            int index = getIndex(books.size());
            Borrower bor = lib.findBorrower();
            if (bor != null) {
                books.get(index).issueBook(bor, (Staff) person);
            }
        }
    }

    private static void returnBook(Library lib, Person person) {
        Borrower bor = lib.findBorrower();
        if (bor != null) {
            ArrayList<Loan> loans = bor.getBorrowedBooks();
            if (!loans.isEmpty()) {
                int index = getIndex(loans.size());
                Loan l = loans.get(index);
                l.getBook().returnBook(bor, l, (Staff) person);
            }
        }
    }

    private static void renewBook(Library lib) {
        Borrower bor = lib.findBorrower();
        if (bor != null && !bor.getBorrowedBooks().isEmpty()) {
            int index = getIndex(bor.getBorrowedBooks().size());
            bor.getBorrowedBooks().get(index).renewIssuedBook(new java.util.Date());
        }
    }

    private static void addBook(Library lib) {
        String title = input("Enter Title");
        String subject = input("Enter Subject");
        String author = input("Enter Author");
        lib.createBook(title, subject, author);
    }

    private static void removeBook(Library lib) throws IOException {
        ArrayList<Book> books = lib.searchForBooks();
        if (books != null) {
            int index = getIndex(books.size());
            lib.removeBookfromLibrary(books.get(index));
        }
    }

    private static void changeBookInfo(Library lib) throws IOException {
        ArrayList<Book> books = lib.searchForBooks();
        if (books != null) {
            int index = getIndex(books.size());
            books.get(index).changeBookInfo();
        }
    }

    /* ===================== MAIN (FLOW UNCHANGED) ===================== */

    public static void main(String[] args) throws IOException {

        Library lib = Library.getInstance();
        lib.setFine(20);
        lib.setRequestExpiry(7);
        lib.setReturnDeadline(5);
        lib.setName("FAST Library");

        Connection con = lib.makeConnection();
        if (con == null) {
            error("Database Connection Failed");
            return;
        }

        try {
            lib.populateLibrary(con);
        } catch (SQLException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {

            int choice = menu("Main Menu", new String[]{
                    "Login", "Exit", "Administrative Functions"
            });

            if (choice == 1) break;

            if (choice == 2) {
                String pass = input("Enter Admin Password");
                if (!ADMIN_PASSWORD.equals(pass)) {
                    error("Wrong Password");
                    continue;
                }

                adminPanel(lib);
            }

            if (choice == 0) {
                Person person = lib.login();
                if (person != null) userPanel(lib, person);
            }
        }

        try {
            lib.fillItBack(con);
        } catch (SQLException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* ===================== PANELS ===================== */

    private static void adminPanel(Library lib) {

        while (true) {
            int choice = menu("Admin Panel", new String[]{
                    "Add Clerk", "Add Librarian", "View Issued History", "View All Books", "Logout"
            });

            if (choice == 4) return;

            switch (choice) {
                case 0: lib.createPerson('c'); break;
                case 1: lib.createPerson('l'); break;
                case 2: lib.viewHistory(); break;
                case 3: lib.viewAllBooks(); break;
            }
        }
    }

    private static void userPanel(Library lib, Person person) throws IOException {

        String role = person.getClass().getSimpleName();

        String[] menu =
                role.equals("Borrower") ?
                        new String[]{"Search", "Hold", "Info", "Fine", "Hold Queue", "Logout"} :
                        role.equals("Clerk") ?
                                new String[]{"Search", "Hold", "Info", "Fine", "Hold Queue",
                                        "Issue", "Return", "Renew", "Add Borrower", "Update Borrower", "Logout"} :
                                new String[]{"Search", "Hold", "Info", "Fine", "Hold Queue",
                                        "Issue", "Return", "Renew", "Add Borrower", "Update Borrower",
                                        "Add Book", "Remove Book", "Change Book", "Clerk Info", "Logout"};

        while (true) {
            int choice = menu(role + " Panel", menu);
            if (choice == menu.length - 1) return;
            performFunctionality(person, choice);
        }
    }
}
