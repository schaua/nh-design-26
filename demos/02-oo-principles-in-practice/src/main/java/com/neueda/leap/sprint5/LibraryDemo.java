package library;

public class LibraryDemo {
    public static void main(String[] args) {
        Library library = new Library();

        LibraryResource book = new Book(
                "B001",
                "Effective Java",
                "Joshua Bloch",
                416
        );

        LibraryResource magazine = new Magazine(
                "M001",
                "Java Monthly",
                42
        );

        LibraryResource dvd = new Dvd(
                "D001",
                "Introduction to Java",
                120
        );

        library.addResource(book);
        library.addResource(magazine);
        library.addResource(dvd);

        // Polymorphism: all objects are handled as LibraryResource objects
        for (LibraryResource resource : library.getAvailableResources()) {
            System.out.println(resource);
        }

        book.loanTo("MEMBER-100");

        System.out.println(book.getBorrowerId());
        System.out.println(book.getDueDate());

        book.returnItem();
    }
}
