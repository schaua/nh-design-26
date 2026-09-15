package com.neueda.leap.sprint5;

// LSP VIOLATION. A frozen loan request (e.g. under regulatory hold) can't be adjusted -
// that's a genuine business rule. But expressing it by EXTENDING Loan and
// overriding adjust() to always throw breaks the promise Loan makes to every
// piece of code that already depends on it: "you can call adjust() on any
// Loan, and it will either succeed or throw only if the specific delta would
// go negative." A FrozenLoanRequestBad breaks that promise unconditionally - any
// generic code written against Loan (see SolidDemo's adjustAll helper) that
// works fine for a real Loan will crash the moment it's handed one of these.
// That's Liskov Substitution, violated: a subtype should be usable anywhere its
// supertype is expected, without surprising the caller.
// Renamed from FrozenHoldingBad.
public class FrozenLoanRequestBad extends Loan {

    public FrozenLoanRequestBad(double quantity) {
        super(quantity);
    }

    @Override
    public void adjust(double delta) {
        throw new UnsupportedOperationException("loan request is frozen");
    }
}
