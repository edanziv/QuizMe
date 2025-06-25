import React, {useState} from "react";
import { useNavigate } from "react-router-dom";

export default function Register() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleUsernameChange = (e) => setUsername(e.target.value);
    const handlePasswordChange = (e) => setPassword(e.target.value);

    const handleRegister = async (e) => {
        e.preventDefault();
        if (!username || !password) {
            alert("Please enter both username and password.");
            return;
        }
        const formData = new FormData();
        formData.append("username", username);  
        formData.append("password", password);
        const res = await fetch("http://localhost:8080/api/quiz/register", {
            method: "POST",
            headers: {
            "Content-Type": "application/json",
            },
            body: JSON.stringify({ username: username, password: password }),
        });
        if (res.ok) {
            navigate("/login");
        } else {
            alert(`The username ${username} is taken. Please choose a different one.`);
        }
    }
  return (
    <div className="login-page">
      <h1>Register to QuizMe!</h1>
      <form onSubmit={handleRegister}>
        <div className="form-group">
          <label htmlFor="username">Username:</label>
          <input type="text" id="username" name="username" required onChange={handleUsernameChange}/>
        </div>
        <div className="form-group">
          <label htmlFor="password">Password:</label>
          <input type="password" id="password" name="password" required onChange={handlePasswordChange}/>
        </div>
        <button type="submit" className='button'>Register</button>
      </form>
    </div>
  );
}