package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.client.CourierClient;
import org.example.data.TestData;
import org.example.model.Courier;
import org.example.model.CourierCredentials;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

@DisplayName("Создание курьера")
public class CourierCreateTest {

    private final CourierClient courierClient = new CourierClient();
    private Courier courier;

    @BeforeEach
    @Step("Подготовка тестовых данных для создания курьера")
    void setUp() {
        courier = TestData.createCourier();
    }

    @AfterEach
    @Step("Удаление курьера, если он был создан")
    void tearDown() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response response = courierClient.login(credentials);
        if (response.statusCode() == SC_NOT_FOUND) {
            return;
        }
        int courierId = response.then()
                .statusCode(SC_OK)
                .extract().path("id");
        courierClient.delete(courierId).then()
                .statusCode(SC_OK)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Создаем курьера со всеми полями")
    @Step("Проверка успешного создания курьера")
    void shouldCreateCourier() {
        courierClient.create(courier).then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Step("Проверка ошибки при дубле курьера")
    void shouldNotCreateDuplicateCourier() {
        courierClient.create(courier).then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        courierClient.create(courier).then()
                .statusCode(SC_CONFLICT)
                .body("message", startsWith("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Нельзя создать курьера с занятым логином но с другим паролем")
    @Step("Проверка уникальности логина курьера")
    void shouldNotCreateCourierWithExistingLogin() {
        courierClient.create(courier).then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
        Courier anotherCourier = new Courier(courier.getLogin(), TestData.WRONG_PASSWORD, courier.getFirstName());

        courierClient.create(anotherCourier).then()
                .statusCode(SC_CONFLICT)
                .body("message", startsWith("Этот логин уже используется"));
    }

    @ParameterizedTest(name = "Логин: {0}")
    @NullAndEmptySource
    @DisplayName("Нельзя создать курьера без логина или с пустым логином")
    @Step("Проверка созданяе курьера без логина")
    void shouldNotCreateCourierWithoutLogin(String login) {
        Courier invalidCourier = new Courier(login, courier.getPassword(), courier.getFirstName());

        courierClient.create(invalidCourier).then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @ParameterizedTest(name = "Пароль: {0}")
    @NullAndEmptySource
    @DisplayName("Нельзя создать курьера без пароля или с пустым паролем")
    @Step("Проверка создания курьера без пароля")
    void shouldNotCreateCourierWithoutPassword(String password) {
        Courier invalidCourier = new Courier(courier.getLogin(), password, courier.getFirstName());

        courierClient.create(invalidCourier).then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без обязательного поля firstName")
    @Step("Проверка создания курьера без имени")
    void shouldNotCreateCourierWithoutFirstName() {
        Courier invalidCourier = new Courier(courier.getLogin(), courier.getPassword(), null);

        courierClient.create(invalidCourier).then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
