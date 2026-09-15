package com.neueda.leap.sprint5;

// This is the END STATE of a live TDD session built from the library requirement
// which states: "A library member can borrow up to five books at a time... A member 
// with more than £5 of unpaid fees cannot borrow further books until the fees are paid."
// See demo-guide.md for the five red-green-refactor cycles that actually built it.
public class LoanValidator {

    private static final int MAX_LOANS = 5;
    private static final double MAX_OUTSTANDING_FEES = 5.00;

    public OutstandingFees validate(LoanRequest request) {
        if (request.getCurrentLoansCount() >= MAX_LOANS) {
            return OutstandingFees.rejected(
                    "member has reached the maximum of " + MAX_LOANS + " loans",
                    request.getOutstandingFees());
        }
        if (request.getOutstandingFees() > MAX_OUTSTANDING_FEES) {
            return OutstandingFees.rejected(
                    "member has unpaid fees of £" + String.format("%.2f", request.getOutstandingFees()) +
                    ", which exceeds the £" + MAX_OUTSTANDING_FEES + " limit",
                    request.getOutstandingFees());
        }
        return OutstandingFees.allowed(request.getOutstandingFees());
    }
}
