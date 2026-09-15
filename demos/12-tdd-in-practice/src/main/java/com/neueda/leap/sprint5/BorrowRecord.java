package com.neueda.leap.sprint5;

import java.time.LocalDate;

// A BorrowRecord represents a completed loan transaction - the resource that was
// borrowed, the member who borrowed it, and key dates. This replaces the financial
// Order concept with a library borrowing concept.
public class BorrowRecord {

    private final String memberId;
    private final LibraryResource resource;
    private final LocalDate borrowDate;
    private final Loan loan;

    public BorrowRecord(String memberId, LibraryResource resource, LocalDate borrowDate, Loan loan) {
        this.memberId = memberId;
        this.resource = resource;
        this.borrowDate = borrowDate;
        this.loan = loan;
    }

    public String getMemberId() {
        return memberId;
    }

    public LibraryResource getResource() {
        return resource;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public Loan getLoan() {
        return loan;
    }

    public double calculateLateFee(LocalDate currentDate) {
        int daysOverdue = loan.calculateDaysOverdue(currentDate);
        return resource.calculateLateFee(daysOverdue);
    }
}
