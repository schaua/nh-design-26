package com.neueda.leap.sprint5;

// Updated interface for calculating late fees.
//
// An interface is a pure contract: a method signature with no body and no state
// at all (not even a private field) - just a promise that "any class implementing
// this has a calculateFee method." Unlike LibraryResource (an abstract class), a
// Feeable doesn't need to BE a LibraryResource at all - it just needs to be able to
// calculate a fee. That's the problem abstract classes alone can't solve: some
// things that need to calculate a fee might have nothing else in common with a
// LibraryResource, and forcing them into the LibraryResource hierarchy just to
// reuse this one capability would be exactly the kind of bad, reuse-only inheritance
// that creates tight coupling.
//
// A class can extend only ONE other class, but can implement as many interfaces
// as it needs - LibraryResource extends nothing, but implements Feeable.
public interface Feeable {
    // Calculate late fees based on days overdue (or other parameters as needed)
    double calculateFee(double daysOverdue);
}
