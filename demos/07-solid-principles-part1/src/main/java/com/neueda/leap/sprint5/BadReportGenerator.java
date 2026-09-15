package com.neueda.leap.sprint5;

import java.util.List;

// SRP VIOLATION - this is Module 6's flawed-design.mmd, in code. One class does
// THREE unrelated things: sums the late fees (a calculation), formats them as a
// report (a presentation concern), and "sends" the report (an I/O concern). Three
// different reasons for this class to change: the fee calculation rule changes,
// the report format changes, or the delivery mechanism changes - and any one of
// those changes risks breaking the other two, because they all live in one place.
public class BadReportGenerator {

    public void generateAndSend(List<ResourceHold> holds) {
        // Responsibility 1: calculation
        double total = 0;
        for (ResourceHold hold : holds) {
            total += hold.calculateLateFee();
        }

        // Responsibility 2: formatting/presentation
        StringBuilder report = new StringBuilder("Late Fee Report\n");
        for (ResourceHold hold : holds) {
            report.append(hold.getMemberId())
                    .append(": $")
                    .append(String.format("%.2f", hold.calculateLateFee()))
                    .append("\n");
        }
        report.append("Total late fees: $").append(String.format("%.2f", total));

        // Responsibility 3: delivery/I-O
        System.out.println("--- Emailing late fee report ---");
        System.out.println(report);
    }
}
