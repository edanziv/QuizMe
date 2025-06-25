// File: src/FileUpload.js
import React from 'react';

function FileUpload({file, setFile, questions, setQuestions}) { //useState - returns a stateful value and a function to update
  const handleFileChange = (e) => setFile(e.target.files[0]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) return;
    setQuestions("Processing...")
    try{
      const formData = new FormData();
      formData.append('file', file);
      const res = await fetch('http://localhost:8080/api/quiz/upload', {
        method: 'POST',
        body: formData,
      });
      const data = await res.json();
      setQuestions(Array.isArray(data) ? data.join("\n\n") : String(data));
    } catch (error) {
      console.error('Error uploading file:', error);
      setQuestions("Error uploading file. Please try again.");    
    }
  };

  return (
    <div>
      <h3>Upload your test material in a pdf or txt format and prepare for the QUIZ!</h3>
      <form onSubmit={handleSubmit}>
        <input type="file" accept=".pdf,.txt" onChange={handleFileChange} />
        <button type="submit" className='button'>Upload</button>
      </form>
      <div>
        <textarea
            value={questions}
            readOnly
        />
      </div>
    </div>
  );
}

export default FileUpload;