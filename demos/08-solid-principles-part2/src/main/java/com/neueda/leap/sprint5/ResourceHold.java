package com.neueda.leap.sprint5;

public class ResourceHold {

    private final String memberId;
    private final LibraryResource resource;
    private final double loanDuration;

    public ResourceHold(String memberId, LibraryResource resource, double loanDuration) {
        this.memberId = memberId;
        this.resource = resource;
        this.loanDuration = loanDuration;
    }

    public String getMemberId() {
        return memberId;
    }

    public LibraryResource getResource() {
        return resource;
    }

    public double getLoanDuration() {
        return loanDuration;
    }

    public double calculateLateFee() {
        return resource.calculateFee(loanDuration);
    }
}
