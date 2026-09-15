package com.neueda.leap.sprint5;

import java.util.List;

// SRP FIX, part 2 of 2. One job: turn resource holds (plus a pre-calculated total)
// into a late fee report string. It doesn't calculate anything itself, and it doesn't
// know or care how the report gets delivered - that's a third class's job, not shown
// here, deliberately, to keep the point sharp: each of these classes has exactly one
// reason to change.
public class FeeReportFormatter {

    public String format(List<ResourceHold> holds, double total) {
        StringBuilder report = new StringBuilder("Late Fee Report\n");
        for (ResourceHold hold : holds) {
            report.append(hold.getMemberId())
                    .append(": $")
                    .append(String.format("%.2f", hold.calculateLateFee()))
                    .append("\n");
        }
        report.append("Total late fees: $").append(String.format("%.2f", total));
        return report.toString();
    }
}
