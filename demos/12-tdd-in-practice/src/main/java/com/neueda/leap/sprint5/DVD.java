package com.neueda.leap.sprint5;

// DVDs: a flat 30p late fee per day overdue.
// This is a Library-Model replacement for FundInstrument.
public class DVD extends LibraryResource {

    private static final double DAILY_LATE_FEE = 0.30;

    public DVD(String id) {
        super(id);
    }

    @Override
    public double calculateLateFee(int daysOverdue) {
        return daysOverdue * DAILY_LATE_FEE;
    }
}
