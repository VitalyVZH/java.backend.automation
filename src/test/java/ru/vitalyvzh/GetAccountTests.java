package ru.vitalyvzh;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.vitalyvzh.dao.GetAccountResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Slf4j
@DisplayName("Авторизация на сервере")
public class GetAccountTests extends BaseTest {

    static RequestSpecification requestWithoutAuth;


//    @BeforeAll
//    static void withoutAuthSpec() {
//
//        requestWithoutAuth = new RequestSpecBuilder()
//                .addHeader("Autorization", "")
//                .build();
//    }


//    @BeforeEach
//    void setUp() {
////        deleteHash = uploadCommonImage().getData().getDeletehash();
//        RestAssured.responseSpecification = responseSpecification;
//        RestAssured.requestSpecification = reqSpec;
//    }

    @Test
    @DisplayName("Позитивная проверка авторизации")
    void getAccountInfoPositiveTest() {

        RestAssured.responseSpecification = responseSpecification;
        RestAssured.requestSpecification = reqSpec;

        GetAccountResponse response = given()
//                .spec(reqSpec)
//                .headers("Authorization", token)
                .log()
                .all()
                .when()
                .get(Endpoints.GET_ACCOUNT_REQUEST, username)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(GetAccountResponse.class);
//                .statusCode(200);
//                .spec(responseSpecification);

        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getData().getUrl(), equalTo("testprogmath"));
    }

    @Test
    @DisplayName("Негативная проверка авторизации")
    void getAccountInfoNegativeTest() {

        requestWithoutAuth = new RequestSpecBuilder()
                .addHeader("Autorization", "")
                .build();

        GetAccountResponse response = given()
//                .spec(requestWithoutAuth)
//                .headers("Authorization", token)
                .when()
                .get(Endpoints.GET_ACCOUNT_REQUEST, username)
                .then()
                .extract()
                .body()
                .as(GetAccountResponse.class);

        assertThat(response.getStatus(), equalTo(400));
//        assertThat(response.getData().getUrl(), equalTo("testprogmath"));
        assertThat(response.getSuccess(), equalTo(false));
    }

    @Test
    @DisplayName("Запрос на получение избражения без указания ID")
    void getEmptyRequestTest() {

        requestWithoutAuth = new RequestSpecBuilder()
                .addHeader("Autorization", "")
                .build();

        GetAccountResponse response = given()
//                .header("Autorization", token)
                .expect()
//                .body("data.error", is("A username is required."))
//                .body("status", is(400))
                .when()
                .get(Endpoints.GET_ACCOUNT_WITHOUT_USERNAME_REQUEST)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(GetAccountResponse.class);

        assertThat(response.getStatus(), equalTo(400));
    }
}
