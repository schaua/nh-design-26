package com.neueda.leap.sprint5;

import java.util.List;

// SRP FIX, part 1 of 2. One job: add up late fees. Nothing about formatting, nothing
// about delivery. If the calculation rule changes, this is the only class that
// needs to change.
public class FeeAggregator {

    public double totalFees(List<ResourceHold> holds) {
        double total = 0;
        for (ResourceHold hold : holds) {
            total += hold.calculateLateFee();
        }
        return total;
    }
}
