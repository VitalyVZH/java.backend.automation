package ru.vitalyvzh;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import ru.vitalyvzh.utils.ByteToBase64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Запрос существующего файла на сервере")
public class GetImageTests extends BaseTest {

    private String uploadedImageId;
    private String path;
    private String fileString;

    @BeforeEach
    @Step("Предварительная загрузка изображения")
    void beforeEach() {

        RestAssured.requestSpecification  = reqSpec;

        path = smallFile;
        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(path);

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
    @DisplayName("Запрос на получение избражения по imageHash")
    void getEmptyRequestTest() {

        RestAssured.requestSpecification  = reqSpec;

        given()
                .expect()
                .body("success", is(true))
                .body("data.link", is(notNullValue()))
                .when()
                .get(Endpoints.IMAGEHASH_REQUEST, properties.getProperty("image.hash"))
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
