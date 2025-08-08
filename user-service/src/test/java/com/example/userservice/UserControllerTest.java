package com.example.userservice;

import com.example.userservice.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void testCreateUserSuccessfully() {
        User user = new User();
        user.setName("Ali Test");
        user.setEmail("ali@example.com");
        user.setPassword("test123");

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post("/users")
                .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo("Ali Test"))
                    .body("email", equalTo("ali@example.com"));
    }

    @Test
    public void testGetAllUsers() {
        RestAssured
                .when()
                    .get("/users")
                .then()
                    .statusCode(anyOf(is(200), is(204)));
    }
}
