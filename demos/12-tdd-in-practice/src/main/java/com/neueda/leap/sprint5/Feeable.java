package com.neueda.leap.sprint5;

// An interface is a pure contract: a method signature with no body and no state
// at all (not even a private field) - just a promise that "any class implementing
// this has a calculateLateFee method." 
//
// For library resources, we calculate late fees based on days overdue and a daily rate
// specific to the resource type. For example, Books charge 20p per day overdue.
//
// A class can extend only ONE other class, but can implement as many interfaces
// as it needs - LibraryResource extends nothing, but implements Feeable.
public interface Feeable {
    double calculateLateFee(int daysOverdue);
}
