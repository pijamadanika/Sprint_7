package org.example;

import io.qameta.allure.Step;
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
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@DisplayName("Авторизация курьера")
public class CourierLoginTest {

    private final CourierClient courierClient = new CourierClient();
    private Courier courier;
    private Integer courierId;

    @BeforeEach
    @Step("Создание курьера для проверки авторизации")
    void setUp() {
        courier = TestData.createCourier();
        courierClient.create(courier).then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        courierId = courierClient.login(credentials).then()
                .statusCode(SC_OK)
                .extract().path("id");
    }

    @AfterEach
    @Step("Удаление курьера после проверки авторизации")
    void tearDown() {
        if (courierId != null) {
            courierClient.delete(courierId).then()
                    .statusCode(SC_OK)
                    .body("ok", equalTo(true));
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться и получает свой id")
    @Step("Проверка успешной авторизации курьера")
    void shouldLoginCourier() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());

        courierClient.login(credentials).then()
                .statusCode(SC_OK)
                .body("id", greaterThan(0))
                .body("id", equalTo(courierId));
    }

    @ParameterizedTest(name = "Логин: {0}")
    @NullAndEmptySource
    @DisplayName("Нельзя авторизоваться без логина или с пустым логином")
    @Step("Проверка авторизации без логина")
    void shouldNotLoginWithoutLogin(String login) {
        CourierCredentials credentials = new CourierCredentials(login, courier.getPassword());

        courierClient.login(credentials).then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @ParameterizedTest(name = "Пароль: {0}")
    @NullAndEmptySource
    @DisplayName("Нельзя авторизоваться без пароля или с пустым паролем")
    @Step("Проверка авторизации без пароля")
    void shouldNotLoginWithoutPassword(String password) {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), password);

        courierClient.login(credentials).then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться с неверным логином")
    @Step("Проверка авторизации с неверным логином")
    void shouldNotLoginWithWrongLogin() {
        String wrongLogin = TestData.createCourier().getLogin();
        CourierCredentials credentials = new CourierCredentials(wrongLogin, courier.getPassword());

        courierClient.login(credentials).then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться с неверным паролем")
    @Step("Проверка авторизации с неверным паролем")
    void shouldNotLoginWithWrongPassword() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), TestData.WRONG_PASSWORD);

        courierClient.login(credentials).then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Несуществующий курьер не может авторизоваться")
    @Step("Проверка авторизации удалённого курьера")
    void shouldNotLoginNonexistentCourier() {
        courierClient.delete(courierId).then()
                .statusCode(SC_OK)
                .body("ok", equalTo(true));
        courierId = null;
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());

        courierClient.login(credentials).then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
