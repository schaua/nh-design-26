# Module 9 Demo Guide — Clean Code

Run `CleanCodeDemo.java` first, then walk through the two classes side by side.

```bash
mvn package
java -cp target/classes com.neueda.leap.sprint5.CleanCodeDemo
```

## The Point: Identical Output, Very Different Readability

Put `MessySettlementSummary.s()` and `SettlementSummary.summarize()` on screen together. The
output is byte-for-byte identical — this is important to say explicitly: **clean code isn't
about correctness, it's entirely about how cheap the code is to read, change, and trust later.**

## Walk the Checklist Against the Messy Version

Go through `clean-code-checklist.md` against `MessySettlementSummary` live:

- **Naming**: `s`, `l`, `m`, `mf`, `x`, `e`, `r` — none of them say what they hold. Ask the
  group to guess what `mf` and `l` mean *before* revealing the answer (member fees and loans) — 
  the fact that nobody can guess correctly is the point.
- **Complex structure**: grouping loans by member and formatting are mixed together in one method,
  rather than being separated into distinct responsibilities.
- **One long method, several jobs**: grouping members by their late fees and formatting a string are
  two different responsibilities, all inline in one method — this is Module 7's SRP, showing up
  again at the method level, not just the class level.
- **The comment**: `// loop through the loans and group by member` describes WHAT the
  next few lines do — which the code already shows. A useful comment would explain something the
  code *can't* show, like the business logic for late fee calculations. There isn't one here, because
  that logic lives in the `LibraryResource` subclasses (Book, DVD, Magazine), which is itself worth
  noticing: the comment's absence signals good design elsewhere.

## Show the Refactor Landing, Piece by Piece

In `SettlementSummary`, point out each fix lands on a specific checklist item:

- `groupLoansByMember()` — collects all late fee calculations for each member, nothing about 
  formatting lives here. Shows clear intent in the method name.
- `format()` — turns the grouped data into the report string. No calculation or grouping happens 
  here; the method receives already-computed results.
- Clear variable names (`memberFees`, `memberId`, `fees`) — each name says exactly what it holds
- No comments at all — none were needed once the names and structure carried the meaning

## Points to Make Explicitly

- **This was a pure refactor — no new behaviour, no bug fixes.** That's deliberate: it isolates
  "harder to read" from "does something different," so the group can evaluate readability without
  the distraction of correctness changes.
- **The business logic lives in the right place.** Late fee calculations are defined by each 
  resource type (`Book`, `DVD`, `Magazine`) through the `Feeable` interface's `lateFeeCalculations()` 
  method. The summary classes just orchestrate reporting, not calculate fees.
- **Clean code and SOLID overlap, but aren't the same thing.** `SettlementSummary`'s two extracted 
  methods are *also* small steps toward SRP, but the lesson here is about readability at the 
  statement/method level, not the class-level responsibility split from Module 7.

## Transition to the Lab

Learners refactor `OrderCategoriser.java` by hand first (Part 1), then — separately — ask GitHub
Copilot Chat to refactor the same original messy class and critically assess its suggestions
against the same checklist (Part 2). The order matters: doing it by hand first means they have
their own informed opinion before seeing what Copilot proposes, rather than anchoring on its
suggestion.

