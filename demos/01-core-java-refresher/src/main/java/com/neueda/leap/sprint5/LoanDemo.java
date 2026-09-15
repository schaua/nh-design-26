package com.neueda.leap.sprint5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoanDemo {

    public static void main(String[] args) {

        // --- Part 1: types and control flow ---
        // Python: loanId = "L0001"              (dynamically typed, inferred at runtime)
        // Java:   String loanId = "L0001";      (statically typed, checked at compile time)
        String loanId = "L0001";   // Python str  -> Java String
        int daysCheckedOut = 7;    // Python int has no width limit; Java int is 32-bit,
                                   // long is 64-bit if you need a bigger range
        boolean isOverdue = false; // Python True/False -> Java true/false, lowercase

        System.out.println(loanId + ": daysCheckedOut=" + daysCheckedOut + " isOverdue=" + isOverdue);
        System.out.println();

        // if/else: same idea as Python, but braces instead of indentation, and the
        // condition needs parentheses
        double overdueCharge = 2.50;
        String feeLevel;
        if (overdueCharge > 2.00) {
            feeLevel = "HIGH";
        } else {
            feeLevel = "LOW";
        }
        System.out.println("Fee level: " + feeLevel);

        // --- Part 2: the collections framework ---
        // Python list  -> Java List (typically ArrayList)
        // Python dict  -> Java Map (typically HashMap)
        // Python set   -> Java Set (typically HashSet)
        // Java's collections are all GENERIC: List<Loan> means "a list that only
        // ever holds Loan objects" - the compiler enforces this, Python's list can
        // silently mix types.
        List<Loan> loans = new ArrayList<>();
        loans.add(new Loan("L0001", "Alice Chen", "The Great Gatsby", "F. Scott Fitzgerald", 5, "ACTIVE"));
        loans.add(new Loan("L0002", "Ben Whitfield", "To Kill a Mockingbird", "Harper Lee", 12, "ACTIVE"));
        loans.add(new Loan("L0003", "Alice Chen", "1984", "George Orwell", 20, "OVERDUE"));

        // for-each: same idea as Python's "for loan in loans:"
        int totalDaysLoaned = 0;
        for (Loan loan : loans) {
            totalDaysLoaned += loan.getDaysCheckedOut();
        }
        // loans.forEach(loan -> totalDaysLoaned += loan.getDaysCheckedOut());

        System.out.println("Total days loaned: " + totalDaysLoaned);

        // Building a summary Map, the same shape as Module 3's Python dict-accumulation
        // pattern (totals.get(key, 0) + value), just with Java's Map API instead
        Map<String, Integer> loansByBorrower = new HashMap<>();
        for (Loan loan : loans) {
            String key = loan.getBorrowerName();
            int existing = loansByBorrower.getOrDefault(key, 0);
            loansByBorrower.put(key, existing + 1);
        }
        // loans.forEach(loan -> {
        //     String key = loan.getBorrowerName();
        //     int existing = loansByBorrower.getOrDefault(key, 0);
        //     loansByBorrower.put(key, existing + 1);
        // });
        System.out.println("Loans by borrower: " + loansByBorrower);

        // Set: same idea as Python's set() for distinct values
        Set<String> distinctBorrowers = new HashSet<>();
        for (Loan loan : loans) {
            distinctBorrowers.add(loan.getBorrowerName());
        }
        
        // loans.forEach(loan -> distinctBorrowers.add(loan.getBorrowerName()));

        System.out.println("Distinct borrowers: " + distinctBorrowers);

        // --- Part 3: checked vs unchecked exceptions ---

        // Unchecked (RuntimeException): the compiler does NOT force you to handle
        // this. Java throws it, and if nothing catches it, the program crashes -
        // the same behaviour as an uncaught Python exception.
        try {
            double parsed = Double.parseDouble("not-a-number");
            System.out.println("Parsed value: " + parsed);
        } catch (NumberFormatException e) {
            System.out.println("Caught unchecked exception: " + e.getMessage());
        }

        // Checked (extends Exception, not RuntimeException): the compiler REQUIRES
        // every caller to either catch it or declare "throws" - this class of
        // exception simply doesn't exist in Python, where every exception is
        // effectively "unchecked" from the compiler's point of view.
        try {
            Loan badLoan = parseLoanLine("L0099,Unknown,???,Unknown,-5,ACTIVE");
            System.out.println("Parsed loan: " + badLoan);
        } catch (MalformedLoanException e) {
            System.out.println("Caught checked exception: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Caught generic exception: " + e.getMessage());
        }
    }


    // "throws MalformedLoanException" in the method signature is what makes this
    // a checked exception - every caller must acknowledge it, at compile time.
    private static Loan parseLoanLine(String line) throws MalformedLoanException {
        String[] parts = line.split(",");
        int daysCheckedOut = Integer.parseInt(parts[4]);
        if (daysCheckedOut <= 0) {
            throw new MalformedLoanException("daysCheckedOut must be positive, got " + daysCheckedOut);
        }
        return new Loan(parts[0], parts[1], parts[2], parts[3], daysCheckedOut, parts[5]);
    }
}
