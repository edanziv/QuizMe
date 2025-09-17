import React, {useState} from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import HomePage from './components/HomePage';
import LogIn from './components/LogIn';
import Register from './components/Register';
import EmailProvider from './context/emailProvider';
import History from './components/History';
import FileHistory from './components/FileHistory';
import './styles.css';

function App() {
  
  return (
    <div>
      <Router>
        <EmailProvider>
          <Routes>
            <Route path="/" element={<Navigate to="/login" replace/>}></Route>
            <Route path="/login" element={<LogIn/>}></Route>
            <Route path="/register" element={<Register/>}></Route>
            <Route path="/home" element={<HomePage/>}></Route>
            <Route path="/history" element={<History/>}></Route>
            <Route path="/file-history" element={<FileHistory/>}></Route>
          </Routes>
        </EmailProvider>
      </Router>
    </div>
  );
}

export default App;