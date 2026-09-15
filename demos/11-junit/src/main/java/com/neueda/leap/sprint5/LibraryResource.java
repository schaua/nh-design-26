package com.neueda.leap.sprint5;

// LibraryResource is now ABSTRACT, and implements the Feeable interface
// (see Feeable.java).
//
// "abstract" means: this class can never be instantiated directly (new
// LibraryResource("Dune") is a compile error) - it exists only to be extended.
// And because calculateFee() has NO body here, every subclass is REQUIRED to
// supply its own, or that subclass must also be declared abstract. A class that
// forgets to override it simply won't compile any more - enforced by the compiler.
public abstract class LibraryResource implements Feeable {

    private final String title;

    protected LibraryResource(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    // No body - subclasses MUST provide one. Each resource type has its own
    // late fee calculation rules.
    @Override
    public abstract double calculateFee(double daysOverdue);
}
