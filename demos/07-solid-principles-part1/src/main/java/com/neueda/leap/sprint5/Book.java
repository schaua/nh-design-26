package com.neueda.leap.sprint5;

// A Book is a library resource with a flat late fee.
// Renamed from BondInstrument.
public class Book extends LibraryResource {

    private static final double DAILY_LATE_FEE = 0.50;

    public Book(String resourceId) {
        super(resourceId);
    }

    @Override
    public double calculateFee(double daysOverdue) {
        return DAILY_LATE_FEE * daysOverdue;
    }
}
