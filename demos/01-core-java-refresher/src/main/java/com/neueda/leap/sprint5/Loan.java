package com.neueda.leap.sprint5;

// A library loan record with encapsulated fields. Similar to the "trade" concept
// from Sprint 4, but now modeling a library system instead of financial transactions.
// Fields are private (encapsulation) with public getters - Module 2 covers why that
// matters as a design decision, not just syntax.
public class Loan {

    private final String loanId;
    private final String borrowerName;
    private final String bookTitle;
    private final String author;
    private final int daysCheckedOut;
    private final String status; // "ACTIVE" or "RETURNED"

    public Loan(String loanId, String borrowerName, String bookTitle,
                String author, int daysCheckedOut, String status) {
        this.loanId = loanId;
        this.borrowerName = borrowerName;
        this.bookTitle = bookTitle;
        this.author = author;
        this.daysCheckedOut = daysCheckedOut;
        this.status = status;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public int getDaysCheckedOut() {
        return daysCheckedOut;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOverdue() {
        return daysCheckedOut > 14;
    }
}
