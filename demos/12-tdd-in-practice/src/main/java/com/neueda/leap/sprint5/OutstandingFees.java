package com.neueda.leap.sprint5;

// OutstandingFees is a small value type, built via TDD alongside LoanValidator itself
// (see demo-guide.md). The library requirement says a rejected loan needs a REASON, 
// not just a boolean - this exists to carry that. It also tracks the member's total
// outstanding fees so that the LoanValidator can make decisions based on fee amounts.
public class OutstandingFees {

    private final boolean canBorrow;
    private final String reason;
    private final double totalFees;

    private OutstandingFees(boolean canBorrow, String reason, double totalFees) {
        this.canBorrow = canBorrow;
        this.reason = reason;
        this.totalFees = totalFees;
    }

    public static OutstandingFees allowed(double totalFees) {
        return new OutstandingFees(true, null, totalFees);
    }

    public static OutstandingFees rejected(String reason, double totalFees) {
        return new OutstandingFees(false, reason, totalFees);
    }

    public boolean canBorrow() {
        return canBorrow;
    }

    public String getReason() {
        return reason;
    }

    public double getTotalFees() {
        return totalFees;
    }
}
