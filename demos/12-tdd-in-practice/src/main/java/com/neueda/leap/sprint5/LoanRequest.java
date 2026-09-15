package com.neueda.leap.sprint5;

import java.time.LocalDate;

// A LoanRequest represents a member's request to borrow a library resource.
// It carries the resource to be borrowed and the member's current outstanding fees,
// because the validation rules need both independently to check if the borrow
// can be allowed (member cannot borrow if they have unpaid fees over £5).
public class LoanRequest {

    private final LibraryResource resource;
    private final double outstandingFees;
    private final int currentLoansCount;

    public LoanRequest(LibraryResource resource, double outstandingFees, int currentLoansCount) {
        this.resource = resource;
        this.outstandingFees = outstandingFees;
        this.currentLoansCount = currentLoansCount;
    }

    public LibraryResource getResource() {
        return resource;
    }

    public double getOutstandingFees() {
        return outstandingFees;
    }

    public int getCurrentLoansCount() {
        return currentLoansCount;
    }
}
