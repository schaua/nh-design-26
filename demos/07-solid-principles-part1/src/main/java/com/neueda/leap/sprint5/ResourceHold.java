package com.neueda.leap.sprint5;

// A request to borrow a library resource.
// Renamed from Order.
public class ResourceHold {

    private final String memberId;
    private final LibraryResource resource;
    private final double daysOverdue;

    public ResourceHold(String memberId, LibraryResource resource, double daysOverdue) {
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

    public double getDaysOverdue() {
        return daysOverdue;
    }

    public double calculateLateFee() {
        return resource.calculateFee(daysOverdue);
    }
}
