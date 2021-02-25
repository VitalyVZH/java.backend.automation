package ru.vitalyvzh;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import ru.vitalyvzh.dao.image.PostImageResponse;
import ru.vitalyvzh.utils.ByteToBase64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Загрузка изображений на сервер")
public class PostImageTests extends BaseTest {

    private String uploadedImageId;
    private String fileString;
    private String path;
    static RequestSpecification requestWithoutAuth;
    private String imageHash;

    @Test
    @DisplayName("Загрузка минимального по размеру изображения c помощью Base64")
    void uploadWithBase64SmallFileTest() {

        RestAssured.requestSpecification  = reqSpec;

        path = smallFile;
        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(path);

        PostImageResponse response = given()
                .multiPart("image", fileString)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(PostImageResponse.class);

        imageHash = response.getData().getId();
        assertThat(response.getStatus(), equalTo(200));
    }

    @Test
    @DisplayName("Загрузка не описанного в документации файла изображения c помощью Base64")
    void uploadWithBase64BigFileTest() {

        RestAssured.requestSpecification  = reqSpec;

        path = bigIncorrectFile;
        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(path);

        uploadedImageId = given()
                .multiPart("image", fileString)
                .expect()
                .body("success", is(false))
                .body("status", is(400))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

        imageHash = uploadedImageId;
    }

    @Test
    @DisplayName("Загрузка изображения c помощью ссылки на URL")
    void uploadWithUrlFileTest() {

        RestAssured.requestSpecification  = reqSpec;

        PostImageResponse response = given()
                .multiPart("image", testurl)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(PostImageResponse.class);

        imageHash = response.getData().getId();
        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getData().getWidth(), equalTo(480));
        assertThat(response.getData().getHeight(), equalTo(480));
    }

    @Test
    @DisplayName("Загрузка файла изображения не являющегося изображением c помощью Base64")
    void uploadNegativeFileTest() {

        RestAssured.requestSpecification  = reqSpec;

        path = brokenFile;
        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(path);

        uploadedImageId = given()
                .multiPart("image", fileString)
                .expect()
                .body("success", is(false))
                .body("status", is(400))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    @DisplayName("Загрузка изображения без авторизации")
    void uploadWithoutTokenTest() {

        RestAssured.requestSpecification  = requestWithoutAuth;

        given()
                .multiPart("image", testurl)
                .expect()
                .body("success", is(false))
                .body("status", is(401))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek();
    }


    @AfterEach
    @Step("Удаление файла изображения после теста")
    void tearDown() {

        RestAssured.requestSpecification  = reqSpec;

        if(uploadedImageId != null) {
            given()
                .when()
                .delete(Endpoints.IMAGEHASH_REQUEST, imageHash)
                .prettyPeek()
                .then()
                .statusCode(200);
        }
    }
}