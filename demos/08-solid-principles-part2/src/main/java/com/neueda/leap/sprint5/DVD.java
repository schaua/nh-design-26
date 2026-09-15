package com.neueda.leap.sprint5;

// DVDs: a flat $2 late fee, regardless of loan duration - a genuinely different fee
// structure to Magazine's percentage-based one. Each subclass owns its own
// rule; nothing about that rule lives in the shared LibraryResource base.
public class DVD extends LibraryResource {

    private static final double FLAT_FEE = 2.00;

    public DVD(String resourceId) {
        super(resourceId);
    }

    @Override
    public double calculateFee(double loanDuration) {
        return FLAT_FEE;
    }
}
