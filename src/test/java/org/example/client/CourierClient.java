package org.example.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.model.Courier;
import org.example.model.CourierCredentials;

public class CourierClient extends BaseClient {

    private static final String COURIER_PATH = "/courier";

    @Step("Создать курьера")
    public Response create(Courier courier) {
        return request()
                .body(courier)
                .post(COURIER_PATH);
    }

    @Step("Авторизоваться под курьером")
    public Response login(CourierCredentials credentials) {
        return request()
                .body(credentials)
                .post(COURIER_PATH + "/login");
    }

    @Step("Удалить тестового курьера с id: {id}")
    public Response delete(int id) {
        return request()
                .pathParam("id", id)
                .delete(COURIER_PATH + "/{id}");
    }
}
