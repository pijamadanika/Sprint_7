package org.example.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseClient {

    private static final String BASE_URL = System.getProperty(
            "baseUrl", "https://qa-scooter.education-services.ru");
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 70000;

    protected RequestSpecification request() {
        return given()
                .baseUri(BASE_URL)
                .basePath("/api/v1")
                .contentType(ContentType.JSON)
                .filter(new AllureRestAssured())
                .config(RestAssuredConfig.config().httpClient(
                        HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", CONNECTION_TIMEOUT)
                                .setParam("http.socket.timeout", READ_TIMEOUT)));
    }
}
