package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.client.OrderClient;
import org.example.data.TestData;
import org.example.model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@DisplayName("Создание заказа")
public class OrderCreateTest {

    private final OrderClient orderClient = new OrderClient();
    private Integer track;

    static Object[][] orderColors() {
        return new Object[][]{
                {"BLACK", new String[]{"BLACK"}},
                {"GREY", new String[]{"GREY"}},
                {"BLACK и GREY", new String[]{"BLACK", "GREY"}},
                {"пустой список цветов", new String[]{}},
                {"поле color отсутствует", null}
        };
    }

    @AfterEach
    @Step("Отмена созданного заказ")
    void tearDown() {
        if (track != null) {
            orderClient.cancel(track).then()
                    .statusCode(SC_OK)
                    .body("ok", equalTo(true));
        }
    }

    @ParameterizedTest(name = "Цвет: {0}")
    @MethodSource("orderColors")
    @DisplayName("Заказ можно создать с разными цветами и без цвета")
    @Step("Проверка создания заказа: {colorDescription}")
    void shouldCreateOrder(String colorDescription, String[] colors) {
        Order order = TestData.createOrder(colors);
        Response response = orderClient.create(order);

        track = response.path("track");

        response.then()
                .statusCode(SC_CREATED)
                .body("track", greaterThan(0));
    }
}
