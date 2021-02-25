package ru.vitalyvzh;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.vitalyvzh.dao.GetAccountResponse;
import ru.vitalyvzh.utils.RandomId;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Slf4j
@DisplayName("Авторизация на сервере")
public class GetAccountTests extends BaseTest {

    @Test
    @DisplayName("Позитивная проверка авторизации")
    void getAccountInfoPositiveTest() {

        RestAssured.responseSpecification = responseSpecification;
        RestAssured.requestSpecification = reqSpec;

        GetAccountResponse response = given()
                .log()
                .all()
                .when()
                .get(Endpoints.GET_ACCOUNT_REQUEST, username)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(GetAccountResponse.class);

        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getData().getUrl(), equalTo("testprogmath"));
    }

    @Test
    @DisplayName("Негативная проверка авторизации, с генерацией рандомного ID")
    void getAccountInfoNegativeTest() {

        RestAssured.requestSpecification  = requestWithoutAuth;

        given()
                .expect()
                .body("data.error", is("Authentication required"))
                .body("status", is(401))
                .when()
                .get(Endpoints.GET_ACCOUNT_REQUEST, RandomId.randomId())
                .prettyPeek();
    }
}