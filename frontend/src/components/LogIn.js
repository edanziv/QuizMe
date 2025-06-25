import React from "react";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import UserNameContext from "../context/UserNameContext";

export default function LogIn({password, setPassword}) {
    const navigate = useNavigate();
    const {username, setUsername} = useContext(UserNameContext);

    const handleUsernameChange = (e) => setUsername(e.target.value);
    const handlePasswordChange = (e) => setPassword(e.target.value);
    console.log("Username:", username);
    console.log("Password:", password);

    const handleLogin = async (e) => {
        e.preventDefault();
        if (!username || !password) {
            alert("Please enter both username and password.");
            return;
        }
        const formData = new FormData();
        formData.append("username", username);  
        formData.append("password", password);
        const res = await fetch("http://localhost:8080/api/quiz/login", {
            method: "POST",
            headers: {
            "Content-Type": "application/json",
            },
            body: JSON.stringify({ username: username, password: password }),
        });
        if (res.ok) {
            navigate("/home");
        } else {
            alert("Login failed. Please check your credentials.");
        }
    }
  return (
    <div className="login-page">
      <h1>Log In</h1>
      <form onSubmit={handleLogin}>
        <div className="form-group">
          <label htmlFor="username">Username:</label>
          <input type="text" id="username" name="username" required onChange={handleUsernameChange}/>
        </div>
        <div className="form-group">
          <label htmlFor="password">Password:</label>
          <input type="password" id="password" name="password" required onChange={handlePasswordChange}/>
        </div>
        <button type="submit" className='button'>Log In</button>
        <p>Don't have an account? <a href="/register">Register here</a></p>
      </form>
    </div>
  );
}