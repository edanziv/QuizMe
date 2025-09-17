import React, {useState} from "react";
import { useNavigate } from "react-router-dom";
import "../styles.css";

export default function Register() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleEmailChange = (e) => setEmail(e.target.value);
    const handlePasswordChange = (e) => setPassword(e.target.value);

    const handleRegister = async (e) => {
        e.preventDefault();
        if (!email || !password) {
            alert("Please enter both email and password.");
            return;
        }
        const formData = new FormData();
        formData.append("email", email);  
        formData.append("password", password);
        const res = await fetch("http://localhost:8080/api/quiz/register", {
            method: "POST",
            headers: {
            "Content-Type": "application/json",
            },
            body: JSON.stringify({ email: email, password: password }),
        });
        console.log("Response status:", res.status);
        if (res.ok) {
            navigate("/login");
        }
        else if (res.status === 422){
          alert("Invalid email format. Please enter a valid email address.");
        } else {
            alert(`The email ${email} is taken. Please choose a different one.`);
        }
    }
  return (
    <div className="login-page">
      <h1>Register to QuizMe!</h1>
      <form onSubmit={handleRegister}>
        <div className="form-group">
          <label htmlFor="email">Email:</label>
          <input type="text" id="email" name="email" required onChange={handleEmailChange}/>
        </div>
        <div className="form-group">
          <label htmlFor="password">Password:</label>
          <input type="password" id="password" name="password" required onChange={handlePasswordChange}/>
        </div>
        <button type="submit" className='button'>Register</button>
        <p><a href="/login">Back to Login</a></p>
      </form>
    </div>
  );
}