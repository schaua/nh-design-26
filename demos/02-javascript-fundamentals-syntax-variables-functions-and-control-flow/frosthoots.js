// A simple quiz game using JavaScript fundamentals: syntax, variables, functions, and control flow.
// Define the first question

const question1 = {
    questionText: "What is the capital of France?",
    correctAnswer: "Paris",
    options: ["Paris", "London", "Berlin", "Madrid"],
    points: 10
};

// let questionText = question1.questionText;
// let correctAnswer = question1.correctAnswer;
// let options = question1.options;
// let points = question1.points;
// let timer = question1.timer;

const { questionText, correctAnswer, option, points, timer } = question1;
console.log(questionText, correctAnswer, option, points, timer);

console.log(question1);
const timedQuestion = {...question1, points: 30}; 
console.log(timedQuestion); // Log the updated question object with the timer
    
// const quiz = [question1]; // Array of questions

const frostScores = ["98", "87", "76"];
const thortonScores = ["92", "85", "78"];
const allScores = [...frostScores, ...thortonScores, "100"];
console.log(allScores);

const highestScore = (label, ...scores) => {
    return console.log(`${label}: `, ...scores);
};

highestScore("All Scores", ...allScores);

const upperCaseOptions = question1.options.map(possibleAnswer => possibleAnswer.toUpperCase()).
filter(possibleAnswer => possibleAnswer.startsWith("P"));
console.log(upperCaseOptions);