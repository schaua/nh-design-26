# Module 10 Demo Guide — TDD Fundamentals: ISBN Validator

This is a **live-coding** demo. Delete `ISBNValidator.java`'s body and
`ISBNValidatorTest.java`'s contents before the session (keep a copy to restore afterward, or
just re-type it live — either is fine). Build it back up in front of the group, one
red-green-refactor cycle at a time, exactly in this order. Narrate the three steps out loud every
single time, even once it starts to feel repetitive — the repetition IS the discipline.

## Background: ISBN Validation Rules

An ISBN (International Standard Book Number) is a unique identifier for books. This validator
supports both formats:

- **ISBN-13**: 13 digits, must start with 978 or 979 prefix, includes a check digit
- **ISBN-10**: 10 characters (9 digits + check digit), check digit can be 0-9 or 'X' (representing 10)

The validator must:
- Handle null and empty string inputs
- Support both 10 and 13 digit formats
- Validate the prefix (978 or 979 for ISBN-13)
- Compute and verify check digits using standard ISBN algorithms
- Allow formatting characters (hyphens and spaces) which are stripped before validation

## The Cycle, Said Out Loud Every Time

1. **Red** — write ONE new test, for ONE new piece of behaviour. Run it. Watch it fail. Read the
   failure message.
2. **Green** — write the SMALLEST amount of code that makes it pass. Not the smallest *sensible*
   amount — the smallest amount, full stop, even if it looks silly.
3. **Refactor** — with the safety net of passing tests, clean up anything that needs it. Run the
   tests again to confirm nothing broke.

## Cycle 1: Reject Null

**Red:**
```java
@Test
void rejectsNull() {
    assertFalse(new ISBNValidator().isValid(null));
}
```
Run it — fails, because `isValid` doesn't exist yet (or returns `false`/isn't implemented).

**Green — the smallest thing that passes:**
```java
public boolean isValid(String isbn) {
    return isbn != null;
}
```
**Say this explicitly:** "This is the simplest implementation that makes the test pass. We handle
null but nothing else yet — exactly what the test requires, nothing more."

**Refactor:** nothing to refactor yet.

## Cycle 2: Reject Empty String

**Red:**
```java
@Test
void rejectsAnEmptyString() {
    assertFalse(new ISBNValidator().isValid(""));
}
```
Run it — fails, because empty string is not null.

**Green:**
```java
public boolean isValid(String isbn) {
    if (isbn == null || isbn.isEmpty()) {
        return false;
    }
    return true;
}
```
Run both tests — both pass.

## Cycle 3: Accept a Valid ISBN-13

**Red:**
```java
@Test
void acceptsAValidISBN13() {
    // 978-0-306-40615-7
    assertTrue(new ISBNValidator().isValid("9780306406157"));
}
```
This test now forces us to actually validate ISBN structure.

**Green:** Add basic ISBN-13 handling. Return `false` for anything that doesn't look like ISBN-13.
For now, just check length (13 digits) and that all are numeric.

**Refactor:** Extract the length constants and the numeric check into helper methods.

## Cycle 4: Validate ISBN-13 Prefix

**Red:**
```java
@Test
void rejectsISBN13WithInvalidPrefix() {
    // Invalid prefix (not 978 or 979)
    assertFalse(new ISBNValidator().isValid("9770306406157"));
}
```
**Green:** Extract the first 3 digits and check that they're either 978 or 979.

## Cycle 5: Validate ISBN-13 Check Digit

**Red:**
```java
@Test
void rejectsISBN13WithInvalidCheckDigit() {
    // Wrong check digit
    assertFalse(new ISBNValidator().isValid("9780306406158"));
}
```
**Green:** Implement the ISBN-13 check digit algorithm (weighted sum mod 10). Calculate what the
check digit should be and compare it against the last digit of the ISBN.

**Refactor:** Extract the check digit validation into a private helper method `isValidISBN13CheckDigit`.

## Cycle 6: Accept ISBN-13 With Formatting

**Red:**
```java
@Test
void acceptsISBN13WithHyphens() {
    assertTrue(new ISBNValidator().isValid("978-0-306-40615-7"));
}
```
Run it — fails because the code looks for exactly 13 digits, but the hyphens are there.

**Green:** Strip hyphens (and spaces) from the input before processing. Add this to the beginning
of `isValid`.

**Refactor:** The ISBN-13 specific logic is now clearly separated. Make this obvious with clear
variable names.

## Cycle 7: Support ISBN-10

**Red:**
```java
@Test
void acceptsAValidISBN10() {
    // 0-306-40615-2
    assertTrue(new ISBNValidator().isValid("0306406159"));
}
```
**Green:** After stripping formatting, check if it's 10 characters. If so, run a different
validation path for ISBN-10. Implement a basic ISBN-10 validator that checks:
- First 9 characters are digits
- Last character is digit or 'X'
- Basic check digit validation (simple algorithm for now)

## Cycle 8: Validate ISBN-10 Check Digit Properly

**Red:**
```java
@Test
void rejectsISBN10WithInvalidCheckDigit() {
    assertFalse(new ISBNValidator().isValid("0306406150"));
}
```
**Green:** Implement the correct ISBN-10 check digit algorithm (weighted sum mod 11, where 10
is represented by 'X').

**Refactor, real refactoring now:** Extract the common ISBN validation concerns:
- The length/format distinction into separate methods: `isValidISBN13` and `isValidISBN10`
- Extract check digit methods: `isValidISBN13CheckDigit` and `isValidISBN10CheckDigit`
- Use named constants for length and prefix values
- Run tests immediately after each extraction to prove nothing broke

## Cycle 9: Reject Wrong Lengths

**Red:**
```java
@Test
void rejectsWrongLength() {
    assertFalse(new ISBNValidator().isValid("978030640615"));
}
```
**Green:** If neither 10 nor 13, return false. This should already work, but explicitly test it.

## Cycle 10 (bonus, if time allows): ISBN-10 with X Check Digit

**Red:**
```java
@Test
void acceptsISBN10WithCheckDigitX() {
    assertTrue(new ISBNValidator().isValid("043942089X"));
}
```
**Green:** The ISBN-10 validation should already handle this if your check digit algorithm
correctly maps 10 to 'X'.

## Points to Make Explicitly

- **Every single test was written before the code that satisfies it.** Nobody wrote
  `ISBNValidator` and then tests to match — the tests are what *drove* each addition to the
  implementation.
- **The implementation only ever does exactly what a test demands.** Notice there's no
  speculative handling for cases nobody's written a test for yet — that's not an oversight, it's
  TDD's discipline against over-building.
- **"Smallest thing that passes" felt uncomfortable in Cycle 1, and that's normal.** Point out
  that the discomfort fades once the pattern of "next test forces the next real behaviour"
  becomes visible.
- **The ISBN-13 prefix and check digit requirements came from real ISBN standards**, but they
  were introduced one test at a time. This mirrors real-world TDD: requirements emerge through
  failing tests, not all at once.
- **Library data vs. Financial data:** This shift from Ticker validation (Finance domain) to ISBN
  validation (Library domain) shows that TDD discipline applies across different problem spaces.
  The cycle is timeless — only the domain changes.

## Transition to the Lab

Learners now run this exact cycle themselves, from a blank class, building a validator or utility
specific to the lab assignment — see the lab README for the step list they'll follow.
