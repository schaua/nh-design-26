package com.neueda.leap.sprint5;

import java.util.List;

// SRP FIX, part 1 of 2. One job: add up late fees. Nothing about formatting, nothing
// about delivery. If the calculation rule changes, this is the only class that
// needs to change.
public class FeeAggregator {

    // BAD: checks the type of each hold and adds up fees accordingly.
    // Violates open/closed principle.  Additional types  would require
    // modifying this code
    public double badTotalFess(List<ResourceHold> holds) {
        double total = 0;
        for (ResourceHold hold : holds) {
            if (hold.getClass().getName().equals("com.neueda.leap.sprint5.Book"))
                total += hold.calculateLateFee();
            else if (hold.getClass().getName().equals("com.neueda.leap.sprint5.DVD"))
                total += hold.calculateLateFee();
            else if (hold.getClass().getName().equals("com.neueda.leap.sprint5.Magazine"))
                total += hold.calculateLateFee();
        }
        return total;
    }
    // Open/Closed FIX: new resource 
    // types can be added without modifying this code.
    public double totalFees(List<ResourceHold> holds) {
        double total = 0;
        for (ResourceHold hold : holds) {
            total += hold.calculateLateFee();
        }
        return total;
    }
}
