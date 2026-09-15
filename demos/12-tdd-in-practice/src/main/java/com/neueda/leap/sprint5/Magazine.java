package com.neueda.leap.sprint5;

// Magazines: a daily late fee of 10p per day overdue (half the rate of books).
// This is a Library-Model replacement for EquityInstrument.
public class Magazine extends LibraryResource {

    private static final double DAILY_LATE_FEE = 0.10;

    public Magazine(String id) {
        super(id);
    }

    @Override
    public double calculateLateFee(int daysOverdue) {
        return daysOverdue * DAILY_LATE_FEE;
    }
}
