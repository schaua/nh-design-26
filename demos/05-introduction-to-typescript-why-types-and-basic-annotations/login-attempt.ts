// Primitive type annotations - a colon after the name, then the type.
const username: string = "alice";
let attemptCount = 3;
const isLockedOut: boolean = false;

// Typing a function: each parameter gets a type, and the return type
// comes after the parameter list.
function describeOutcome(outcome: string): string {
  if (outcome === "success") {
    return "logged in successfully";
  } else {
    return "failed to log in";
  }
}
// attemptCount = describeOutcome("success");
console.log(describeOutcome("success"));
// console.log(describeOutcome(42));

// An interface names a shape - the same "attempt object" from Module 3,
// but now with its shape written down and enforced.
interface Attempt {
  username: string;
  outcome: string;
}

const attempt: Attempt = 
{ username: "alice", outcome: "success" };
console.log(attempt);

function describeAttempt(attempt: Attempt): string {
  return `${attempt.username} ${describeOutcome(attempt.outcome)}`;
}
console.log(describeAttempt(attempt));
