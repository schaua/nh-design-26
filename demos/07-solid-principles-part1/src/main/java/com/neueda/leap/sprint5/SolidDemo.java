package com.neueda.leap.sprint5;

import java.util.List;

public class SolidDemo {

    public static void main(String[] args) {
        List<ResourceHold> holds = List.of(
                new ResourceHold("M001", new Magazine("MAG-001"), 5),
                new ResourceHold("M002", new Book("BOOK-001"), 3),
                new ResourceHold("M003", new JournalIssue("JOURNAL-001"), 7)
        );

        System.out.println("=== S: Single Responsibility Principle ===");
        System.out.println("-- Before: one class does calculation, formatting, AND delivery --");
        new BadReportGenerator().generateAndSend(holds);

        System.out.println();
        System.out.println("-- After: three separate, single-purpose classes --");
        FeeAggregator aggregator = new FeeAggregator();
        double total = aggregator.totalFees(holds);
        FeeReportFormatter formatter = new FeeReportFormatter();
        String report = formatter.format(holds, total);
        System.out.println(report);

        System.out.println();
        System.out.println("=== O: Open/Closed Principle ===");
        ResourceHold dvdHold = new ResourceHold("M004", new DVD("DVD-001"), 2);
        System.out.println("New resource type, zero changes to LibraryResource, Feeable, ResourceHold,");
        System.out.println("FeeAggregator, or FeeReportFormatter:");
        System.out.println("DVD late fee for 2 days overdue: $" + String.format("%.2f", dvdHold.calculateLateFee()));

        System.out.println();
        System.out.println("=== L: Liskov Substitution Principle ===");
        Loan realLoan = new Loan(100);
        System.out.println("-- Before: FrozenLoanRequestBad extends Loan, but breaks its contract --");
        Loan disguisedFrozenLoan = new FrozenLoanRequestBad(100);
        try {
            adjustAll(List.of(realLoan, disguisedFrozenLoan), 10);
            System.out.println("adjustAll succeeded for every Loan in the list");
        } catch (UnsupportedOperationException e) {
            System.out.println("adjustAll CRASHED on a value that's still typed as Loan: " + e.getMessage());
        }

        System.out.println();
        System.out.println("-- After: FrozenLoanRequest doesn't extend Loan, so it can't be");
        System.out.println("   smuggled into code that expects a genuine, adjustable Loan --");
        FrozenLoanRequest properlyFrozen = new FrozenLoanRequest(new Loan(100));
        adjustAll(List.of(realLoan), 10);
        System.out.println("adjustAll succeeded — only real Loans were ever in the list");
        System.out.println("Frozen quantity, read-only: " + properlyFrozen.getQuantity());
    }

    // Generic code written against Loan. This is exactly the kind of code that
    // silently breaks when a subtype doesn't honour its supertype's contract.
    private static void adjustAll(List<Loan> loans, double delta) {
        for (Loan loan : loans) {
            loan.adjust(delta);
        }
    }
}
