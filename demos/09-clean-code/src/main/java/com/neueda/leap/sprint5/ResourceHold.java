package com.neueda.leap.sprint5;

public class ResourceHold {

    private final String memberId;
    private final LibraryResource resource;
    private final int daysOverdue;

    public ResourceHold(String memberId, LibraryResource resource, int daysOverdue) {
        this.memberId = memberId;
        this.resource = resource;
        this.daysOverdue = daysOverdue;
    }

    public String getMemberId() {
        return memberId;
    }

    public LibraryResource getResource() {
        return resource;
    }

    public int getDaysOverdue() {
        return daysOverdue;
    }

    public double calculateLateFee() {
        return resource.lateFeeCalculations(daysOverdue);
    }
}
