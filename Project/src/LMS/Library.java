package LMS;

import javax.swing.JOptionPane;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Library {

    private String name;
    public static Librarian librarian;
    public static ArrayList<Person> persons;
    private ArrayList<Book> booksInLibrary;
    private ArrayList<Loan> loans;

    public int book_return_deadline;
    public double per_day_fine;
    public int hold_request_expiry;
    private HoldRequestOperations holdRequestsOperations = new HoldRequestOperations();

    private static Library obj;

    public static Library getInstance() {
        if (obj == null) obj = new Library();
        return obj;
    }

    private Library() {
        name = null;
        librarian = null;
        persons = new ArrayList<>();
        booksInLibrary = new ArrayList<>();
        loans = new ArrayList<>();
    }

    public void setReturnDeadline(int deadline) {
        book_return_deadline = deadline;
    }

    public void setFine(double perDayFine) {
        per_day_fine = perDayFine;
    }

    public void setRequestExpiry(int hrExpiry) {
        hold_request_expiry = hrExpiry;
    }

    public void setName(String n) {
        name = n;
    }

    public int getHoldRequestExpiry() {
        return hold_request_expiry;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public String getLibraryName() {
        return name;
    }

    public ArrayList<Book> getBooks() {
        return booksInLibrary;
    }

    public void addClerk(Clerk c) {
        persons.add(c);
    }

    public void addBorrower(Borrower b) {
        persons.add(b);
    }

    public void addLoan(Loan l) {
        loans.add(l);
    }

    public Borrower findBorrower() {
        String idStr = JOptionPane.showInputDialog("Enter Borrower's ID: ");
        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Input");
        }

        for (Person p : persons) {
            if (p.getID() == id && p.getClass().getSimpleName().equals("Borrower"))
                return (Borrower) p;
        }
        JOptionPane.showMessageDialog(null, "Sorry this ID didn't match any Borrower's ID.");
        return null;
    }

    public Clerk findClerk() {
        String idStr = JOptionPane.showInputDialog("Enter Clerk's ID: ");
        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Input");
        }

        for (Person p : persons) {
            if (p.getID() == id && p.getClass().getSimpleName().equals("Clerk"))
                return (Clerk) p;
        }
        JOptionPane.showMessageDialog(null, "Sorry this ID didn't match any Clerk's ID.");
        return null;
    }

    public void addBookinLibrary(Book b) {
        booksInLibrary.add(b);
    }

    public void removeBookfromLibrary(Book b) {
        boolean delete = true;
        for (Person p : persons) {
            if (p.getClass().getSimpleName().equals("Borrower")) {
                ArrayList<Loan> borBooks = ((Borrower) p).getBorrowedBooks();
                for (Loan loan : borBooks) {
                    if (loan.getBook() == b) {
                        delete = false;
                        JOptionPane.showMessageDialog(null, "This particular book is currently borrowed by some borrower.");
                        break;
                    }
                }
            }
        }

        if (delete) {
            ArrayList<HoldRequest> hRequests = b.getHoldRequests();
            if (!hRequests.isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(null,
                        "This book has hold requests. Do you still want to delete it?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.NO_OPTION) {
                    JOptionPane.showMessageDialog(null, "Delete Unsuccessful.");
                    return;
                } else {
                    for (HoldRequest hr : hRequests) {
                        hr.getBorrower().removeHoldRequest(hr);
                        holdRequestsOperations.removeHoldRequest();
                    }
                }
            }
            booksInLibrary.remove(b);
            JOptionPane.showMessageDialog(null, "The book is successfully removed.");
        } else {
            JOptionPane.showMessageDialog(null, "Delete Unsuccessful.");
        }
    }

    public ArrayList<Book> searchForBooks() {
        String choice = JOptionPane.showInputDialog("Enter 1 for Title, 2 for Subject, 3 for Author: ");
        while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
            choice = JOptionPane.showInputDialog("Invalid input! Enter 1, 2, or 3:");
        }

        String query = "";
        if (choice.equals("1")) query = JOptionPane.showInputDialog("Enter Title of Book:");
        if (choice.equals("2")) query = JOptionPane.showInputDialog("Enter Subject of Book:");
        if (choice.equals("3")) query = JOptionPane.showInputDialog("Enter Author of Book:");

        ArrayList<Book> matchedBooks = new ArrayList<>();
        for (Book b : booksInLibrary) {
            if (choice.equals("1") && b.getTitle().equals(query)) matchedBooks.add(b);
            if (choice.equals("2") && b.getSubject().equals(query)) matchedBooks.add(b);
            if (choice.equals("3") && b.getAuthor().equals(query)) matchedBooks.add(b);
        }

        if (!matchedBooks.isEmpty()) {
            StringBuilder sb = new StringBuilder("Matched Books:\n");
            for (int i = 0; i < matchedBooks.size(); i++) {
                sb.append(i).append("- ").append(matchedBooks.get(i).getTitle()).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
            return matchedBooks;
        } else {
            JOptionPane.showMessageDialog(null, "No books found.");
            return null;
        }
    }

    public void viewAllBooks() {
        if (!booksInLibrary.isEmpty()) {
            StringBuilder sb = new StringBuilder("Books in Library:\n");
            for (Book b : booksInLibrary) {
                sb.append(b.getTitle()).append(" | ").append(b.getAuthor()).append(" | ").append(b.getSubject()).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        } else {
            JOptionPane.showMessageDialog(null, "Library has no books.");
        }
    }

    public double computeFine2(Borrower borrower) {
        double totalFine = 0;
        for (Loan l : loans) {
            if (l.getBorrower() == borrower) {
                totalFine += l.computeFine1();
            }
        }
        return totalFine;
    }

    public void createPerson(char x) {
        String n = JOptionPane.showInputDialog("Enter Name:");
        String address = JOptionPane.showInputDialog("Enter Address:");
        int phone = 0;
        try {
            phone = Integer.parseInt(JOptionPane.showInputDialog("Enter Phone Number:"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Phone Number.");
        }

        if (x == 'c') {
            double salary = 0;
            try {
                salary = Double.parseDouble(JOptionPane.showInputDialog("Enter Salary:"));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid Salary.");
            }
            Clerk c = new Clerk(-1, n, address, phone, salary, -1);
            addClerk(c);
            JOptionPane.showMessageDialog(null, "Clerk " + n + " created.\nID: " + c.getID() + "\nPassword: " + c.getPassword());
        } else if (x == 'l') {
            double salary = 0;
            try {
                salary = Double.parseDouble(JOptionPane.showInputDialog("Enter Salary:"));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid Salary.");
            }
            Librarian l = new Librarian(-1, n, address, phone, salary, -1);
            if (Librarian.addLibrarian(l))
                JOptionPane.showMessageDialog(null, "Librarian " + n + " created.\nID: " + l.getID() + "\nPassword: " + l.getPassword());
        } else {
            Borrower b = new Borrower(-1, n, address, phone);
            addBorrower(b);
            JOptionPane.showMessageDialog(null, "Borrower " + n + " created.\nID: " + b.getID() + "\nPassword: " + b.getPassword());
        }
    }

    public void createBook(String title, String subject, String author) {
        Book b = new Book(-1, title, subject, author, false);
        addBookinLibrary(b);
        JOptionPane.showMessageDialog(null, "Book " + title + " created.");
    }

    public Person login() {
        int id = 0;
        try {
            id = Integer.parseInt(JOptionPane.showInputDialog("Enter ID:"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Input");
        }
        String password = JOptionPane.showInputDialog("Enter Password:");

        for (Person p : persons) {
            if (p.getID() == id && p.getPassword().equals(password)) {
                JOptionPane.showMessageDialog(null, "Login Successful");
                return p;
            }
        }
        if (librarian != null) {
            if (librarian.getID() == id && librarian.getPassword().equals(password)) {
                JOptionPane.showMessageDialog(null, "Login Successful");
                return librarian;
            }
        }
        JOptionPane.showMessageDialog(null, "Wrong ID or Password");
        return null;
    }

    public void viewHistory()
{
    if (!loans.isEmpty())
    { 
        System.out.println("\nIssued Books are: ");
        
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");            
        System.out.println("No.\tBook's Title\tBorrower's Name\t  Issuer's Name\t\tIssued Date\t\t\tReceiver's Name\t\tReturned Date\t\tFine Paid");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");
        
        for (int i = 0; i < loans.size(); i++)
        {    
            if (loans.get(i).getIssuer() != null)
                System.out.print(i + "-" + "\t" + loans.get(i).getBook().getTitle() + "\t\t\t" + loans.get(i).getBorrower().getName() + "\t\t" + loans.get(i).getIssuer().getName() + "\t    " + loans.get(i).getIssuedDate());
            
            if (loans.get(i).getReceiver() != null)
            {
                System.out.print("\t" + loans.get(i).getReceiver().getName() + "\t\t" + loans.get(i).getReturnDate() + "\t   " + loans.get(i).getFineStatus() + "\n");
            }
            else
                System.out.print("\t\t" + "--" + "\t\t\t" + "--" + "\t\t" + "--" + "\n");
        }
    }
    else
        System.out.println("\nNo issued books.");                        
}
    
    // Making Connection With Database    
public Connection makeConnection()
{        
    try
    {
        String host = "jdbc:derby://localhost:1527/LMS";
        String uName = "haris";
        String uPass= "123";
        Connection con = DriverManager.getConnection(host, uName, uPass);
        return con;
    }
    catch (SQLException err) 
    {
        System.out.println(err.getMessage());
        return null;
    }   
}

   // Loading all info in code via Database.
    public void populateLibrary(Connection con) throws SQLException, IOException
    {       
            Library lib = this;
            Statement stmt = con.createStatement( );
            
            /* --- Populating Book ----*/
            String SQL = "SELECT * FROM BOOK";
            ResultSet rs = stmt.executeQuery( SQL );
            
            if(!rs.next())
            {
               System.out.println("\nNo Books Found in Library"); 
            }
            else
            {
                int maxID = 0;
                
                do
                {
                    if(rs.getString("TITLE") !=null && rs.getString("AUTHOR")!=null && rs.getString("SUBJECT")!=null && rs.getInt("ID")!=0)
                    {
                        String title=rs.getString("TITLE");
                        String author=rs.getString("AUTHOR");
                        String subject=rs.getString("SUBJECT");
                        int id= rs.getInt("ID");
                        boolean issue=rs.getBoolean("IS_ISSUED");
                        Book b = new Book(id,title,subject,author,issue);
                        addBookinLibrary(b);
                        
                        if (maxID < id)
                            maxID = id;
                    }
                }while(rs.next());
                
                // setting Book Count
                Book.setIDCount(maxID);              
            }
            
            /* ----Populating Clerks----*/
           
            SQL="SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,DESK_NO FROM PERSON INNER JOIN CLERK ON ID=C_ID INNER JOIN STAFF ON S_ID=C_ID";
            
            rs=stmt.executeQuery(SQL);
                      
            if(!rs.next())
            {
               System.out.println("No clerks Found in Library"); 
            }
            else
            {
                do
                {
                    int id=rs.getInt("ID");
                    String cname=rs.getString("PNAME");
                    String adrs=rs.getString("ADDRESS"); 
                    int phn=rs.getInt("PHONE_NO");
                    double sal=rs.getDouble("SALARY");
                    int desk=rs.getInt("DESK_NO");
                    Clerk c = new Clerk(id,cname,adrs,phn,sal,desk);
                    
                    addClerk(c);
                }
                while(rs.next());
                                
            }
            
            /*-----Populating Librarian---*/
            SQL="SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,OFFICE_NO FROM PERSON INNER JOIN LIBRARIAN ON ID=L_ID INNER JOIN STAFF ON S_ID=L_ID";
            
            rs=stmt.executeQuery(SQL);
            if(!rs.next())
            {
               System.out.println("No Librarian Found in Library"); 
            }
            else
            {
                do
                {
                    int id=rs.getInt("ID");
                    String lname=rs.getString("PNAME");
                    String adrs=rs.getString("ADDRESS"); 
                    int phn=rs.getInt("PHONE_NO");
                    double sal=rs.getDouble("SALARY");
                    int off=rs.getInt("OFFICE_NO");
                    Librarian l= new Librarian(id,lname,adrs,phn,sal,off);

                    Librarian.addLibrarian(l);
                    
                }while(rs.next());
           
            }
                                    
            /*---Populating Borrowers (partially)!!!!!!--------*/
            
            SQL="SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO FROM PERSON INNER JOIN BORROWER ON ID=B_ID";
            
            rs=stmt.executeQuery(SQL);
                      
            if(!rs.next())
            {
               System.out.println("No Borrower Found in Library"); 
            }
            else
            {
                do
                {
                        int id=rs.getInt("ID");
                        String name=rs.getString("PNAME");
                        String adrs=rs.getString("ADDRESS"); 
                        int phn=rs.getInt("PHONE_NO"); 
                        
                        Borrower b= new Borrower(id,name,adrs,phn);
                        addBorrower(b);
                                                
                }while(rs.next());
                                
            }
            
            /*----Populating Loan----*/
            
            SQL="SELECT * FROM LOAN";
            
            rs=stmt.executeQuery(SQL);
            if(!rs.next())
            {
               System.out.println("No Books Issued Yet!"); 
            }
            else
            {
                do
                    {
                        int borid=rs.getInt("BORROWER");
                        int bokid=rs.getInt("BOOK");
                        int iid=rs.getInt("ISSUER");
                        Integer rid=(Integer)rs.getObject("RECEIVER");
                        int rd=0;
//                        Date rdate;
                        
                        java.util.Date idate = new java.util.Date(rs.getTimestamp("ISS_DATE").getTime());

java.util.Date rdate = null;  // declare null initially
                        
                      if (rid != null) {    // if there is a receiver 
    rdate = new java.util.Date(rs.getTimestamp("RET_DATE").getTime()); 
    rd = (int) rid;
} else {
    rdate = null;
}
                        
                        boolean fineStatus = rs.getBoolean("FINE_PAID");
                        
                        boolean set=true;
                        
                        Borrower bb = null;
                       
                        
                        for(int i=0;i<getPersons().size() && set;i++)
                        {
                            if(getPersons().get(i).getID()==borid)
                            {
                                set=false;
                                bb=(Borrower)(getPersons().get(i));
                            }
                        }
                        
                        set =true;
                        Staff s[]=new Staff[2];
                        
                        if(iid==getLibrarian().getID())
                        {
                            s[0]=getLibrarian();
                        }
                            
                        else
                        {                                
                            for(int k=0;k<getPersons().size() && set;k++)
                            {
                                if(getPersons().get(k).getID()==iid && getPersons().get(k).getClass().getSimpleName().equals("Clerk"))
                                {
                                    set=false;
                                    s[0]=(Clerk)(getPersons().get(k));
                                }
                            }
                        }       
                        
                        set=true;
                        // If not returned yet...
                        if(rid==null)
                        {
                            s[1]=null;  // no reciever 
                            rdate=null;      
                        }
                        else
                        {
                            if(rd==getLibrarian().getID())
                                s[1]=getLibrarian();

                            else
                            {    //System.out.println("ff");
                                 for(int k=0;k<getPersons().size() && set;k++)
                                {
                                    if(getPersons().get(k).getID()==rd && getPersons().get(k).getClass().getSimpleName().equals("Clerk"))
                                    {
                                        set=false;
                                        s[1]=(Clerk)(getPersons().get(k));
                                    }
                                }
                            }     
                        }
                        
                        set=true;
                        
                        ArrayList<Book> books = getBooks();
                        
                        for(int k=0;k<books.size() && set;k++)
                        {
                            if(books.get(k).getID()==bokid)
                            {
                              set=false;   
                              Loan l = new Loan(bb,books.get(k),s[0],s[1],idate,rdate,fineStatus);
                              loans.add(l);
                            }
                        }
                        
                    }while(rs.next());
            }
            
            /*----Populationg Hold Books----*/
            
            SQL="SELECT * FROM ON_HOLD_BOOK";
            
            rs=stmt.executeQuery(SQL);
            if(!rs.next())
            {
               System.out.println("No Books on Hold Yet!"); 
            }
            else
            {
                do
                    {
                        int borid=rs.getInt("BORROWER");
                        int bokid=rs.getInt("BOOK");
                        java.util.Date off = new java.util.Date(rs.getDate("REQ_DATE").getTime());
                        
                        boolean set=true;
                        Borrower bb =null;
                        
                        ArrayList<Person> persons = lib.getPersons();
                        
                        for(int i=0;i<persons.size() && set;i++)
                        {
                            if(persons.get(i).getID()==borid)
                            {
                                set=false;
                                bb=(Borrower)(persons.get(i));
                            }
                        }
                                              
                        set=true;
                        
                        ArrayList<Book> books = lib.getBooks();
                        
                        for(int i=0;i<books.size() && set;i++)
                        {
                            if(books.get(i).getID()==bokid)
                            {
                              set=false;   
                              HoldRequest hbook= new HoldRequest(bb,books.get(i),off);
                             holdRequestsOperations.addHoldRequest(hbook);
                             bb.addHoldRequest(hbook);
                            }
                        }
                        }while(rs.next());
            }
            
            /* --- Populating Borrower's Remaining Info----*/
            
            // Borrowed Books
            SQL="SELECT ID,BOOK FROM PERSON INNER JOIN BORROWER ON ID=B_ID INNER JOIN BORROWED_BOOK ON B_ID=BORROWER ";
            
            rs=stmt.executeQuery(SQL);
                      
            if(!rs.next())
            {
               System.out.println("No Borrower has borrowed yet from Library"); 
            }
            else
            {
                
                do
                    {
                        int id=rs.getInt("ID");      // borrower
                        int bid=rs.getInt("BOOK");   // book
                        
                        Borrower bb=null;
                        boolean set=true;
                        boolean okay=true;
                        
                        for(int i=0;i<lib.getPersons().size() && set;i++)
                        {
                            if(lib.getPersons().get(i).getClass().getSimpleName().equals("Borrower"))
                            {
                                if(lib.getPersons().get(i).getID()==id)
                                {
                                   set =false;
                                    bb=(Borrower)(lib.getPersons().get(i));
                                }
                            }
                        }
                        
                        set=true;
                        
                        ArrayList<Loan> books = loans;
                        
                        for(int i=0;i<books.size() && set;i++)
                        {
                            if(books.get(i).getBook().getID()==bid &&books.get(i).getReceiver()==null )
                            {
                              set=false;   
                              Loan bBook= new Loan(bb,books.get(i).getBook(),books.get(i).getIssuer(),null,books.get(i).getIssuedDate(),null,books.get(i).getFineStatus());
                              bb.addBorrowedBook(bBook);
                            }
                        }
                                 
                    }while(rs.next());               
            }
                      
            ArrayList<Person> persons = lib.getPersons();
            
            /* Setting Person ID Count */
            int max=0;
            
            for(int i=0;i<persons.size();i++)
            {
                if (max < persons.get(i).getID())
                    max=persons.get(i).getID();
            }

            Person.setIDCount(max);  
    }
    
    
    // Filling Changes back to Database
    public void fillItBack(Connection con) throws SQLException,SQLIntegrityConstraintViolationException
    {
            /*-----------Loan Table Cleared------------*/
            
            String template = "DELETE FROM LIBRARY.LOAN";
            PreparedStatement stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
                        
            /*-----------Borrowed Books Table Cleared------------*/
            
            template = "DELETE FROM LIBRARY.BORROWED_BOOK";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
                       
            /*-----------OnHoldBooks Table Cleared------------*/
            
            template = "DELETE FROM LIBRARY.ON_HOLD_BOOK";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
            
            /*-----------Books Table Cleared------------*/
            
            template = "DELETE FROM LIBRARY.BOOK";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
                       
            /*-----------Clerk Table Cleared------------*/
            
            template = "DELETE FROM LIBRARY.CLERK";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
            
            /*-----------Librarian Table Cleared------------*/
            
            template = "DELETE FROM LIBRARY.LIBRARIAN";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
                       
            /*-----------Borrower Table Cleared------------*/
            
            template = "DELETE FROM LIBRARY.BORROWER";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
            
            /*-----------Staff Table Cleared------------*/
            
            template = "DELETE FROM LIBRARY.STAFF";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
            
            /*-----------Person Table Cleared------------*/
            
            template = "DELETE FROM LIBRARY.PERSON";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
           
            Library lib = this;
            
        /* Filling Person's Table*/
        for(int i=0;i<lib.getPersons().size();i++)
        {
            template = "INSERT INTO LIBRARY.PERSON (ID,PNAME,PASSWORD,ADDRESS,PHONE_NO) values (?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
            
            stmt.setInt(1, lib.getPersons().get(i).getID());
            stmt.setString(2, lib.getPersons().get(i).getName());
            stmt.setString(3,  lib.getPersons().get(i).getPassword());
            stmt.setString(4, lib.getPersons().get(i).getAddress());
            stmt.setInt(5, lib.getPersons().get(i).getPhoneNumber());
            
            stmt.executeUpdate();
        }
        
        /* Filling Clerk's Table and Staff Table*/
        for(int i=0;i<lib.getPersons().size();i++)
        {
            if (lib.getPersons().get(i).getClass().getSimpleName().equals("Clerk"))
            {
                template = "INSERT INTO LIBRARY.STAFF (S_ID,TYPE,SALARY) values (?,?,?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1,lib.getPersons().get(i).getID());
                stmt.setString(2,"Clerk");
                stmt.setDouble(3, ((Clerk)(lib.getPersons().get(i))).getSalary());

                stmt.executeUpdate();

                template = "INSERT INTO LIBRARY.CLERK (C_ID,DESK_NO) values (?,?)";
                stmt = con.prepareStatement(template);

                stmt.setInt(1,lib.getPersons().get(i).getID());
                stmt.setInt(2, ((Clerk)(lib.getPersons().get(i))).deskNo);

                stmt.executeUpdate();
            }
        
        }
        
        if(lib.getLibrarian()!=null)    // if  librarian is there
            {
            template = "INSERT INTO LIBRARY.STAFF (S_ID,TYPE,SALARY) values (?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
             
            stmt.setInt(1, lib.getLibrarian().getID());
            stmt.setString(2,"Librarian");
            stmt.setDouble(3,lib.getLibrarian().getSalary());
            
            stmt.executeUpdate();
            
            template = "INSERT INTO LIBRARY.LIBRARIAN (L_ID,OFFICE_NO) values (?,?)";
            stmt = con.prepareStatement(template);
            
            stmt.setInt(1,lib.getLibrarian().getID());
            stmt.setInt(2, lib.getLibrarian().officeNo);
            
            stmt.executeUpdate();  
            }
        
        /* Filling Borrower's Table*/
        for(int i=0;i<lib.getPersons().size();i++)
        {
            if (lib.getPersons().get(i).getClass().getSimpleName().equals("Borrower"))
            {
                template = "INSERT INTO LIBRARY.BORROWER(B_ID) values (?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1, lib.getPersons().get(i).getID());

                stmt.executeUpdate();    
            }
        }
                       
        ArrayList<Book> books = lib.getBooks();
        
        /*Filling Book's Table*/
        for(int i=0;i<books.size();i++)
        {
            template = "INSERT INTO LIBRARY.BOOK (ID,TITLE,AUTHOR,SUBJECT,IS_ISSUED) values (?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
            
            stmt.setInt(1,books.get(i).getID());
            stmt.setString(2,books.get(i).getTitle());
            stmt.setString(3, books.get(i).getAuthor());
            stmt.setString(4, books.get(i).getSubject());
            stmt.setBoolean(5, books.get(i).getIssuedStatus());
            stmt.executeUpdate();
            
        }
         
        /* Filling Loan Book's Table*/
        for(int i=0;i<loans.size();i++)
        {
            template = "INSERT INTO LIBRARY.LOAN(L_ID,BORROWER,BOOK,ISSUER,ISS_DATE,RECEIVER,RET_DATE,FINE_PAID) values (?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
            
            stmt.setInt(1,i+1);
            stmt.setInt(2,loans.get(i).getBorrower().getID());
            stmt.setInt(3,loans.get(i).getBook().getID());
            stmt.setInt(4,loans.get(i).getIssuer().getID());
            stmt.setTimestamp(5,new java.sql.Timestamp(loans.get(i).getIssuedDate().getTime()));
            stmt.setBoolean(8,loans.get(i).getFineStatus());
            if(loans.get(i).getReceiver()==null)
            {
                stmt.setNull(6,Types.INTEGER); 
                stmt.setDate(7,null);
            }
            else
            {
                stmt.setInt(6,loans.get(i).getReceiver().getID());  
                stmt.setTimestamp(7,new java.sql.Timestamp(loans.get(i).getReturnDate().getTime()));
            }
                
            stmt.executeUpdate(); 
   
        }
       
        /* Filling On_Hold_ Table*/
        
        int x=1;
        for(int i=0;i<lib.getBooks().size();i++)
        {
            for(int j=0;j<lib.getBooks().get(i).getHoldRequests().size();j++)
            {
            template = "INSERT INTO LIBRARY.ON_HOLD_BOOK(REQ_ID,BOOK,BORROWER,REQ_DATE) values (?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
            
            stmt.setInt(1,x);
            stmt.setInt(3,lib.getBooks().get(i).getHoldRequests().get(j).getBorrower().getID());
            stmt.setInt(2,lib.getBooks().get(i).getHoldRequests().get(j).getBook().getID());
            stmt.setDate(4,new java.sql.Date(lib.getBooks().get(i).getHoldRequests().get(j).getRequestDate().getTime()));
                    
            stmt.executeUpdate(); 
            x++;
            
            }
        }
            
        /* Filling Borrowed Book Table*/
        for(int i=0;i<lib.getBooks().size();i++)
          {
              if(lib.getBooks().get(i).getIssuedStatus()==true)
              {
                  boolean set=true;
                  for(int j=0;j<loans.size() && set ;j++)
                  {
                      if(lib.getBooks().get(i).getID()==loans.get(j).getBook().getID())
                      {
                          if(loans.get(j).getReceiver()==null)
                          {
                            template = "INSERT INTO LIBRARY.BORROWED_BOOK(BOOK,BORROWER) values (?,?)";
                            PreparedStatement stmt = con.prepareStatement(template);
                            stmt.setInt(1,loans.get(j).getBook().getID());
                            stmt.setInt(2,loans.get(j).getBorrower().getID());
                  
                            stmt.executeUpdate();
                            set=false;
                          }
                      }
                      
                  }
                  
              }
          }   
    } // Filling Done!     
  
}   // Library Class Closed


