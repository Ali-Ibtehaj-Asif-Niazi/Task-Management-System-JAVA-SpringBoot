import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Login from "./components/Login";
import ProjectList from "./components/ProjectList";
import CreateProject from "./components/CreateProject";
import Signup from "./components/Signup";
import Navigation from "./components/Navigation";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Navigation />
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/projects" element={
              <ProtectedRoute>
                <ProjectList />
              </ProtectedRoute>
            } />
            <Route path="/create-project" element={
              <ProtectedRoute>
                <CreateProject />
              </ProtectedRoute>
            } />
            <Route path="/" element={<Navigate to="/projects" />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;