package com.neueda.leap.sprint5;

// The corrected version - now extending the ABSTRACT LibraryResource, which forces
// this override to exist. Leave this method out entirely and the project simply won't compile.
// Books have a flat late fee regardless of loan duration.
public class Book extends LibraryResource {

    private static final double FLAT_FEE = 5.00;

    public Book(String resourceId) {
        super(resourceId);
    }

    @Override
    public double calculateFee(double loanDuration) {
        return FLAT_FEE;
    }
}
