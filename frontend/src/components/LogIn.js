import React from "react";
import { useNavigate } from "react-router-dom";
import { useContext, useState } from "react";
import EmailContext from "../context/emailContext";
import "../styles.css";

export default function LogIn() {
    const navigate = useNavigate();
    const {email, setEmail} = useContext(EmailContext);
    const [password, setPassword] = useState("");

    const handleEmailChange = (e) => setEmail(e.target.value);
    const handlePasswordChange = (e) => setPassword(e.target.value);
    console.log("email:", email);
    console.log("Password:", password);

    const handleLogin = async (e) => {
        e.preventDefault();
        if (!email || !password) {
            alert("Please enter both email and password.");
            return;
        }
        const formData = new FormData();
        formData.append("email", email);  
        formData.append("password", password);
        const res = await fetch("http://localhost:8080/api/quiz/login", {
            method: "POST",
            headers: {
            "Content-Type": "application/json",
            },
            body: JSON.stringify({ email: email, password: password }),
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
          <label htmlFor="email">Email:</label>
          <input type="text" id="email" name="email" required onChange={handleEmailChange}/>
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