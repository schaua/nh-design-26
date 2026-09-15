package com.neueda.leap.sprint5;

// Magazines: a flat 0.1% late fee based on loan duration.
public class Magazine extends LibraryResource {

    private static final double FEE_RATE = 0.001;

    public Magazine(String resourceId) {
        super(resourceId);
    }

    @Override
    public double calculateFee(double loanDuration) {
        return loanDuration * FEE_RATE;
    }
}
