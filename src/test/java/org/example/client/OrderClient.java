package org.example.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.model.Order;

public class OrderClient extends BaseClient {

    private static final String ORDERS_PATH = "/orders";

    @Step("Создать заказ")
    public Response create(Order order) {
        return request()
                .body(order)
                .post(ORDERS_PATH);
    }

    @Step("Получить список заказов: limit={limit}, page={page}")
    public Response getList(int limit, int page) {
        return request()
                .queryParam("limit", limit)
                .queryParam("page", page)
                .get(ORDERS_PATH);
    }

    @Step("Отменить тестовый заказ с track {track}")
    public Response cancel(int track) {
        return request()
                .queryParam("track", track)
                .put(ORDERS_PATH + "/cancel");
    }
}
