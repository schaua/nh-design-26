package com.neueda.leap.sprint5;

public class LoanRequest {

    private final String patronId;
    private final LibraryResource resource;
    private final double daysOverdue;

    public LoanRequest(String patronId, LibraryResource resource, double daysOverdue) {
        this.patronId = patronId;
        this.resource = resource;
        this.daysOverdue = daysOverdue;
    }

    public String getPatronId() {
        return patronId;
    }

    public LibraryResource getResource() {
        return resource;
    }

    public double getDaysOverdue() {
        return daysOverdue;
    }

    public double calculateFee() {
        return resource.calculateFee(daysOverdue);
    }
}
