package com.neueda.leap.sprint5;

// LSP FIX. FrozenLoanRequest does NOT extend Loan - it HOLDS one (composition,
// exactly like Module 4's Portfolio/Loan relationship). It exposes only
// getQuantity(), and deliberately has no adjust() method at all: there is no
// promise to break, because it never claimed to be a Loan in the first place.
// Any code that specifically wants to work with frozen loan requests has to say so
// explicitly (by using the FrozenLoanRequest type), rather than discovering the
// restriction at runtime via a thrown exception.
// Renamed from FrozenHolding.
public class FrozenLoanRequest {

    private final Loan loan;

    public FrozenLoanRequest(Loan loan) {
        this.loan = loan;
    }

    public double getQuantity() {
        return loan.getQuantity();
    }
}
