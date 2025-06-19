import React, {useState} from "react";

export default function GetAnswers({ results }) {
    const [answers, setAnswers] = useState([]);

    const handleGetAnswers = async (e) => {
        e.preventDefault();
        try{
            setAnswers(["Fetching answers..."]);
            const res = await fetch("http://localhost:8080/api/quiz/answers", {
            method: "GET",
            });
            const data = await res.json();
            setAnswers(data);
        } catch (error) {
            console.error("Error fetching answers:", error);
            setAnswers(["Error fetching answers. Please try again."]);
        }
    }

    return (
        <div>
            <button
                className='button'
                onClick={handleGetAnswers}>Get Answers</button>
            <textarea
                value={answers.join("\n\n")}
                readOnly
            />
        </div>
    );
}