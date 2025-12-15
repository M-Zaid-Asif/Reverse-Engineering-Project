package LMS;

import static LMS.Library.librarian;
import static LMS.Library.persons;

public class Librarian extends Staff {

    int officeNo;     // Office Number of the Librarian
    public static int currentOfficeNumber = 0;

    public Librarian(int id, String name, String address, int phoneNo, double salary, int officeNo) {
        super(id, name, address, phoneNo, salary);

        if (officeNo == -1)
            this.officeNo = currentOfficeNumber;
        else
            this.officeNo = officeNo;

        currentOfficeNumber++;
    }

    // Printing Librarian's Info
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Office Number: " + officeNo);
    }

    public static boolean addLibrarian(Librarian lib) {
        // One Library can have only one Librarian
        if (librarian == null) {
            librarian = lib;
            persons.add(librarian);
            return true;
        } else
            System.out.println("\nSorry, the library already has one librarian. New Librarian can't be created.");
        return false;
    }
}
