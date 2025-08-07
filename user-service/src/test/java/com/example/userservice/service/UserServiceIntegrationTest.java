package com.example.userservice.service;

import com.example.userservice.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceIntegrationTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    public void cleanUp() {
        mongoTemplate.dropCollection(User.class);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        logger.info("Create user response: {}", response);
        logger.info("Create user response body: {}", response.getBody());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Test User", response.getBody().getName());
    }

    @Test
    public void testGetUser() {
        User user = new User();
        user.setName("Get Test User");
        user.setEmail("gettest@example.com");
        user.setPassword("password");
        ResponseEntity<User> createResponse = restTemplate.postForEntity("/users", user, User.class);

        logger.info("Create user response for get test: {}", createResponse);
        logger.info("Create user response body for get test: {}", createResponse.getBody());

        assertNotNull(createResponse.getBody(), "Created user should not be null");
        String userId = createResponse.getBody().getId();

        ResponseEntity<User> getResponse = restTemplate.getForEntity("/users/" + userId, User.class);

        logger.info("Get user response: {}", getResponse);
        logger.info("Get user response body: {}", getResponse.getBody());

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(userId, getResponse.getBody().getId());
        assertEquals("Get Test User", getResponse.getBody().getName());
    }

    @Test
    public void testCreateUserWithInvalidEmail() {
        User user = new User();
        user.setName("Invalid User");
        user.setEmail("invalid"); // Invalid email format
        user.setPassword("password");

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);

        logger.info("Invalid email response: {}", response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateUserWithExistingEmail() {
        User user1 = new User();
        user1.setName("Test User 1");
        user1.setEmail("test@example.com");
        user1.setPassword("password1");

        restTemplate.postForEntity("/users", user1, User.class);

        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test@example.com");
        user2.setPassword("password2");

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user2, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("Email already exists"));
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setName("Test User 1");
        user1.setEmail("test1@example.com");
        user1.setPassword("password1");

        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2.setPassword("password2");

        restTemplate.postForEntity("/users", user1, User.class);
        restTemplate.postForEntity("/users", user2, User.class);

        ResponseEntity<List<User>> response = restTemplate.exchange(
            "/users",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<User>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }
}