package com.neueda.leap.sprint5;

// A DVD is a library resource with a higher daily late fee rate.
// Renamed from DerivativeInstrument.
public class DVD extends LibraryResource {

    private static final double DAILY_LATE_FEE = 2.00;

    public DVD(String resourceId) {
        super(resourceId);
    }

    @Override
    public double calculateFee(double daysOverdue) {
        return DAILY_LATE_FEE * daysOverdue;
    }
}
