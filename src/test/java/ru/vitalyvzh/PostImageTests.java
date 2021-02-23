package ru.vitalyvzh;

import io.qameta.allure.Step;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Загрузка изображений на сервер")
public class PostImageTests extends BaseTest {

    private String uploadedImageId;
    private String fileString;
    private String path;

    @Test
    @DisplayName("Загрузка минимального по размеру изображения c помощью Base64")
    void uploadSmallFileTest() {
        path = smallFile;
        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(path);

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
    @DisplayName("Загрузка не описанного в документации файла изображения c помощью Base64")
    void uploadBigFileTest() {
        path = bigIncorrectFile;
        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(path);

        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", fileString)
                .expect()
                .body("success", is(false))
                .body("status", is(400))
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
}