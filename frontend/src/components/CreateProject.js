import { useState, useContext } from "react";
import { useApi } from "../api";
import { AuthContext } from "../context/AuthContext";
import styles from "./CreateProject.module.css";

export default function CreateProject() {
  const API = useApi();
  const { user } = useContext(AuthContext);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    if (!name.trim() || !description.trim()) {
      setError("Please fill in all fields");
      setIsLoading(false);
      return;
    }

    if (!user || !user._id) {
      setError("User information is missing. Please log in again.");
      setIsLoading(false);
      return;
    }

    try {
      const projectData = {
        name,
        description,
        userId: user._id  // Use the user's ObjectId
      };

      console.log("Sending project data:", projectData);

      const response = await API.post("/projects", projectData);
      console.log("Project created:", response.data);
      alert("Project created successfully");
      setName("");
      setDescription("");
    } catch (err) {
      console.error("Error creating project:", err.response?.data || err.message);
      setError(err.response?.data?.message || "Error creating project. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>Create Project</h2>
      <form onSubmit={handleSubmit} className={styles.form}>
        <input
          type="text"
          placeholder="Project name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className={styles.input}
          required
        />
        <textarea
          placeholder="Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className={styles.textarea}
          required
        />
        {error && <p className={styles.error}>{error}</p>}
        <button type="submit" className={styles.button} disabled={isLoading}>
          {isLoading ? "Creating..." : "Create Project"}
        </button>
      </form>
    </div>
  );
}