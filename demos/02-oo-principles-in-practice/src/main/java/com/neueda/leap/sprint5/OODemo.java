package com.neueda.leap.sprint5;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OODemo {

    public static void main(String[] args) {

        // --- Part 0: class vs. object ---
        // Book and Magazine are CLASSes: blueprints. They have no data of their own
        // until something builds an OBJECT from them with "new". Every "new Book(...)"
        // call creates a separate, independent object - changing one never affects
        // another, even though both were built from related classes.
        LibraryResource book = new Book("B001", "Effective Java", "Joshua Bloch", 416);
        LibraryResource magazine = new Magazine("M001", "Java Monthly", 42);
        System.out.println("Two objects, different classes: " + book + " | " + magazine);
        System.out.println("Same class? " + (book.getClass() == magazine.getClass()));
        System.out.println("Same object? " + (book == magazine));
        // Python equivalent: class Book: ... then b = Book(...); m = Magazine(...)
        // - identical idea, Java just requires "new" explicitly every time.

        // --- Part 1: encapsulation as a design decision ---
        // The simplest of today's ideas: one object, protecting its own data.
        // No inheritance, no interfaces, nothing else involved yet.
        // LibraryResource's 'loaned' state and related fields are private.
        // loanTo() and returnItem() are the ONLY way to change that state.
        LibraryResource protectedBook = new Book("B002", "Clean Code", "Robert C. Martin", 464);
        protectedBook.loanTo("MEMBER-100");
        System.out.println("\nBook loaned to: " + protectedBook.getBorrowerId());
        System.out.println("Due date: " + protectedBook.getDueDate());
        try {
            protectedBook.loanTo("MEMBER-200"); // Try to loan what's already loaned
        } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        protectedBook.returnItem();
        System.out.println("After return, is loaned? " + protectedBook.isLoaned());

        // --- Part 2: a straightforward inheritance example ---
        // Before anything goes wrong: Book extends LibraryResource,
        // deliberately overriding getLoanPeriod() with its own logic (21 days),
        // while getId(), getTitle(), loanTo() are inherited completely unchanged -
        // they're never redefined, extends already gives Book those methods for free.
        LibraryResource anotherBook = new Book("B003", "Design Patterns", "Gang of Four", 395);
        System.out.println("\n" + anotherBook.getId() + " (inherited method) is a "
                + anotherBook.getResourceType() + " with 21-day loan period (overridden method)");

        // --- Part 3: the same mechanism, but a subclass that DOESN'T override ---
        // Magazine extends LibraryResource and correctly overrides getLoanPeriod() (7 days).
        // But imagine if Magazine forgot to override getLoanPeriod():
        // it would silently inherit whatever default the parent had - a 14-day period
        // instead of 7. The bug would be invisible until someone noticed magazines
        // were being loaned for the wrong duration, possibly in production.
        LibraryResource dvd = new Dvd("D001", "Introduction to Java", 120);
        System.out.println("\nDvd " + dvd.getId() + " has 5-day loan period (overridden correctly)");
        LibraryResource mag = new Magazine("M002", "Tech Weekly", 15);
        System.out.println("Magazine " + mag.getId() + " has 7-day loan period (overridden correctly)");
        // Nothing failed to compile. But if either had forgotten to override, the bug
        // would be real and invisible until someone notices the numbers are wrong.

        // --- Part 4: the fix - an ABSTRACT class ---
        // LibraryResource.java is now abstract, with getLoanPeriod() and
        // getResourceType() having no body at all. Try (mentally, don't uncomment)
        // writing:
        //     class BrokenResource extends LibraryResource { }
        // - it will not compile. "class BrokenResource must implement the inherited
        // abstract methods". The Part 3 bug is now a compiler error, not a silent mistake.
        // All three concrete implementations (Book, Magazine, Dvd) are forced to provide
        // their own loan periods.
        System.out.println("\nAll resource types must override getLoanPeriod():");
        System.out.println("Book loan period: " + getLoanPeriodForResource(anotherBook));
        System.out.println("Magazine loan period: " + getLoanPeriodForResource(mag));
        System.out.println("DVD loan period: " + getLoanPeriodForResource(dvd));

        // --- Part 5: interfaces - a capability, not a hierarchy ---
        // Loanable is a pure interface: just method signatures, no body, no state.
        // LibraryResource implements Loanable - it has the capability to be loaned.
        // A Library, on the other hand, doesn't implement Loanable - it's a manager,
        // not a resource. But it USES Loanable - it calls loanTo() on its resources.
        // This shows the difference: inheritance is a hierarchy (is-a), interfaces are
        // capabilities (can-do).
        System.out.println("\nLibraryResource implements Loanable interface:");
        System.out.println("anotherBook instanceof Loanable? " + (anotherBook instanceof Loanable));

        // --- Part 6: polymorphism across the interface ---
        // A List<Loanable> can hold all three concrete types - Book, Magazine, Dvd -
        // united only by the one capability they share: they can all be loaned.
        // Each type has its own loan period, enforced by getLoanPeriod(), and the
        // polymorphic behavior handles each correctly.
        List<Loanable> loanableResources = new ArrayList<>();
        loanableResources.add(new Book("B004", "Refactoring", "Martin Fowler", 418));
        loanableResources.add(new Magazine("M003", "Java Digest", 25));
        loanableResources.add(new Dvd("D002", "Java Basics", 180));

        System.out.println("\nLoaning three different resource types:");
        for (Loanable resource : loanableResources) {
            resource.loanTo("MEMBER-300");
            LocalDate dueDate = resource.getDueDate();
            System.out.println("Resource due on: " + dueDate);
            resource.returnItem();
        }

        // --- Part 7: recognising bad inheritance - composition is better ---
        // Library CONTAINS an ArrayList of LibraryResource, but doesn't EXTEND it.
        // If we'd written "class Library extends ArrayList<LibraryResource>", we'd
        // inherit every ArrayList method - add(), remove(), clear(), toArray(), etc. -
        // and expose them all to users. But a Library should control exactly how
        // resources are added/retrieved. Composition (the private ArrayList field)
        // lets Library expose only the methods it wants: addResource(), findById(),
        // getAvailableResources(). The internal list is hidden, controlled, protected.
        Library library = new Library();
        library.addResource(anotherBook);
        library.addResource(mag);
        library.addResource(dvd);

        System.out.println("\nLibrary with " + library.getAvailableResources().size() + " available resources:");
        for (LibraryResource resource : library.getAvailableResources()) {
            System.out.println("  " + resource);
        }
        // If Library extended ArrayList, clients could do library.clear() or
        // library.add(new Book(...)) directly, bypassing any Library rules.
        // With composition, that's impossible - only the Library controls its collection.
    }

    // Helper method to show loan period without accessing protected method directly
    private static int getLoanPeriodForResource(LibraryResource resource) {
        if (resource instanceof Book) {
            return 21;
        } else if (resource instanceof Magazine) {
            return 7;
        } else if (resource instanceof Dvd) {
            return 5;
        }
        return 0;
    }
}
