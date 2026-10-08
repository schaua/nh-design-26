// Shared across all three demo files - a fake network call to verify a
// username, standing in for a real HTTP request to Sprint 6's auth-stub.
// setTimeout is the stand-in for "this takes time and doesn't finish
// immediately" - the same reason a real fetch() to an auth service is
// asynchronous.

const KNOWN_USERS = ["alice", "bob", "carol"];

// Callback-based version of the credential verification.
export function verifyCredentialsBlocking(username, callback) {
  for (let i = 0; i < 3000000; i++) { // spin
  }
  if (KNOWN_USERS.includes(username)) {
    callback(null, { username, verified: true });
  } else {
    callback(new Error(`Unknown user: ${username}`));
  }
}

// Node-style callback: (error, result) => void. err is null on success.
export function verifyCredentialsCallback(username, callback) {
  setTimeout(() => {
    if (KNOWN_USERS.includes(username)) {
      callback(null, { username, verified: true });
    } else {
      callback(new Error(`Unknown user: ${username}`));
    }
  }, 200);
}

// Promise-based version of the exact same check.
export function verifyCredentials(username) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (KNOWN_USERS.includes(username)) {
        resolve({ username, verified: true });
      } else {
        reject(new Error(`Unknown user: ${username}`));
      }
    }, 20);
  });
}
