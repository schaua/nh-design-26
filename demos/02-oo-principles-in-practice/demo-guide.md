# Demo: Module 2 — Object-Oriented Principles in Practice

**Duration:** 27 minutes
**Files:** `LibraryDemo.java`, `Book.java`, `Magazine.java`, `Dvd.java`, `LibraryResource.java`,
`Loanable.java`, `Library.java`

Every code example referenced in this guide is shown **before** it's discussed, in the same
order `LibraryDemo.java` runs them — nothing here refers to code the room hasn't seen yet.

## Part 0: Class vs. object (4 min)

Show `Book` (a library resource) and the first lines of `LibraryDemo.java`:

```java
LibraryResource book = new Book("B001", "Effective Java", "Joshua Bloch", 416);
LibraryResource magazine = new Magazine("M001", "Java Monthly", 42);
```

Narration: `Book` (the `.java` file, the `class` keyword) is a **blueprint** — it has no data
of its own. Each `new Book(...)` call builds a separate **object**: a real, independent thing in
memory, with its own copy of every field. Run a check showing the difference:

```java
System.out.println("Same class? " + (book.getClass() == magazine.getClass())); // false
System.out.println("Same object? " + (book == magazine));                       // false
```

Narration: different *classes*, two different *objects* — a `Book` and a `Magazine`. Changing
one book's title would never affect the magazine. Python equivalent: `class Book: ...`,
`class Magazine: ...`, then `b = Book(...)`, `m = Magazine(...)` — identical idea, Java just
always requires the explicit `new`.

## Part 1: Encapsulation — the simplest idea today (5 min)

Deliberately covered first: one object, protecting its own data — no inheritance, no interfaces,
nothing else involved yet.

Show `LibraryResource.java`. Narration: `loaned` (and its related fields `borrowerId` and
`dueDate`) are `private` for a specific reason — this class has invariants that need protecting.
If `loaned` were a public field, *every* piece of code anywhere that touches a `LibraryResource`
would individually have to remember the rules: when loaning, borrowerId and dueDate must be set;
when returning, they must be cleared; you can't loan what's already loaned. One careless line,
anywhere, breaks these invariants silently.

Run the demo's `loanTo()` and `returnItem()` calls — a valid loan, then show what happens if you
try to loan something already loaned or return something not currently loaned. These methods are
the *only* way to change the loan state, and they enforce the rules every single time, in
exactly one place.

## Part 2: A straightforward example of inheritance working (5 min)

Now introduce inheritance, starting with a clean example — nothing wrong with it yet. Show
`LibraryResource` (an abstract base class) and its concrete subclass `Book`:

```java
public abstract class LibraryResource {
    private final String id;
    private String title;
    private boolean loaned;
    // ...
    protected abstract int getLoanPeriod();  // no body — subclass must provide it
    public abstract String getResourceType();
}

public class Book extends LibraryResource {
    private final String author;
    private final int pageCount;
    
    @Override
    protected int getLoanPeriod() {
        return 21;  // deliberately different loan period for books
    }
    
    @Override
    public String getResourceType() {
        return "Book";
    }
}
```

Run it, showing a `Book` being created and loaned:

```
Book: Effective Java [B001]
```

Narration, pointing at two things happening at once: `getId()`, `getTitle()`, `loanTo()` were
never written anywhere in `Book` — they're **inherited** unchanged from `LibraryResource`,
`extends` gives them to this class for free. `getLoanPeriod()` and `getResourceType()`, on the
other hand, are **overridden** — this subclass supplies its own version, on purpose, because a
book has different lending rules than a DVD or magazine. This is the basic mechanism working
exactly as intended: inherit what you don't need to change, override what you do.

## Part 3: The same mechanism — but a subclass that *doesn't* override (4 min)

Show `Magazine extends LibraryResource` and `Dvd extends LibraryResource`. Both are concrete
classes that override the abstract methods. Now suppose someone creates a broken version of
`Dvd` that forgets to override `getLoanPeriod()`:

```java
public class DvdBuggy extends LibraryResource {
    private final int durationMinutes;
    
    // FORGOT TO OVERRIDE getLoanPeriod() 
    // So it inherits the abstract method... but wait, it can't!
    
    @Override
    public String getResourceType() {
        return "DVD";
    }
}
```

Narration: this code won't compile. That's the point. In an abstract parent, leaving an abstract
method unoverridden isn't just invisible — it's a compile error. But imagine if `LibraryResource`
weren't abstract and `getLoanPeriod()` had a real body — say, it returned 14 days (some default).
Then a `DvdBuggy` that forgot to override would silently inherit that wrong behavior, charging
the wrong late fee or enforcing the wrong loan period. DVDs should charge a flat fee, not a
percentage — whoever wrote the buggy subclass forgot to override `getLoanPeriod()`, and the
compiler said nothing, because the inherited method is a real, callable one, exactly like
`Book`'s inherited `getId()` was fine to leave alone. The difference is that here, leaving it
alone was wrong. This bug is invisible until someone notices a DVD was loaned for 14 days
instead of 5, possibly in production.

## Part 4: The fix — an abstract class (5 min)

Show `LibraryResource.java`: `public abstract class LibraryResource`, with `getLoanPeriod()` and
`getResourceType()` having **no body at all**. Narration: `abstract` means two things — the
class itself can never be instantiated directly (`new LibraryResource("R001")` is a compile
error), and any subclass that doesn't override these abstract methods **will not compile**. Show
`Book.java` — providing real implementations — and narrate: the Part 3 bug is now a compiler
error, not a silent mistake. If you tried to create a `DvdBuggy` that forgot to override
`getLoanPeriod()`, the whole project would refuse to build.

Run the demo's output for `Book` and `Dvd` (showing their different loan periods) to show both
approaches landing correctly.

## Part 5: Interfaces — a capability, not a hierarchy (5 min)

Show `Loanable.java`: `public interface Loanable { void loanTo(...); void returnItem(); ... }`.
Narration: an interface is a pure contract — method signatures, no body, no state at all, not
even a private field. `LibraryResource implements Loanable` (point back at `LibraryResource.java`'s
class declaration).

Now show `Library.java`: it doesn't implement `Loanable` at all — it's a manager class, not a
resource. Yet it *uses* `Loanable` — it calls `loanTo()` and `returnItem()` on the resources it
manages. Narration: this is exactly the problem abstract classes alone can't solve. An interface
defines a capability — "things that can be loaned" — and lets us use any class that provides that
capability, whether or not they're related by inheritance at all.

## Part 6: Polymorphism across the interface (2 min)

```java
List<Loanable> loanableThings = new ArrayList<>();
loanableThings.add(book);
loanableThings.add(magazine);
loanableThings.add(dvd);

for (Loanable item : loanableThings) {
    item.loanTo("MEMBER-001");
}
```

Narration: this list holds genuinely different concrete classes — `Book`, `Magazine`, `Dvd` —
each a subclass of `LibraryResource`, but all united by the one capability they share: they can
be loaned. That capability alone is enough for this code to work. Each resource type has its own
loan period, its own behavior, but the interface `Loanable` lets us treat them uniformly.

## Part 7: Recognising bad inheritance — composition is better (3 min)

Show `Library.java`: it *contains* an `ArrayList<LibraryResource>`, but doesn't *extend* it.
Narration: this is the right pattern. If we'd written `class Library extends ArrayList` instead,
we'd inherit every ArrayList method — `add()`, `remove()`, `clear()`, `toArray()`, all of it —
and expose them to users of the `Library` class. But a library shouldn't let clients do
arbitrary things to the collection; it should control exactly how resources are added and
retrieved. By using composition (the `resources` field), `Library` exposes only the methods it
wants: `addResource()`, `findById()`, `getAvailableResources()`. The internal list is hidden,
controlled, and protected.

Narration: the rule is simple — **use inheritance when the subclass *is a* specialization of
the parent** (a `Book` *is a* `LibraryResource`), and **use composition when the class has a
relationship but isn't a true specialization** (a `Library` *has a* collection of resources, but
isn't a collection itself). Inheritance is powerful but also dangerous — it locks you into a
hierarchy and exposes everything. Composition gives you control.

## Key message

Encapsulation is the simplest idea today: one object protecting its own data. Inheritance builds
on the same mechanism whether it goes right (`Book` with `getLoanPeriod()` overridden) or wrong
(a `DvdBuggy` silently inheriting the wrong loan period). Abstract classes turn that silent
failure into a compiler error. Interfaces solve a different problem entirely: a capability
shared across classes that may or may not be related by inheritance. And when building larger
structures like `Library`, composition — delegating to a contained collection — gives you the
control you need, while inheritance would expose too much and lock you into a hierarchy that
doesn't fit the problem.
