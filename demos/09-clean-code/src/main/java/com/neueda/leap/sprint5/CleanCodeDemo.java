package com.neueda.leap.sprint5;

import java.util.List;

public class CleanCodeDemo {

    public static void main(String[] args) {
        List<ResourceHold> loans = List.of(
                new ResourceHold("M001", new Book("1984"), 5),
                new ResourceHold("M002", new Magazine("Time-2024", 8.99), 3),
                new ResourceHold("M001", new DVD("Avatar"), 7),
                new ResourceHold("M003", new Magazine("Vogue-2024", 6.99), 2)
        );

        System.out.println("=== Messy version ===");
        System.out.println(new MessySettlementSummary().s(loans));

        System.out.println();
        System.out.println("=== Clean version - identical output ===");
        System.out.println(new SettlementSummary().summarize(loans));
    }
}
