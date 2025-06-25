import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";
import FileUpload from './FileUpload';
import GetAnswers from './GetAnswers'

export default function HomePage({username}) {
    const [file, setFile] = useState(null);
    const [questions, setQuestions] = useState("");
    const [answers, setAnswers] = useState("");
    const navigate = useNavigate();

    const handleLogout = () => {
        navigate("/login");
    };

    const handleSaveResults = async () => {
        if (questions.length === 0) {     
            alert("No questions to save. Please fetch questions first.");
            return;
        }
        try {
            console.log({
                fileName: file ? file.name : "",
                userName: username,
                questions: questions,
                answers: answers
            });
            const res = await fetch("http://localhost:8080/api/quiz/saveQuiz", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({fileName: file ? file.name : "", userName: username,  questions: questions, answers: answers }),
            });
            if (res.ok) {
                alert("Results saved successfully!");
            } else {
                alert("Failed to save results. Please try again.");
            }
        } catch (error) {
            console.error("Error saving results:", error);
            alert("Error saving results. Please try again.");
        }
    }

    return (
        <div className='main-page'>
            <button className="logout-button" onClick={handleLogout}>logout</button>
            <h1>QuizMe GenAI</h1>
            <FileUpload 
            file={file}
            setFile={setFile}
            questions={questions}
            setQuestions={setQuestions}/>
            <GetAnswers 
            answers={answers}
            setAnswers={setAnswers}/>
            <button className='button' onClick={handleSaveResults}>Save Results</button>
        </div>
    );
}