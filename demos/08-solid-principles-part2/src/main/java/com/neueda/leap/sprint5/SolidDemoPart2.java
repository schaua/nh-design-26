package com.neueda.leap.sprint5;

import java.util.List;

public class SolidDemoPart2 {

    public static void main(String[] args) {
        List<ResourceHold> holds = List.of(
                new ResourceHold("M001", new Magazine("MAG-001"), 10000),
                new ResourceHold("M002", new Book("BOOK-001"), 8000),
                new ResourceHold("M003", new DVD("DVD-001"), 5000)
        );

        System.out.println("=== I: Interface Segregation Principle ===");
        System.out.println("-- Before: one fat interface forces every implementer to support --");
        System.out.println("   console, CSV, AND PDF output, whether they need it or not --");
        CsvOnlyReportBad badReport = new CsvOnlyReportBad();
        System.out.println(badReport.toCsv(holds));
        try {
            badReport.toPdf(holds);
        } catch (UnsupportedOperationException e) {
            System.out.println("Calling toPdf() on a \"full\" FatReportable CRASHED: " + e.getMessage());
        }

        System.out.println();
        System.out.println("-- After: segregated interfaces - implement only what you need --");
        CsvSettlementReport goodReport = new CsvSettlementReport();
        System.out.println(goodReport.toCsv(holds));
        System.out.println("(CsvSettlementReport has no toPdf() to accidentally call - the");
        System.out.println(" compiler removes the possibility entirely, not just at runtime)");

        System.out.println();
        System.out.println("=== D: Dependency Inversion Principle ===");
        System.out.println("-- Before: OrderExecutor builds its own ConsoleReportWriter --");
        BadOrderExecutor badExecutor = new BadOrderExecutor();
        double badFee = badExecutor.execute(holds.get(0));
        System.out.println("Fee calculated: $" + badFee + " (but you can only ever get console output)");

        System.out.println();
        System.out.println("-- After: OrderExecutor depends on the ReportWriter abstraction --");
        InMemoryReportWriter testWriter = new InMemoryReportWriter();
        OrderExecutor goodExecutor = new OrderExecutor(testWriter);
        double goodFee = goodExecutor.execute(holds.get(0));
        System.out.println("Fee calculated: $" + goodFee + ", captured for testing: " + testWriter.getLines());
        System.out.println("Swap in a ConsoleReportWriter instead, and the SAME OrderExecutor");
        System.out.println("class prints to the console - zero changes to OrderExecutor itself:");
        OrderExecutor consoleExecutor = new OrderExecutor(new ConsoleReportWriter());
        consoleExecutor.execute(holds.get(1));
    }
}
