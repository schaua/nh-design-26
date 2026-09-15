package com.neueda.leap.sprint5;

// Abstract base class for all library resources.
// Renamed from Instrument. Now deals with late fees on loans instead of trading fees.
public abstract class LibraryResource implements Feeable {

    private final String resourceId;

    protected LibraryResource(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    // No body - subclasses MUST provide one. Calculates late fees based on days overdue.
    @Override
    public abstract double calculateFee(double daysOverdue);
}
