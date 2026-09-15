package com.neueda.leap.sprint5;

// Books: a flat 20p late fee per day overdue.
// This is a Library-Model replacement for BondInstrument.
public class Book extends LibraryResource {

    private static final double DAILY_LATE_FEE = 0.20;

    public Book(String id) {
        super(id);
    }

    @Override
    public double calculateLateFee(int daysOverdue) {
        return daysOverdue * DAILY_LATE_FEE;
    }
}
