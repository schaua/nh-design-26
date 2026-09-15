package com.neueda.leap.sprint5;

// DVDs have a flat $2 late fee per day, regardless of how many days overdue
// - a genuinely different fee structure than Book's flat $5 or Magazine's
// percentage-based one. Each resource type owns its own rule; nothing about
// that rule lives in the shared LibraryResource base.
public class DVD extends LibraryResource {

    private static final double FEE_PER_DAY = 2.00;

    public DVD(String title) {
        super(title);
    }

    @Override
    public double calculateFee(double daysOverdue) {
        return daysOverdue * FEE_PER_DAY;
    }
}
