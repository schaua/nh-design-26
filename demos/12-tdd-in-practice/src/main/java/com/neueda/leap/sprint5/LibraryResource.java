package com.neueda.leap.sprint5;

// LibraryResource is an abstract base class for all library resources (books, magazines, DVDs).
// It implements the Feeable interface, which means every library resource is capable of
// calculating late fees when returned after their due date.
//
// "abstract" means: this class can never be instantiated directly (new LibraryResource("ISBN123")
// is a compile error) - it exists only to be extended. And because calculateLateFee() has NO
// body here, every subclass is REQUIRED to supply its own, or that subclass must also be
// declared abstract. A class that forgets to override it simply won't compile any more.
//
// Each subclass (Book, Magazine, DVD) owns its own late fee calculation rule.
public abstract class LibraryResource implements Feeable {

    private final String id;

    protected LibraryResource(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // No body - subclasses MUST provide one. Each resource type has its own fee structure.
    @Override
    public abstract double calculateLateFee(int daysOverdue);
}
