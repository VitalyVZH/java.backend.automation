package ru.vitalyvzh;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class GetAccountTests extends BaseTest {

    @DisplayName("Позитивная проверка авторизации")
    @Test
    void getAccountInfoPositiveTest () {
        given()
                .headers("Authorization", token)
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
                .headers("Authorization", token)
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
                .headers("Authorization", token)
                .when()
                .get("/account/{username}", username)
                .then()
                .statusCode(400);
    }
}
