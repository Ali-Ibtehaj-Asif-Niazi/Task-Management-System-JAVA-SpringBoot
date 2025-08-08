package com.example.userservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.example.userservice.model.User;

import java.util.Map;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    public void cleanUp() {
        mongoTemplate.dropCollection(User.class);
    }

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void testValidateUserWithCorrectCredentials() {
        // First, create a user (POST /users)
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("name", "Login User", "email", "login@example.com", "password", "12345"))
                .when()
                    .post("/users")
                .then()
                    .statusCode(201);

        // Now validate the user (POST /auth/validate)
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", "login@example.com", "password", "12345"))
                .when()
                    .post("/auth/validate")
                .then()
                    .statusCode(200)
                    .body("status", equalTo("VALID"))
                    .body("email", equalTo("login@example.com"));
    }

    @Test
    public void testValidateUserWithInvalidCredentials() {
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", "nonexistent@example.com", "password", "wrong"))
                .when()
                    .post("/auth/validate")
                .then()
                    .statusCode(401)
                    .body("status", equalTo("INVALID"));
    }
}
