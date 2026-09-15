package com.neueda.leap.sprint5;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class SettlementSummary {

    public String summarize(List<ResourceHold> loans) {
        Map<String, Double> loansByMember = groupLoansByMember(loans);
        return format(loansByMember);
    }

    // One job: group loans by member and calculate total late fees per member.
    // Nothing about formatting lives here.
    private Map<String, Double> groupLoansByMember(List<ResourceHold> loans) {
        Map<String, Double> memberFees = new LinkedHashMap<>();
        for (ResourceHold loan : loans) {
            String memberId = loan.getMemberId();
            double lateFee = loan.calculateLateFee();
            memberFees.put(memberId, memberFees.getOrDefault(memberId, 0.0) + lateFee);
        }
        return memberFees;
    }

    // One job: turn the member fees into the report string. No calculation happens
    // here - by the time this method runs, every number it needs already exists.
    private String format(Map<String, Double> memberFees) {
        if (memberFees.isEmpty()) {
            return "Library Status: No overdue loans";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== Library Loan Status ===\n");
        
        double totalLateFees = 0;
        for (Map.Entry<String, Double> entry : memberFees.entrySet()) {
            String memberId = entry.getKey();
            double fees = entry.getValue();
            totalLateFees += fees;
            report.append("Member ").append(memberId).append(": $").append(String.format("%.2f", fees)).append(" in late fees\n");
        }
        
        report.append("---\n");
        report.append("Total late fees owed: $").append(String.format("%.2f", totalLateFees));
        
        return report.toString();
    }
}
