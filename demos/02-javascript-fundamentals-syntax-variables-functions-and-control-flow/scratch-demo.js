const pi = 3.14;

if (pi  === 3.14){
   var correctAnswers = 0;
   let missedQuestions = 0;
   console.log(missedQuestions);
}

console.log(correctAnswers);


const question1 = "What is the capital of France?";

function askQuestion(question) {
   console.log(question);
}
askQuestion(question1);

const getAnswer = function (question) {
   console.log(question);
   return "Paris";
};

const answer1 = getAnswer(question1);  
console.log(answer1);

let answerIndex = "1";
answerIndex = "0";
const correctAnswer = "0";
if (answerIndex === correctAnswer) {
   console.log("Correct!");
} else {
   console.log("Incorrect!");
}

do {
   console.log("This will run at least once.");
} while (answerIndex !== correctAnswer);


question2 = {
   question: "What is the capital of Germany?",
   answer: "Berlin"
};
console.log(question2);