package com.example.resourceserverdemo;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import io.restassured.http.ContentType;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yaml")
@Testcontainers
class ResourceServerDemoApplicationTests {

    @LocalServerPort
    int port;

    private String token;

    @Container
    public static DockerComposeContainer keycloak = new DockerComposeContainer(
            new File("docker-compose.yaml"))
            .withExposedService("keycloak", 8080)
            .waitingFor("keycloak",
                    Wait.forHttp("/") // Keycloak's base path
                            .forPort(8080) // Keycloak's internal port
                            .forStatusCode(200)); // HTTP status code indicating success; // change to match your
                                                  // docker-compose file

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        this.token = getAccessToken("test", "test");
    }

    @Test
    void whenRegularUserAccessUserEndpoint_thenShouldReturnHelloRegularUser() {
        given()
                .auth().oauth2(this.token) // regular user token
                .contentType(ContentType.JSON)
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("<h2> Hello Regular User! </h2>"));
    }

     @Test
    void whenRegularUserAccessAdminEndpoint_thenShouldReturnForbidden() {
        given()
                .auth().oauth2(this.token) // user token
                .contentType(ContentType.JSON)
                .when()
                .get("/admin")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void whenRegularUserAccessModeratorEndpoint_thenShouldReturnForbidden() {
        given()
                .auth().oauth2(this.token) // user token
                .contentType(ContentType.JSON)
                .when()
                .get("/moderator")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void whenAdminUserAccessUserEndpoint_thenShouldReturnHelloRegularUser() {
        given()
                .auth().oauth2(getAccessToken("test2", "test")) // admin token
                .contentType(ContentType.JSON)
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("<h2> Hello Regular User! </h2>"));
    }

    @Test
    void whenAdminUserAccessAdminEndpoint_thenShouldReturnHelloAdminUser() {
        given()
                .auth().oauth2(getAccessToken("test2", "test")) // admin token
                .contentType(ContentType.JSON)
                .when()
                .get("/admin")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("<h2> Hello Admin! </h2>"));
    }


    @Test
    void whenModeratorUserAccessUserEndpoint_thenShouldReturnHelloRegularUser() {
        given()
                .auth().oauth2(getAccessToken("test3", "test")) // moderator token
                .contentType(ContentType.JSON)
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("<h2> Hello Regular User! </h2>"));
    }

    @Test
    void whenModeratorUserAccessAdminEndpoint_thenShouldReturnForbidden() {
        given()
                .auth().oauth2(getAccessToken("test3", "test")) // moderator token
                .contentType(ContentType.JSON)
                .when()
                .get("/admin")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void whenModeratorUserAccessModeratorEndpoint_thenShouldReturnHelloModerator() {
        given()
                .auth().oauth2(getAccessToken("test3", "test")) // moderator token
                .contentType(ContentType.JSON)
                .when()
                .get("/moderator")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("<h2> Hello Moderator! </h2>"));
    }

    private String getAccessToken(String username, String password) {
    final String KEYCLOAK_SERVER_URL = String.format(
            "http://localhost:%s/realms/itemis/protocol/openid-connect/token",
            keycloak.getServicePort("keycloak", 8080));
    final String CLIENT_ID = "demo";

    return RestAssured.given()
            .baseUri(KEYCLOAK_SERVER_URL)
            .contentType("application/x-www-form-urlencoded")
            .formParam("grant_type", "password")
            .formParam("client_id", CLIENT_ID)
            .formParam("client_secret", "LbEvrQ9aA5YR8hyNUesQrpwVYScRfmoe")
            .formParam("scope", "openid")
            .formParam("username", username)
            .formParam("password", password)
            .when()
            .post()
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .extract().path("access_token");
}


}
