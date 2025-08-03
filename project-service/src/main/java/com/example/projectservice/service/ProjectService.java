package com.example.projectservice.service;

import com.example.projectservice.model.Project;
import com.example.projectservice.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final WebClient.Builder webClientBuilder;

    public ProjectService(ProjectRepository projectRepository, WebClient.Builder webClientBuilder) {
        this.projectRepository = projectRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Project createProject(Project project) {
        // Validate userId
        Boolean userExists = webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/users/" + project.getUserId())
                .retrieve()
                .bodyToMono(Project.class)
                .map(u -> true)
                .onErrorReturn(false)
                .block();

        if (!userExists) {
            throw new RuntimeException("Invalid userId: " + project.getUserId());
        }

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
}
