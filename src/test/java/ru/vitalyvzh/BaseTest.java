package ru.vitalyvzh;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseTest {

    static Properties properties = new Properties();
    static String token;
    static String username;
    static String testurl;
    static String smallFile;
    static String bigIncorrectFile;
    static String brokenFile;
    static ResponseSpecification responseSpecification = null;
    static RequestSpecification reqSpec;
    static RequestSpecification requestWithoutAuth;

    @BeforeAll
    static void beforeAll() {

        loadProperties();

        token = properties.getProperty("token");
        username = properties.getProperty("username");
        testurl = properties.getProperty("test.url");
        smallFile = properties.getProperty("small.file");
        bigIncorrectFile = properties.getProperty("big.incorrect.file");
        brokenFile = properties.getProperty("broken.file");

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = properties.getProperty("base.url");
        RestAssured.filters(new AllureRestAssured());

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .expectHeader("Access-Control-Allow-Credentials", "true")
                .build();
//
        reqSpec = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .build();

        requestWithoutAuth = new RequestSpecBuilder()
                .addHeader("Autorization", "")
                .build();

//        RestAssured.responseSpecification  = responseSpecification;
//        RestAssured.requestSpecification  = reqSpec;
//        RestAssured.requestSpecification  = requestWithoutAuth;

    }

    static void loadProperties() {
        try (InputStream inputStream = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}