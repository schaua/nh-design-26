package com.neueda.leap.sprint5;

// DIP FIX. LoanManager now depends on the ReportWriter ABSTRACTION, supplied
// through its constructor, not on any specific implementation it builds itself.
// The high-level policy (calculate a fee, report it) no longer knows or cares
// whether that report ends up on the console, in memory, or somewhere not
// invented yet - that decision is made once, at the point where a LoanManager
// is constructed, not baked into the class.
public class LoanManager {

    private final ReportWriter writer;

    public LoanManager(ReportWriter writer) {
        this.writer = writer;
    }

    public double processLoan(LoanRequest loanRequest) {
        double fee = loanRequest.calculateFee();
        writer.write(loanRequest.getPatronId() + ": $" + fee);
        return fee;
    }
}
