package com.neueda.leap.sprint5;

// Magazines: a flat 0.1% daily late fee on the purchase value.
public class Magazine extends LibraryResource {

    private static final double FEE_RATE = 0.001;
    private final double purchaseValue;

    public Magazine(String resourceId, double purchaseValue) {
        super(resourceId);
        this.purchaseValue = purchaseValue;
    }

    public double getPurchaseValue() {
        return purchaseValue;
    }

    @Override
    public double lateFeeCalculations(int daysOverdue) {
        return purchaseValue * daysOverdue * FEE_RATE;
    }
}
