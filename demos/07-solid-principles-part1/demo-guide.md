# Module 7 Demo Guide — SOLID Principles Part 1 (S, O, L)

Run `SolidDemo.java` end to end first, then walk back through it section by section. Each
section is a direct payoff of earlier modules — make that link explicit as you go.

```bash
mvn package
java -cp target/classes com.neueda.leap.sprint5.SolidDemo
```

## S — Single Responsibility Principle

`BadReportGenerator` does calculation, formatting, *and* "delivery" (a `println` standing in for email) all in one method. Point out: the
output is identical whether you call `BadReportGenerator` or the split `FeeAggregator` +
`FeeReportFormatter` pair. **SRP is not about behaviour changing — it's about how many reasons a
class has to change.** Ask the group: name a change to the fee rule, the report format, and the
delivery mechanism, one at a time. In the bad version, all three risk touching the same file. In
the good version, each touches exactly one.

## O — Open/Closed Principle

`DVD` is a brand new resource type with its own fee rule. The demo's point
isn't the 2% calculation — it's the sentence in the console output: *zero changes to LibraryResource,
Feeable, ResourceHold, FeeAggregator, or FeeReportFormatter*. This is the direct payoff of Module 1-2's
`Feeable` interface: the abstraction was built before this requirement existed, and it absorbed
the new requirement without being touched. Ask: what would adding a new resource type have
looked like without that abstraction? (Answer: an edit to core fee calculation logic, risking every existing resource type's fee logic in the same change.)

## L — Liskov Substitution Principle

This is the subtlest of the three, so slow down here. Run the demo and watch `adjustAll` crash on
`FrozenLoanRequestBad` even though the calling code only ever declared `List<Loan>` — nothing
about the *type* warned the caller this could happen. Contrast with `Loan.adjust()`'s own
`IllegalArgumentException`: that one's fine, because it's part of the documented contract every
caller already expects (don't let the quantity go negative). The problem isn't "throwing
exceptions" — it's throwing *unconditionally*, breaking a promise the type claims to keep.

Show the fix: `FrozenLoanRequest` doesn't extend `Loan` at all — it wraps one (composition, same
pattern as Module 4's wrapping approach). No `adjust()` method exists on it, so
there's no contract to break.

## Points to Make Explicitly

- **All three of today's fixes are things you already did in Modules 1-2 and 4**, without the
  vocabulary for them yet: `LibraryResource`'s encapsulation, `Feeable`'s abstraction, composition over
  inheritance. SOLID names patterns you've already been practising.
- **These three are about structure, not correctness.** `BadReportGenerator` and the SRP-split
  version produce identical output. The value is entirely in how easy each is to change safely.

## Transition to the Lab

Learners implement the same three fixes themselves: `FeeAggregator`/`FeeReportFormatter` (SRP),
`DVD` (OCP), and `FrozenLoanRequest` (LSP) — with `FrozenLoanRequestBad.java` given as
the "before," exactly as in this demo.
