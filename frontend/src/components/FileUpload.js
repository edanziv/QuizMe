// File: src/FileUpload.js
import React, { useState } from 'react';

function FileUpload() { //useState - returns a stateful value and a function to update
  const [file, setFile] = useState(null);
  const [results, setResults] = useState([]);

  const handleFileChange = (e) => setFile(e.target.files[0]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) return;
    setResults(["Processing..."])
    try{
      const formData = new FormData();
      formData.append('file', file);
      const res = await fetch('http://localhost:8080/api/quiz/upload', {
        method: 'POST',
        body: formData,
      });
      const data = await res.json();
      setResults(data);
    } catch (error) {
      console.error('Error uploading file:', error);
      setResults(["Error uploading file. Please try again."]);    
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <input type="file" accept=".pdf,.txt" onChange={handleFileChange} />
        <button type="submit" className='button'>Upload</button>
      </form>
      <div>
        <textarea
            value={results.join('\n\n')}
            readOnly
        />
      </div>
    </div>
  );
}

export default FileUpload;