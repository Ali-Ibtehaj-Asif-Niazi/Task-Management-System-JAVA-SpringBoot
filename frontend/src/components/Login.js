import React, { useState, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post("http://localhost:8085/auth/login", {
        email,
        password,
      });
      
      console.log("Login response:", res.data);

      if (res.data.token && res.data.userId) {
        login(res.data.token, {
          id: res.data.userId,
          email: res.data.email,
          // Include any other user data returned by the server
        });
        alert("Logged in successfully");
        navigate('/projects');
      } else {
        throw new Error("Invalid response structure");
      }
    } catch (err) {
      console.error("Login error:", err);
      alert(err.response?.data || "Invalid credentials");
    }
  };

  return (
    <>
      <form onSubmit={handleSubmit}>
        <h2>Login</h2>
        <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        <button type="submit">Login</button>
      </form>
      <p>Don't have an account? <Link to="/signup">Sign up here</Link></p>
    </>
  );
}