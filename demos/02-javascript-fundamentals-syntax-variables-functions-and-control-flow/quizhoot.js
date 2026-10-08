import readline from 'readline';

// Create interface for input/output
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});
const maxAttempts = 1;
let quizItem = {};
quizItem.questionText = "What is the capital of France?";
quizItem.possibleAnswers = ["Paris", "London", "Berlin", "Madrid"];
quizItem.correctAnswer = 0;
  
 rl.question(`${quizItem.questionText}`,  input => {
   console.log("You entered: " + input);
 });

// for (let answerIndex = 0; answerIndex < quizItem.possibleAnswers.length; answerIndex = answerIndex + 1) {
//   console.log("[" + answerIndex + "] " + quizItem.possibleAnswers[answerIndex]);
// }

// for (let attempt = 0; attempt < maxAttempts; attempt = attempt + 1) {
//   answer = await new Promise(resolve => rl.question("What is your answer? ", resolve)).then(input => parseInt(input.trim()));
//   if (parseInt(answer) === quizItem.correctAnswer) {
//     console.log("Correct!");
//     break;
//   } else {
//     console.log("Incorrect.");
//   }
//   console.log("Attempt #" + (attempt + 1));
// }   
