package com.example.projectservice.service;

import com.example.projectservice.model.Project;
import com.example.projectservice.repository.ProjectRepository;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

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

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository, webClientBuilder);
    }

    @Test
    void createProject_withValidUserId_shouldSaveProject() {
        // Arrange
        Project project = new Project();
        project.setUserId("validUserId");

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Project.class)).thenReturn(Mono.just(new Project()));

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Act
        Project savedProject = projectService.createProject(project);

        // Assert
        assertNotNull(savedProject);
        verify(projectRepository).save(project);
    }

    @Test
    void createProject_withInvalidUserId_shouldThrowException() {
        // Arrange
        Project project = new Project();
        project.setUserId("invalidUserId");

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Project.class)).thenReturn(Mono.error(new RuntimeException()));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> projectService.createProject(project));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getAllProjects_shouldReturnListOfProjects() {
        // Arrange
        List<Project> projects = Arrays.asList(new Project(), new Project());
        when(projectRepository.findAll()).thenReturn(projects);

        // Act
        List<Project> result = projectService.getAllProjects();

        // Assert
        assertEquals(projects, result);
        verify(projectRepository).findAll();
    }

    @Test
    void getProjectById_withValidId_shouldReturnProject() {
        // Arrange
        String id = "validId";
        Project project = new Project();
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));

        // Act
        Project result = projectService.getProjectById(id);

        // Assert
        assertEquals(project, result);
        verify(projectRepository).findById(id);
    }

    @Test
    void getProjectById_withInvalidId_shouldThrowException() {
        // Arrange
        String id = "invalidId";
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> projectService.getProjectById(id));
        verify(projectRepository).findById(id);
    }
}