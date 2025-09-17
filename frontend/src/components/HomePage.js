import React, { useState, useContext } from 'react';
import { useNavigate } from "react-router-dom";
import FileUpload from './FileUpload';
import GetAnswers from './GetAnswers';
import EmailContext from "../context/emailContext";
import "../styles.css";

export default function HomePage() {
    const [file, setFile] = useState(null);
    const [questions, setQuestions] = useState("");
    const [answers, setAnswers] = useState("");
    const { email, setEmail } = useContext(EmailContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        navigate("/login");
    };

    const handleHistory = () => navigate("/history");

    const handleSaveResults = async () => {
        if (questions.length === 0) {     
            alert("No questions to save. Please fetch questions first.");
            return;
        }
        try {
            console.log("questions: ", JSON.stringify(questions))
            const res = await fetch("http://localhost:8080/api/quiz/saveQuiz", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({fileName: file ? file.name : "",
                    email: email,  
                    questions: JSON.stringify(questions.split('\n\n')), //questions and answers will
                    answers: JSON.stringify(answers.split('\n\n')) }),  //be saved as JSON Arrays
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
            <div style={{ display: "flex", gap: "12px", marginBottom: "12px" }}>
                <button className="logout-button" onClick={handleLogout}>logout</button>
                <button
                    className="logout-button"
                    onClick={handleHistory}
                >
                    History
                </button>
            </div>
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