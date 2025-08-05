import React, { useEffect, useState, useContext } from "react";
import { useApi } from "../api";
import { AuthContext } from "../context/AuthContext";
import CreateTask from "./CreateTask";
import styles from "./ProjectList.module.css";

export default function ProjectList() {
  const API = useApi();
  const { user } = useContext(AuthContext);
  const [projects, setProjects] = useState([]);
  const [expandedProject, setExpandedProject] = useState(null);

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      const res = await API.get("/projects");
      setProjects(res.data);
    } catch (err) {
      console.error("Error fetching projects:", err);
    }
  };

  const fetchTasks = async (projectId) => {
    try {
      const res = await API.get(`/tasks/project/${projectId}`);
      return res.data;
    } catch (err) {
      console.error("Error fetching tasks:", err);
      return [];
    }
  };

  const handleExpandProject = async (projectId) => {
    if (expandedProject === projectId) {
      setExpandedProject(null);
    } else {
      const tasks = await fetchTasks(projectId);
      setProjects(projects.map(p => 
        p.id === projectId ? { ...p, tasks } : p
      ));
      setExpandedProject(projectId);
    }
  };

  const handleTaskCreated = (projectId, newTask) => {
    setProjects(projects.map(p => 
      p.id === projectId 
        ? { ...p, tasks: [...(p.tasks || []), newTask] } 
        : p
    ));
  };

  return (
    <div className={styles.projectList}>
      <h2>Projects</h2>
      {projects.map((p) => (
        <div key={p.id} className={styles.projectItem}>
          <div 
            className={styles.projectHeader} 
            onClick={() => handleExpandProject(p.id)}
          >
            <span className={styles.projectName}>{p.name}</span>
            <span>{p.description}</span>
          </div>
          {expandedProject === p.id && (
            <div className={styles.taskList}>
              <h4>Tasks</h4>
              {p.tasks && p.tasks.length > 0 ? (
                <ul>
                  {p.tasks.map(task => (
                    <li key={task.id} className={styles.taskItem}>
                      <strong>{task.title}</strong> - {task.description}
                    </li>
                  ))}
                </ul>
              ) : (
                <p>No tasks for this project yet.</p>
              )}
              <div className={styles.createTaskForm}>
                <CreateTask 
                  projectId={p.id} 
                  onTaskCreated={(newTask) => handleTaskCreated(p.id, newTask)} 
                />
              </div>
            </div>
          )}
        </div>
      ))}
    </div>
  );
}