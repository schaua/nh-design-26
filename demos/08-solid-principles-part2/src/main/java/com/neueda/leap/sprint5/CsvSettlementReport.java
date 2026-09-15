package com.neueda.leap.sprint5;

import java.util.List;

// Implements exactly what it needs, nothing else. No stubbed, exception-throwing
// methods anywhere - there's nothing to stub, because CsvReportable never asked
// for a toConsole() or toPdf() in the first place.
public class CsvSettlementReport implements CsvReportable {

    @Override
    public String toCsv(List<ResourceHold> holds) {
        StringBuilder csv = new StringBuilder("memberId,fee\n");
        for (ResourceHold hold : holds) {
            csv.append(hold.getMemberId()).append(",").append(hold.calculateLateFee()).append("\n");
        }
        return csv.toString();
    }
}
