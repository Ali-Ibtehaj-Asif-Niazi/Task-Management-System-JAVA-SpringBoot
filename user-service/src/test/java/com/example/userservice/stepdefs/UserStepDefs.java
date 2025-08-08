package com.example.userservice.stepdefs;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserStepDefs {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private ResponseEntity<String> response;
    private Map<String, String> requestPayload;

    // Clean the database after each scenario to ensure test isolation
    @After
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Given("I want to create a new user with the following details:")
    public void iWantToCreateANewUserWithTheFollowingDetails(io.cucumber.datatable.DataTable dataTable) {
        requestPayload = dataTable.asMaps().get(0);
    }

    @Given("a user with email {string} and password {string} is already registered")
    public void aUserWithEmailAndPasswordIsAlreadyRegistered(String email, String password) {
        User user = new User();
        user.setName(email.split("@")[0]); // Simple name generation
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @When("I send a POST request to {string} to register")
    public void iSendAPOSTRequestToToRegister(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestPayload, headers);
        response = testRestTemplate.postForEntity(path, entity, String.class);
    }
    
    @When("I send a POST request to {string} with email {string} and password {string}")
    public void iSendAPOSTRequestToWithEmailAndPassword(String path, String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(credentials, headers);
        response = testRestTemplate.postForEntity(path, entity, String.class);
    }
    
    @When("I send a GET request to retrieve the user with email {string}")
    public void iSendAGETRequestToRetrieveTheUserWithEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Test setup error: User not found for GET request"));
        String path = "/users/" + user.getId();
        response = testRestTemplate.getForEntity(path, String.class);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        assertEquals(statusCode, response.getStatusCode().value());
    }

    @And("the response body should contain the email {string}")
    public void theResponseBodyShouldContainTheEmail(String email) throws JsonProcessingException {
        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
        assertEquals(email, responseBody.get("email"));
    }

    @And("the user {string} should exist in the database with a hashed password")
    public void theUserShouldExistInTheDatabaseWithAHashedPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        assertTrue(userOptional.isPresent(), "User should exist in the database");
        User user = userOptional.get();
        // The password from the original request
        String plainPassword = requestPayload.get("password");
        assertNotEquals(plainPassword, user.getPassword(), "Password should be hashed");
        assertTrue(passwordEncoder.matches(plainPassword, user.getPassword()), "Plain password should match the hashed password");
    }

    @And("the response body should contain the message {string}")
    public void theResponseBodyShouldContainTheMessage(String message) {
        assertTrue(response.getBody().contains(message));
    }

    @And("the validation response should have a status of {string}")
    public void theValidationResponseShouldHaveAStatusOf(String status) throws JsonProcessingException {
        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
        assertEquals(status, responseBody.get("status"));
    }

    @And("the validation response should contain the email {string}")
    public void theValidationResponseShouldContainTheEmail(String email) throws JsonProcessingException {
        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
        assertEquals(email, responseBody.get("email"));
    }

    @And("the response body should contain the user name {string} and email {string}")
    public void theResponseBodyShouldContainTheUserNameAndEmail(String name, String email) throws JsonProcessingException {
        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
        assertEquals(name, responseBody.get("name"));
        assertEquals(email, responseBody.get("email"));
    }
}