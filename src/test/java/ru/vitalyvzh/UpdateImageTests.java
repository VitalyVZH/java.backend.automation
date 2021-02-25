package ru.vitalyvzh;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Обновление существующего файла на сервере")
public class UpdateImageTests extends BaseTest {

    private String uploadedImageId;
    private String path;
    private String fileString;
//    private String imageHash;
//    private String imageDeleteHash;

    @BeforeEach
    @Step("Предварительная загрузка изображения")
    void beforeEach() {

        RestAssured.requestSpecification  = reqSpec;

        path = smallFile;
        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(path);

        uploadedImageId = given()
//                .headers("Authorization", token)
                .multiPart("image", fileString)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
//                .prettyPeek()
                .then()
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
//                .getString("data.deletehash");
                .getString("data.id");
        properties.setProperty("image.hash", uploadedImageId);
    }

    @Test
    @DisplayName("Обновление файла")
    void updateImageTest() {
        given()
//                .headers("Authorization", token)
                .multiPart("image", testurl)
                .expect()
                .body("success", is(true))
                .when()
                .post(Endpoints.POST_IMAGEHASH_REQUEST, properties.getProperty("image.hash"))
                .prettyPeek();
    }
//
    @AfterEach
    @Step("Удаление файла изображения после теста")
    void tearDown() {
        given()
//                .headers("Authorization", token)
                .when()
                .delete(Endpoints.POST_IMAGEHASH_REQUEST, properties.getProperty("image.hash"))
//                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
