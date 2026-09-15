package com.neueda.leap.sprint5;

// Step 2 of the story: the fix for ConcreteInheritanceProblem.java. LibraryResource is
// now ABSTRACT, and implements the Feeable interface (see Feeable.java).
//
// "abstract" means: this class can never be instantiated directly (new
// LibraryResource("1984") is a compile error) - it exists only to be extended. And
// because lateFeeCalculations() has NO body here, every subclass is REQUIRED to supply
// its own, or that subclass must also be declared abstract. A class that forgets
// to override it, like BookBuggy did, simply won't compile any more -
// the exact bug from Step 1 is now impossible, enforced by the compiler, not by
// someone remembering.
public abstract class LibraryResource implements Feeable {

    private final String resourceId;

    protected LibraryResource(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    // No body - subclasses MUST provide one. Compare to ConcreteResource's
    // version, which had a real (and dangerously reusable) default.
    @Override
    public abstract double lateFeeCalculations(int daysOverdue);
}
