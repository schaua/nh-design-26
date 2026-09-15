# Module 12 Demo Guide — TDD in Practice

This is where Modules 3, 7-11 converge: build a real piece of the library management system
(`LoanValidator`, derived from the library requirement), test-first, using
JUnit's organisational features from Module 11.

Delete `LoanValidator.java`'s body, `OutstandingFees.java`, and
`LoanValidatorTest.java`'s contents before the session. Build it back up live, in this order.

## Before Writing Any Code: Read the Requirement Together

Reference Module 3's demo-guide.md, which describes this library requirement:

> "A library member can borrow up to five books at a time. When a member borrows a book, a loan 
> record is created with a due date 21 days later. If a book is returned after its due date, the 
> member is charged a late fee of 20p per day. A member with more than £5 of unpaid fees cannot 
> borrow further books until the fees are paid."

Point out: the requirement's "cannot borrow further books" part means borrowing requires validation—
this is *why* this build starts with a small value type (`OutstandingFees`), not a plain 
`boolean` — the test-first process surfaces this design need immediately, because the very first 
test can't be written meaningfully without deciding what `validate()` returns and what information 
it carries.

## Cycle 1: The Simplest Valid Case, and `OutstandingFees` Is Born

**Red:**
```java
@Test
void acceptsAValidBorrowRequest() {
    LibraryResource book = new Book("ISBN001");
    LoanRequest request = new LoanRequest(book, 0, 0);
    OutstandingFees result = validator.validate(request);
    assertTrue(result.canBorrow());
}
```
This doesn't compile yet — `LoanRequest` and `OutstandingFees` don't exist. Write the smallest
versions of both that make this compile and pass: `LoanRequest` with three fields (resource, 
outstandingFees, currentLoansCount) and a constructor; `OutstandingFees` with a static 
`allowed()` factory and `canBorrow()`.

**Green:**
```java
public OutstandingFees validate(LoanRequest request) {
    return OutstandingFees.allowed(request.getOutstandingFees());
}
```
Say it out loud again: yes, this ignores the loan count and resource. That's correct — nothing 
has demanded otherwise yet.

## Cycle 2-3: The Constraint Checks

**Red** (max loans): a member trying to borrow when they already have 5 loans should be rejected, 
with reason `"member has reached the maximum of 5 loans"`. This forces `OutstandingFees.rejected(reason)` 
and `getReason()` into existence too.

**Green:** the first `if` branch checking loan count.

**Red** (outstanding fees): same pattern — a member with fees over £5 should be rejected. This 
forces the second `if` branch.

**Green:** add the fee check.

## Cycle 4: Refactoring the Test Suite

**Refactor moment:** organise the growing test class into `@Nested` groups now, before it gets
harder to do — `WhenLoanConstraintsViolated`, `WhenFeesAreConcern`, `WhenMultipleConstraints`. 
Do this live; re-run tests immediately after to prove the reorganisation didn't change behaviour.
This is exactly the pattern from Module 11: each `@Nested` class corresponds to a natural grouping
in the requirement text itself (loan limits, fee limits, and their interaction).

## Cycle 5: Boundary Tests

Add tests like `acceptsWhenFeesExactlyAtLimit` (£5.00), `acceptsWhenBelowMaxLoans` (4 loans), 
and `acceptsWhenFeesJustUnderLimit` (£4.99) — these should already pass if `>` (not `>=`) was 
used correctly in the validation logic. Run them. If any fails, it's caught here, immediately, 
rather than as a production incident where a member is wrongly denied a book they're entitled to borrow.

## Points to Make Explicitly

- **The requirement drove the design, not the other way round.** `OutstandingFees` exists
  because the requirement said borrowing should be rejected with a *reason* — nobody sat down 
  and designed a "result object pattern" in the abstract first.
- **Every `@Nested` group corresponds to a natural grouping in the requirement text itself**
  (loan constraints, fee constraints, their interaction) — the test structure documents the 
  business rule structure from Module 3's library requirement.
- **The library entities (Book, Magazine, DVD) all extend LibraryResource**, which implements 
  `Feeable` — this is the exact inheritance and interface pattern from Modules 3-7, now applied 
  to a library domain instead of a financial one.
- **This is the same five-cycle shape as Module 10's `FeeBandClassifier` kata**, just applied to
  a real library requirement instead of an invented one. The discipline transfers directly.

## Transition to the Lab

Learners build a complementary piece — `LoanManager`, which applies an already-validated
loan to a member's account — using the exact same test-first discipline, working from
`labs/12-tdd-in-practice/README.md`.
