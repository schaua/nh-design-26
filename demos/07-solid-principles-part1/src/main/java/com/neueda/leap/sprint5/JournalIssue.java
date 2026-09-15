package com.neueda.leap.sprint5;

// A JournalIssue is a library resource with a flat daily late fee.
// Renamed from FundInstrument.
public class JournalIssue extends LibraryResource {

    private static final double DAILY_LATE_FEE = 0.25;

    public JournalIssue(String resourceId) {
        super(resourceId);
    }

    @Override
    public double calculateFee(double daysOverdue) {
        return DAILY_LATE_FEE * daysOverdue;
    }
}
