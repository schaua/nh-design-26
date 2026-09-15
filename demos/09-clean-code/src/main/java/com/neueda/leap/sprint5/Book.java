package com.neueda.leap.sprint5;

// Books: a flat $1 late fee per day overdue.
public class Book extends LibraryResource {

    private static final double LATE_FEE_PER_DAY = 1.00;

    public Book(String resourceId) {
        super(resourceId);
    }

    @Override
    public double lateFeeCalculations(int daysOverdue) {
        return daysOverdue * LATE_FEE_PER_DAY;
    }
}
