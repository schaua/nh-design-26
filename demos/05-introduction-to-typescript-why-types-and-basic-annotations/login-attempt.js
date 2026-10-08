"use strict";
// Primitive type annotations - a colon after the name, then the type.
const username = "alice";
let attemptCount = 3;
const isLockedOut = false;
// Typing a function: each parameter gets a type, and the return type
// comes after the parameter list.
function describeOutcome(outcome) {
    if (outcome === "success") {
        return "logged in successfully";
    }
    else {
        return "failed to log in";
    }
}
// attemptCount = describeOutcome("success");
console.log(describeOutcome("success"));
const attempt = { username: "alice", outcome: "success" };
console.log(attempt);
function describeAttempt(attempt) {
    return `${attempt.username} ${describeOutcome(attempt.outcome)}`;
}
console.log(describeAttempt(attempt));
