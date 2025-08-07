package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
void createUser_shouldEncodePasswordAndSaveUser() {
    // Arrange
    User user = new User();
    user.setEmail("test@example.com");
    user.setPassword("password");

    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    User savedUser = userService.createUser(user);

    // Assert
    assertNotEquals("password", savedUser.getPassword());
    assertTrue(new BCryptPasswordEncoder().matches("password", savedUser.getPassword()));
    verify(userRepository).save(user);
}

@Test
void getAllUsers_shouldReturnListOfUsers() {
    // Arrange
    User user1 = new User();
    User user2 = new User();
    when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

    // Act
    List<User> users = userService.getAllUsers();

    // Assert
    assertEquals(2, users.size());
    verify(userRepository).findAll();
}

@Test
void getUserById_shouldReturnUser_whenUserExists() {
    // Arrange
    String userId = "123";
    User user = new User();
    user.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // Act
    User foundUser = userService.getUserById(userId);

    // Assert
    assertEquals(userId, foundUser.getId());
    verify(userRepository).findById(userId);
}

@Test
void getUserById_shouldThrowException_whenUserDoesNotExist() {
    // Arrange
    String userId = "123";
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
    verify(userRepository).findById(userId);
}
}