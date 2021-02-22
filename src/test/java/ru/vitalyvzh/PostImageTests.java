package ru.vitalyvzh;

import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class PostImageTests extends BaseTest {

    static final String INPUT_IMAGE_FILE_PATH = "smile.jpg";
    private String uploadedImageId;
    private String fileString;

    // Кодируем в BASE64 массив байтов
    @BeforeEach
    void setUp() {
        byte[] fileContent = getFileContent();
        fileString = Base64.getEncoder().encodeToString(fileContent);
    }

    @Test
    @DisplayName("Загрузка минимального по размеру изображения c помощью Base64")
    void uploadSmallFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", fileString)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    @DisplayName("Загрузка изображения c помощью ссылки на URL")
    void uploadWithUrlFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", testurl)
                .expect()
                .body("status", is(200))
                .body("data.id", is(notNullValue()))
                .body("data.width", is(480))
                .body("data.height", is(480))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }


    @AfterEach
    @Step("Удаление файла изображения после текста")
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("/image/{imageHash}", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    // переводим файл в массив байтов
    private byte[] getFileContent() {
        ClassLoader classLoader = getClass().getClassLoader();
        File inputFile = new File(classLoader.getResource(INPUT_IMAGE_FILE_PATH).getFile());

        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }
}