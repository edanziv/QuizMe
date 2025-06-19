import React from 'react';
import FileUpload from './components/FileUpload';
import GetAnswers from './components/GetAnswers';
import './styles.css';

function App() {
  return (
    <div className='main-page'>
      <h1>QuizMe GenAI</h1>
      <h3>Upload your test material in a pdf or txt format and prepare for the QUIZ!</h3>
      <FileUpload />
      <GetAnswers/>
    </div>
  );
}

export default App;