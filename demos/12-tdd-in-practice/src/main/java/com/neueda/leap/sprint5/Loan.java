package com.neueda.leap.sprint5;

import java.time.LocalDate;

// A Loan represents a member's borrowing of a library resource (book, magazine, DVD).
// Encapsulation as a DESIGN DECISION: the field is private specifically because this class 
// has an invariant to protect: daysOverdue can never be negative, and a due date must be valid.
// Making the field private means the rule is enforced in exactly one place, permanently.
public class Loan {

    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(LocalDate dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("due date cannot be null");
        }
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void returnResource(LocalDate actualReturnDate) {
        if (actualReturnDate == null) {
            throw new IllegalArgumentException("return date cannot be null");
        }
        this.returnDate = actualReturnDate;
    }

    public int calculateDaysOverdue(LocalDate currentDate) {
        if (returnDate == null) {
            // Loan still active; check against current date
            if (currentDate.isAfter(dueDate)) {
                return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, currentDate);
            }
            return 0;
        } else {
            // Loan has been returned; check against return date
            if (returnDate.isAfter(dueDate)) {
                return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
            }
            return 0;
        }
    }

    public boolean isOverdue(LocalDate currentDate) {
        LocalDate checkDate = returnDate != null ? returnDate : currentDate;
        return checkDate.isAfter(dueDate);
    }
}
