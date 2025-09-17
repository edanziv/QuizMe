import React, { useContext, useState, useEffect } from "react";
import { Link } from "react-router-dom";
import EmailContext from "../context/emailContext";
import FileHistory from "./FileHistory";
import "../styles.css";

export default function History({files}) {
    const { email } = useContext(EmailContext);
    const [history, setHistory] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const filesPerPage = 10;

    const fetchHistory = async () => {
        try {
        const res = await fetch("http://localhost:8080/api/quiz/history", {
            method: "POST",
            headers: {
            "Content-Type": "application/json",
            },
            body: JSON.stringify({ email: email }),
        });
        if (res.ok) {
            const data = await res.json();
            setHistory(data);
        } else {
            alert("Failed to fetch history. Please try again.");
        }
        } catch (error) {
        console.error("Error fetching history:", error);
        alert("Error fetching history. Please try again.");
        }
    };

    useEffect(() => {
        if (email) {
            setCurrentPage(1);
            fetchHistory();
        } else {
            setHistory([]);
        }
    }, [email]);

    const handleGenerateQuize = async (file_name) => {
        try{
            const formData = new FormData();
            formData.append('file_name', file_name);
            const res = await fetch('http://localhost:8080/api/quiz/generateQuiz', {
                method: 'GET',
                body: formData,
            });
            const data = await res.json();
        } catch (error) {
            console.error("Error generating quiz:", error);
        }
    }

    // Pagination logic
    const indexOfLastFile = currentPage * filesPerPage;
    const indexOfFirstFile = indexOfLastFile - filesPerPage;
    const currentFiles = history.slice(indexOfFirstFile, indexOfLastFile);
    const totalPages = Math.max(1, Math.ceil(history.length / filesPerPage));

    const handlePrevPage = () => {
        setCurrentPage((prev) => Math.max(prev - 1, 1));
    };

    const handleNextPage = () => {
        setCurrentPage((prev) => Math.min(prev + 1, totalPages));
    };
    
    return (
        <div className="history-page">
        <button className="logout-button" onClick={() => window.location.href = "/home"}>Home</button>
            <h1>Quiz History</h1>
            { history.length === 0 ? (
                <p>No history found.</p>
            ) : (
                <ul>
                    {currentFiles.map((item, index) => (
                    <li key={index}>
                        <div className="history-list-row">
                            <Link
                                to="/file-history"
                                state={{
                                    file_name: item.file_name,
                                    questions: item.questions,
                                    answers: item.answers
                                }}
                                >
                                {item.file_name}
                            </Link>
                            <button className="generate-quiz-btn" onClick={() => handleGenerateQuize(item.file_name)}>Generate Quiz</button>
                        </div>
                    </li>
                    ))}
                </ul>
                )
            }
            <div className="pagination-controls">
                <button
                className="pagination-btn"
                onClick={handlePrevPage}
                disabled={currentPage === 1}
                >
                Previous
                </button>
                <span className="pagination-info">
                Page {currentPage} of {totalPages}
                </span>
                <button
                className="pagination-btn"
                onClick={handleNextPage}
                disabled={currentPage === totalPages}
                >
                Next
                </button>
            </div>
        </div>
    );
}