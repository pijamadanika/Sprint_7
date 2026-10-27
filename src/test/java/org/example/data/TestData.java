package org.example.data;

import io.qameta.allure.Step;
import org.example.model.Courier;
import org.example.model.Order;

import java.time.LocalDate;
import java.util.UUID;

public class TestData {

    public static final String WRONG_PASSWORD = "wrong_password";
    public static final int ORDERS_LIMIT = 5;
    public static final int FIRST_PAGE = 0;

    @Step("Подготовить курьера с уникальным логином")
    public static Courier createCourier() {
        String login = "courier" + UUID.randomUUID();
        return new Courier(login, "qwerty12345", "Daniyar");
    }

    @Step("Подготовить данные заказа")
    public static Order createOrder(String[] colors) {
        Order order = new Order();
        order.setFirstName("Данияр");
        order.setLastName("Ташен");
        order.setAddress("Шевченко 294, кв. 44");
        order.setMetroStation("Алмалы");
        order.setPhone("+7 777 123 45 67");
        order.setRentTime(1);
        order.setDeliveryDate(LocalDate.now().plusDays(2).toString());
        order.setComment("Заказ для Sprint_7");
        order.setColor(colors);
        return order;
    }
}
