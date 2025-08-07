package com.example.taskservice.service;

import com.example.taskservice.model.Task;
import com.example.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, webClientBuilder);
    }

    @Test
    void createTask_withValidProjectIdAndUserId_shouldSaveTask() {
        // Arrange
        Task task = new Task();
        task.setProjectId("validProjectId");
        task.setUserId("validUserId");

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Task.class)).thenReturn(Mono.just(new Task()));

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        Task savedTask = taskService.createTask(task);

        // Assert
        assertNotNull(savedTask);
        verify(taskRepository).save(task);
    }

    @Test
    void createTask_withInvalidProjectId_shouldThrowException() {
        // Arrange
        Task task = new Task();
        task.setProjectId("invalidProjectId");
        task.setUserId("validUserId");

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Task.class)).thenReturn(Mono.error(new RuntimeException()));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_withInvalidUserId_shouldThrowException() {
        // Arrange
        Task task = new Task();
        task.setProjectId("validProjectId");
        task.setUserId("invalidUserId");

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Task.class))
                .thenReturn(Mono.just(new Task()))  // For project validation
                .thenReturn(Mono.error(new RuntimeException()));  // For user validation

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getAllTasks_shouldReturnListOfTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(new Task(), new Task());
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertEquals(tasks, result);
        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_withValidId_shouldReturnTask() {
        // Arrange
        String id = "validId";
        Task task = new Task();
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        // Act
        Task result = taskService.getTaskById(id);

        // Assert
        assertEquals(task, result);
        verify(taskRepository).findById(id);
    }

    @Test
    void getTaskById_withInvalidId_shouldThrowException() {
        // Arrange
        String id = "invalidId";
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.getTaskById(id));
        verify(taskRepository).findById(id);
    }

    @Test
    void getTasksByProjectId_shouldReturnListOfTasks() {
        // Arrange
        String projectId = "projectId";
        List<Task> tasks = Arrays.asList(new Task(), new Task());
        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getTasksByProjectId(projectId);

        // Assert
        assertEquals(tasks, result);
        verify(taskRepository).findByProjectId(projectId);
    }
}