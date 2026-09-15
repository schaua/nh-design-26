package com.neueda.leap.sprint5;

// Magazines: a flat 0.1% late fee per day overdue (calculated as a percentage
// of a standard magazine value).
public class Magazine extends LibraryResource {

    private static final double FEE_RATE = 0.001;

    public Magazine(String title) {
        super(title);
    }

    @Override
    public double calculateFee(double daysOverdue) {
        return daysOverdue * FEE_RATE;
    }
}
