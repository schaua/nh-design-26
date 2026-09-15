package com.neueda.leap.sprint5;

// Books have a flat late fee of $5.00 regardless of how many days overdue.
public class Book extends LibraryResource {

    private static final double FLAT_FEE = 5.00;

    public Book(String title) {
        super(title);
    }

    @Override
    public double calculateFee(double daysOverdue) {
        return FLAT_FEE;
    }
}
