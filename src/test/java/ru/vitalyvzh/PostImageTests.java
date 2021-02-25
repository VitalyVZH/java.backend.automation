package ru.vitalyvzh;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import ru.vitalyvzh.dao.GetAccountResponse;
import ru.vitalyvzh.dao.image.PostImageResponse;

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

//    @BeforeAll
//    static void withoutAuthSpec() {
//
//        requestWithoutAuth = new RequestSpecBuilder()
//                .addHeader("Autorization", "")
//                .build();
//    }

//    @BeforeEach
//    void setUp() {
//        RestAssured.requestSpecification  = reqSpec;
//    }

    @Test
    @DisplayName("Загрузка минимального по размеру изображения c помощью Base64")
    void uploadWithBase64SmallFileTest() {

        RestAssured.requestSpecification  = reqSpec;

        path = smallFile;
        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(path);

        PostImageResponse response = given()
//                .headers("Authorization", token)
                .multiPart("image", fileString)
//                .expect()
//                .body("success", is(true))
//                .body("data.id", is(notNullValue()))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
//                .contentType("application/json")
//                .extract()
//                .response()
//                .jsonPath()
//                .getString("data.deletehash");
                .extract()
                .body()
                .as(PostImageResponse.class);

        imageHash = response.getData().getId();
//        assertThat(response.);
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
//                .headers("Authorization", token)
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
    @DisplayName("Загрузка изображения c помощью ссылки на URL")
    void uploadWithUrlFileTest() {

        RestAssured.requestSpecification  = reqSpec;

        uploadedImageId = given()
//                .headers("Authorization", token)
                .multiPart("image", testurl)
                .expect()
                .body("status", is(200))
                .body("data.id", is(notNullValue()))
                .body("data.width", is(480))
                .body("data.height", is(480))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
//        AssertThat()
    }

    @Test
    @DisplayName("Загрузка файла изображения не являющегося изображением c помощью Base64")
    void uploadNegativeFileTest() {

        RestAssured.requestSpecification  = reqSpec;

        path = brokenFile;
        ByteToBase64 byteToBase = new ByteToBase64();
        fileString = byteToBase.fileString(path);

        uploadedImageId = given()
//                .headers("Authorization", token)
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

        requestWithoutAuth = new RequestSpecBuilder()
                .addHeader("Autorization", "")
                .build();

        given()
//                .headers("Authorization", token)
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
//                .headers("Authorization", token)
                .when()
                .delete(Endpoints.POST_IMAGEHASH_REQUEST, imageHash)
//                .delete(Endpoints.POST_IMAGEHASH_REQUEST, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
        }
    }
}