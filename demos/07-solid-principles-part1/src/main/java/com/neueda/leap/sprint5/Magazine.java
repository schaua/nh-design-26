package com.neueda.leap.sprint5;

// A Magazine is a library resource with a percentage-based late fee.
// Renamed from EquityInstrument.
public class Magazine extends LibraryResource {

    private static final double DAILY_LATE_FEE = 0.10;

    public Magazine(String resourceId) {
        super(resourceId);
    }

    @Override
    public double calculateFee(double daysOverdue) {
        return DAILY_LATE_FEE * daysOverdue;
    }
}
