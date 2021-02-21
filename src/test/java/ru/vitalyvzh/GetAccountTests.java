package ru.vitalyvzh;

import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class GetAccountTests {

    static Properties properties = new Properties();
    static String token;
    static String username;
    static private Map<String, String> headers = new HashMap<>();

    @BeforeAll
    static void beforeAll() {

        loadProperties();

        token = properties.getProperty("token");
        username = properties.getProperty("username");

        headers.put("Authorization", token);
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = properties.getProperty("base.url");
    }

    @DisplayName("Позитивная проверка авторизации")
    @Test
    void getAccountInfoPositiveTest () {
        given()
                .headers(headers)
                .log()
                .all()
                .when()
                .get("/account/{username}", username)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @DisplayName("Позитивная расширенная проверка авторизации")
    @Test
    void getAccountInfoPositiveWithManyChecksTest () {
        given()
                .headers(headers)
                .expect()
                .body(CoreMatchers.containsString(username))
                .body("success", is(true))
                .when()
                .get("/account/{username}", username)
                .then()
                .statusCode(200);
    }

    @DisplayName("Негативная проверка авторизации")
    @Test
    void getAccountInfoNegativeTest () {
        given()
                .headers(headers)
                .when()
                .get("/account/{username}", username)
                .then()
                .statusCode(400);
    }

    private static void loadProperties() {
        try (InputStream inputStream = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
