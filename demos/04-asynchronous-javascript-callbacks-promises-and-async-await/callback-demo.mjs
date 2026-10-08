import { verifyCredentialsCallback , verifyCredentialsBlocking } from "./verify-credentials.mjs";

// Add a demonstration of the blocking version of the credential check.
// Not asynchronous - it blocks the event loop until it finishes.

// console.log("...blocking call");
// console.log("Checking alice...");
// verifyCredentialsBlocking("alice", (err, result) => {
//   if (err) {
//     console.log("Slow Error:", err.message);
//     return;
//   }
//   console.log("Slowly Verified:", result.username);
// });
// console.log("...done after blocking call");

// Back to the originally scheduled program...


console.log("--- a single callback ---");
console.log("Checking alice...");
verifyCredentialsCallback("alice", (err, result) => {
  if (err) {
    console.log("Error:", err.message);
    return;
  }
  console.log("Verified:", result.username);
});
console.log("This line runs BEFORE the callback above - verifyCredentialsCallback returns immediately");

// // Give the first block's setTimeout a moment to fire before starting the
// // next one, purely so the two demo sections don't interleave in the
// // console. Not something you'd do in real code.
setTimeout(() => {
  console.log("");
  console.log("--- callback hell: three checks, nested ---");
  verifyCredentialsCallback("dave", (err1, result1) => {
    if (err1) {
      console.log("Error:", err1.message);
      return;
    }
    console.log("Verified:", result1.username);
    verifyCredentialsCallback("bob", (err2, result2) => {
      if (err2) {
        console.log("Error:", err2.message);
        return;
      }
      console.log("Verified:", result2.username);
      verifyCredentialsCallback("dave", (err3, result3) => {
        if (err3) {
          console.log("Error:", err3.message);
          return;
        }
        console.log("Verified:", result3.username);
      });
    });
  });
}, 210);
