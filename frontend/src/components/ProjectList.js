import { useEffect, useState } from "react";
import { useApi } from "../api";

export default function ProjectList() {
  const API = useApi();
  const [projects, setProjects] = useState([]);

  useEffect(() => {
    API.get("/projects")
      .then((res) => setProjects(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <div>
      <h2>Projects</h2>
      <ul>
        {projects.map((p) => (
          <li key={p.id}>{p.name} - {p.description}</li>
        ))}
      </ul>
    </div>
  );
}
