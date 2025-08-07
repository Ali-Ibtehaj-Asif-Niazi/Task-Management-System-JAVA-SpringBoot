package com.example.taskservice.service;

import com.example.taskservice.model.Task;
import com.example.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @BeforeEach
    public void setup() {
        taskRepository.deleteAll();
    }

    private void mockWebClientForSuccess() {
        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Task.class)).thenReturn(Mono.just(new Task()));
    }

    private void mockWebClientForFailure() {
        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Task.class)).thenReturn(Mono.error(new RuntimeException()));
    }

    @Test
    public void testCreateTask_ValidUserIdAndProjectId() {
        // Arrange
        mockWebClientForSuccess();

        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setProjectId("validProjectId");
        task.setUserId("validUserId");

        // Act
        Task createdTask = taskService.createTask(task);

        // Assert
        assertNotNull(createdTask.getId());
        assertEquals("Test Task", createdTask.getTitle());
        assertEquals("Test Description", createdTask.getDescription());
        assertEquals("validProjectId", createdTask.getProjectId());
        assertEquals("validUserId", createdTask.getUserId());
    }

    @Test
    public void testCreateTask_InvalidProjectId() {
        // Arrange
        mockWebClientForFailure();

        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setProjectId("invalidProjectId");
        task.setUserId("validUserId");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    public void testCreateTask_InvalidUserId() {
        // Arrange
        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Task.class))
                .thenReturn(Mono.just(new Task()))  // First call (project validation) succeeds
                .thenReturn(Mono.error(new RuntimeException()));  // Second call (user validation) fails

        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setProjectId("validProjectId");
        task.setUserId("invalidUserId");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    public void testGetAllTasks() {
        // Arrange
        mockWebClientForSuccess();

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setProjectId("project1");
        task1.setUserId("user1");

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setProjectId("project2");
        task2.setUserId("user2");

        taskService.createTask(task1);
        taskService.createTask(task2);

        // Act
        List<Task> tasks = taskService.getAllTasks();

        // Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task 1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task 2")));
    }

    @Test
    public void testGetTaskById_ExistingTask() {
        // Arrange
        mockWebClientForSuccess();

        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setProjectId("projectId");
        task.setUserId("userId");

        Task savedTask = taskService.createTask(task);

        // Act
        Task retrievedTask = taskService.getTaskById(savedTask.getId());

        // Assert
        assertNotNull(retrievedTask);
        assertEquals(savedTask.getId(), retrievedTask.getId());
        assertEquals("Test Task", retrievedTask.getTitle());
        assertEquals("Test Description", retrievedTask.getDescription());
        assertEquals("projectId", retrievedTask.getProjectId());
        assertEquals("userId", retrievedTask.getUserId());
    }

    @Test
    public void testGetTaskById_NonExistingTask() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.getTaskById("nonExistingId"));
    }

    @Test
    public void testGetTasksByProjectId() {
        // Arrange
        mockWebClientForSuccess();

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setProjectId("project1");
        task1.setUserId("user1");

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setProjectId("project1");
        task2.setUserId("user2");

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setDescription("Description 3");
        task3.setProjectId("project2");
        task3.setUserId("user1");

        taskService.createTask(task1);
        taskService.createTask(task2);
        taskService.createTask(task3);

        // Act
        List<Task> tasksForProject1 = taskService.getTasksByProjectId("project1");

        // Assert
        assertEquals(2, tasksForProject1.size());
        assertTrue(tasksForProject1.stream().allMatch(t -> t.getProjectId().equals("project1")));
        assertTrue(tasksForProject1.stream().anyMatch(t -> t.getTitle().equals("Task 1")));
        assertTrue(tasksForProject1.stream().anyMatch(t -> t.getTitle().equals("Task 2")));
    }
}