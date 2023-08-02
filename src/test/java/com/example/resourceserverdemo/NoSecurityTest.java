package com.example.resourceserverdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import static io.restassured.RestAssured.given;

@ActiveProfiles(value = "no-security")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test-nosecurity.yaml")
public class NoSecurityTest {

    @LocalServerPort
    private int port;

    @Test
    public void testHelloEndpoint() {
        given()
            .port(port)
        .when()
            .get("/user") // Replace with the actual endpoint you want to test
        .then()
            .statusCode(200);
    }
    
}
