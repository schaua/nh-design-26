var questionOne = {
    question: "What is the capital of France?",
    options: ["Paris", "London", "Berlin", "Madrid"],
    correctAnswerIndex: 0,
    points: 50
};

console.log(questionOne);
questionOne.time = 30;
console.log(questionOne);    

const {a,b,c,d,e} = questionOne;
console.log(a, b, c, d, e);

var points = 42;
const {options, question, correctAnswerIndex, time} = questionOne;
console.log(question, options, correctAnswerIndex, points, time);   

const [a1, b1, c1, d1, e1, f1] = [1,1,2,3,5,8];
console.log(a1, b1, c1, d1, e1, f1);

const scoresFrost = [95, 87, 78, 92, 88, 100];
const scoreThorton = [89, 90, 85, 91, 87];
const allScores = [...scoresFrost, ...scoreThorton];
console.log(allScores); 

const newQuestion = {...questionOne, points: 1000, options: [ `South Paris`, `Norway`, `Poland`] };
console.log(newQuestion);   

const joinQuiz = (quizNumber, ...players) => {
    console.log(`Quiz Number: ${quizNumber}`);
    console.log(`Players: ${players.join(', ')}`);
};
joinQuiz(1, "Alice", "Bob", "Charlie");
joinQuiz(2, "David", "Eve");
joinQuiz(3, "Frank", "Grace", "Hannah", "Ivy");
joinQuiz(4, "Jack", "Kathy", "Liam");

console.log(`Quiz number 4 has ${questionOne.options.length} options.`)
console.log(`Quiz number 4 has ${newQuestion.options.length} options.`);

console.log(`The 4 options are: ${questionOne.options.map(option => option.toUpperCase()).join(', ')}`);
const tempOptions = questionOne.options.map(option => option.toUpperCase());
console.log(`The temporary options are: ${tempOptions.join(', ')}`);
console.log(`The 6 character options are: ${questionOne.options.filter(option => option.length === 6).join(', ')}`);

const KNOWN_USERS = ["Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hannah", "Ivy", "Jack", "Kathy", "Liam"];

export function verifyCredentialsCallback(username, callback) {
    setTimeout(() => {
        if (KNOWN_USERS.includes(username)){
            callback(null, { username, verified: true });
        } else {
            callback(new Error(`Unknown user: ${username}`));
        }
    }, 20);
}

console.log("Checking Grace...");
verifyCredentialsCallback("Grace", (err, result) => {
    if (err) {
        console.error(err.message);
    } else {
        console.log(result);
    }
});
console.log("This line is after the verification of Grace.");

console.log("Checking Augie...");
verifyCredentialsCallback("Augie", (err, result) => {
    if (err) {
        console.error(err.message);
    } else {
        console.log(result);
    }
});
console.log("This line is after the verification of Augie.");

// The pyramid of DOOM
verifyCredentialsCallback("Alice", (err, result) => {
    if (err) {
        console.error(err.message);
    } else {
        console.log(result);
        verifyCredentialsCallback("Bob", (err, result) => {
            if (err) {
                console.error(err.message);
            } else {
                console.log(result);
                verifyCredentialsCallback("Dave", (err, result) => {
                    if (err) {
                        console.error(err.message);
                    } else {
                        console.log(result);
                    }
                });
            }
        });
    }
});
