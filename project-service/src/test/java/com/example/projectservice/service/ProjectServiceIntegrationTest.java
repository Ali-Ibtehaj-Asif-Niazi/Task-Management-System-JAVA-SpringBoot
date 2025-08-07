package com.example.projectservice.service;

import com.example.projectservice.model.Project;
import com.example.projectservice.repository.ProjectRepository;
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
public class ProjectServiceIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @BeforeEach
    public void setup() {
        projectRepository.deleteAll();
    }

    @Test
    public void testCreateProject_ValidUserId() {
        // Arrange
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setUserId("validUserId");

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Project.class)).thenReturn(Mono.just(new Project()));

        // Act
        Project createdProject = projectService.createProject(project);

        // Assert
        assertNotNull(createdProject.getId());
        assertEquals("Test Project", createdProject.getName());
        assertEquals("Test Description", createdProject.getDescription());
        assertEquals("validUserId", createdProject.getUserId());
    }

    @Test
    public void testCreateProject_InvalidUserId() {
        // Arrange
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setUserId("invalidUserId");

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Project.class)).thenReturn(Mono.error(new RuntimeException()));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> projectService.createProject(project));
    }

    @Test
    public void testGetAllProjects() {
        // Arrange
        Project project1 = new Project();
        project1.setName("Project 1");
        project1.setDescription("Description 1");
        project1.setUserId("user1");

        Project project2 = new Project();
        project2.setName("Project 2");
        project2.setDescription("Description 2");
        project2.setUserId("user2");

        projectRepository.save(project1);
        projectRepository.save(project2);

        // Act
        List<Project> projects = projectService.getAllProjects();

        // Assert
        assertEquals(2, projects.size());
        assertTrue(projects.stream().anyMatch(p -> p.getName().equals("Project 1")));
        assertTrue(projects.stream().anyMatch(p -> p.getName().equals("Project 2")));
    }

    @Test
    public void testGetProjectById_ExistingProject() {
        // Arrange
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setUserId("userId");

        Project savedProject = projectRepository.save(project);

        // Act
        Project retrievedProject = projectService.getProjectById(savedProject.getId());

        // Assert
        assertNotNull(retrievedProject);
        assertEquals(savedProject.getId(), retrievedProject.getId());
        assertEquals("Test Project", retrievedProject.getName());
        assertEquals("Test Description", retrievedProject.getDescription());
        assertEquals("userId", retrievedProject.getUserId());
    }

    @Test
    public void testGetProjectById_NonExistingProject() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> projectService.getProjectById("nonExistingId"));
    }
}