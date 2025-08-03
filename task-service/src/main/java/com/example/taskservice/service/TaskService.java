package com.example.taskservice.service;

import com.example.taskservice.model.Task;
import com.example.taskservice.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final WebClient.Builder webClientBuilder;

    public TaskService(TaskRepository taskRepository, WebClient.Builder webClientBuilder) {
        this.taskRepository = taskRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Task createTask(Task task) {
        // Validate projectId
        Boolean projectExists = webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/projects/" + task.getProjectId())
                .retrieve()
                .bodyToMono(Task.class)
                .map(p -> true)
                .onErrorReturn(false)
                .block();

        if (!projectExists) {
            throw new RuntimeException("Invalid projectId: " + task.getProjectId());
        }

        // Validate userId
        Boolean userExists = webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/users/" + task.getUserId())
                .retrieve()
                .bodyToMono(Task.class)
                .map(u -> true)
                .onErrorReturn(false)
                .block();

        if (!userExists) {
            throw new RuntimeException("Invalid userId: " + task.getUserId());
        }

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
}
