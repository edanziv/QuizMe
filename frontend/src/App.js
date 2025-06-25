import React, {useState} from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import HomePage from './components/HomePage';
import LogIn from './components/LogIn';
import Register from './components/Register';
import UserNameProvider from './context/UserNameProvider';
import './styles.css';

function App() {
  //const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  return (
    <div>
      <Router>
        <UserNameProvider>
          <Routes>
            <Route path="/" element={<Navigate to="/login" replace/>}></Route>
            <Route path="/login" element={<LogIn 
            //username={username}
            //setUsername={setUsername}
            password={password}
            setPassword={setPassword}/>}></Route>
            <Route path="/register" element={<Register/>}></Route>
            <Route path="/home" element={<HomePage/>}></Route>
          </Routes>
        </UserNameProvider>
      </Router>
    </div>
  );
}

export default App;