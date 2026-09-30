package com.neueda.leap.sprint7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// The fix: every row is explicitly validated against named rules. Rows that
// fail are QUARANTINED - kept, counted, and labelled with why - not thrown
// away.

// The update: have validate return a list of all of the reasons for failure
// This allows us to report multiple issues per row instead of stopping at the first one.
public class QuarantineLoader {

    record QuarantinedRow(int lineNumber, String rawLine, String reason) {}

    public static void main(String[] args) throws Exception {
        List<String[]> valid = new ArrayList<>();
        List<String> reasons = new ArrayList<>();
        List<QuarantinedRow> quarantined = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(QuarantineLoader.class.getClassLoader().getResourceAsStream("trades.csv"), StandardCharsets.UTF_8))) {
            br.readLine(); // header
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] cols = line.split(",", -1);
                reasons.clear();
                reasons = validate(cols);
                if (reasons.isEmpty()) {
                    valid.add(cols);
                } else {
                    quarantined.add(new QuarantinedRow(lineNumber, line, String.join("; ", reasons)));
                }
            }
        }

        System.out.println("=== Data Quality Report ===");
        System.out.println("Valid rows:       " + valid.size());
        System.out.println("Quarantined rows: " + quarantined.size());
        System.out.println();
        for (QuarantinedRow q : quarantined) {
            System.out.printf("  line %d: %-40s -> %s%n", q.lineNumber(), q.rawLine(), q.reason());
        }
    }

    // Populates the reasons list with all the issues found in the row.
    static List<String> validate(String[] cols) {
        List<String> reasons = new ArrayList<>();
        
        if (cols.length < 4) {
            reasons.add("wrong number of columns");
        }
        String accountId = cols[0];
        String ticker = cols[1];
        String quantityStr = cols[2];
        String priceStr = cols[3];

        if (accountId.isBlank()) reasons.add("missing account_id");
        if (ticker.isBlank()) reasons.add("missing ticker");

        Double quantity;
        try {
            quantity = Double.parseDouble(quantityStr);
        } catch (NumberFormatException e) {
            quantity = Double.NaN;
            reasons.add("quantity is not a number: '" + quantityStr + "'");
        }
        if (quantity <= 0) reasons.add("quantity must be positive, was " + quantity);

        Double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            price = Double.NaN;
            reasons.add("price is not a number: '" + priceStr + "'");
        }
        if (price <= 0) reasons.add("price must be positive, was " + price);

        return reasons;
    }
}
