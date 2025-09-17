import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import "../styles.css";

const PAGE_SIZE = 10; 

export default function FileHistory() {
    const location = useLocation();
    const { file_name, questions, answers } = location.state || {};

    let parsedQuestions = [];
    let parsedAnswers = [];

    try {
        parsedQuestions = JSON.parse(questions);
    } catch (e) {
        console.error("Invalid questions JSON:", e);
    }

    try {
        parsedAnswers = JSON.parse(answers);
    } catch (e) {
        console.error("Invalid answers JSON:", e);
    }

    const [visibleIndexes, setVisibleIndexes] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);

    const toggleAnswer = (index) => {
        setVisibleIndexes((prev) =>
            prev.includes(index)
                ? prev.filter((i) => i !== index)
                : [...prev, index]
        );
    };

    // Pagination logic
    const totalQuestions = parsedQuestions.length;
    const totalPages = Math.ceil(totalQuestions / PAGE_SIZE);
    const startIdx = (currentPage - 1) * PAGE_SIZE;
    const endIdx = startIdx + PAGE_SIZE;
    const paginatedQuestions = parsedQuestions.slice(startIdx, endIdx);
    const paginatedAnswers = parsedAnswers.slice(startIdx, endIdx);

    const handlePrevPage = () => {
        if (currentPage > 1) setCurrentPage(currentPage - 1);
    };

    const handleNextPage = () => {
        if (currentPage < totalPages) setCurrentPage(currentPage + 1);
    };

    return (
        <div className="file-history">
            <button className="logout-button" onClick={() => window.location.href = "/history"}>
                Back to History
            </button>
            <h2>File History</h2>
            <div className="file-details">
                <p><strong>File Name:</strong> {file_name}</p>

                <div className="qa-section">
                    <h3 className="qa-heading">Questions & Answers:</h3>
                    <div className="qa-list">
                        {paginatedQuestions.map((question, index) => (
                            <div key={startIdx + index} className="qa-block">
                                <button
                                    className="qa-question-button"
                                    onClick={() => toggleAnswer(startIdx + index)}
                                >
                                    <strong>Q{startIdx + index + 1}:</strong> {question}
                                </button>
                                {visibleIndexes.includes(startIdx + index) && (
                                    <p className="qa-answer">
                                        <strong>A{startIdx + index + 1}:</strong> {paginatedAnswers[index] || <em>No answer</em>}
                                    </p>
                                )}
                            </div>
                        ))}
                    </div>
                </div>
            </div>
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
