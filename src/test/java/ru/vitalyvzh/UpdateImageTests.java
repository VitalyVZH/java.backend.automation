package ru.vitalyvzh;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import ru.vitalyvzh.utils.ByteToBase64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Обновление существующего файла на сервере")
public class UpdateImageTests extends BaseTest {

    private String uploadedImageId;
    private String path;
    private String fileString;

    @BeforeEach
    @Step("Предварительная загрузка изображения")
    void beforeEach() {

        RestAssured.requestSpecification  = reqSpec;

        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(Images.SMALL_SIZE.path);

        uploadedImageId = given()
                .multiPart("image", fileString)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .then()
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
        properties.setProperty("image.hash", uploadedImageId);
    }

    @Test
    @DisplayName("Обновление файла")
    void updateImageTest() {

        RestAssured.requestSpecification  = reqSpec;

        given()
                .multiPart("image", Images.POSITIVE_URL)
                .expect()
                .body("success", is(true))
                .body("status", is(200))
                .when()
                .post(Endpoints.IMAGEHASH_REQUEST, properties.getProperty("image.hash"))
                .prettyPeek();
    }

    @AfterEach
    @Step("Удаление файла изображения после теста")
    void tearDown() {

        RestAssured.requestSpecification  = reqSpec;

        given()
                .when()
                .delete(Endpoints.IMAGEHASH_REQUEST, properties.getProperty("image.hash"))
                .then()
                .statusCode(200);
    }
}