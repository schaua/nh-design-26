package com.neueda.leap.sprint5;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

// CLEAN CODE VIOLATIONS, deliberately, for the demo:
// - single-letter/meaningless variable names (l, m, x, mf)
// - deep nesting instead of early returns
// - one long method doing several unrelated jobs at once (grouping and formatting)
// - a comment explaining WHAT the code does (the code already says that) instead
//   of WHY, which is the only kind of comment worth writing
public class MessySettlementSummary {

    public String s(List<ResourceHold> l) {
        Map<String, Double> mf = new HashMap<>();
        // loop through the loans and group by member
        for (int i = 0; i < l.size(); i++) {
            ResourceHold m = l.get(i);
            double x = m.calculateLateFee();
            if (mf.containsKey(m.getMemberId())) {
                mf.put(m.getMemberId(), mf.get(m.getMemberId()) + x);
            } else {
                mf.put(m.getMemberId(), x);
            }
        }
        String r = "";
        r = r + "=== Library Loan Status ===\n";
        double t = 0;
        for (Map.Entry<String, Double> e : mf.entrySet()) {
            r = r + "Member " + e.getKey() + ": $" + String.format("%.2f", e.getValue()) + " in late fees\n";
            t = t + e.getValue();
        }
        r = r + "---\n";
        r = r + "Total late fees owed: $" + String.format("%.2f", t);
        return r;
    }
}

