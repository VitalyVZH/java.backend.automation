package ru.vitalyvzh;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
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
    }

    static void loadProperties() {
        try (InputStream inputStream = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
