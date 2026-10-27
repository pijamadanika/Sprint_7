package org.example;

import io.qameta.allure.Step;
import org.example.client.OrderClient;
import org.example.data.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

@DisplayName("Получение списка заказов")
public class OrderListTest {

    private final OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("В ответе возвращается список заказов и данные страницы")
    @Step("Проверка получения списка заказов")
    void shouldReturnOrdersList() {
        orderClient.getList(TestData.ORDERS_LIMIT, TestData.FIRST_PAGE).then()
                .statusCode(SC_OK)
                .body("orders", instanceOf(List.class))
                .body("pageInfo.page", equalTo(TestData.FIRST_PAGE))
                .body("pageInfo.limit", equalTo(TestData.ORDERS_LIMIT));
    }
}
