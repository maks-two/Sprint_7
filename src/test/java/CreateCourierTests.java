import courier.CourierClient;
import data.Courier;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static courier.CourierGenerator.randomCourier;
import static data.CourierCreds.credsFrom;
import static org.junit.Assert.assertEquals;

public class CreateCourierTests {

    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courier = randomCourier();
        courierClient = new CourierClient();
    }

    @Test
    @DisplayName("Create courier")
    @Description("Проверка возможности создания курьера")
    public void CourierCreateTest(){

        ValidatableResponse response = courierClient.create(courier);

        assertEquals("Статус код неверный при создании курьера",
                HttpStatus.SC_CREATED, response.extract().statusCode());

        assertEquals("Неверное сообщение при успешном создании курьера",
                true,response.extract().path("ok"));

    }

    @Test
    @DisplayName("Create two same courier")
    @Description("Проверка возмоожности создания двух одинаковых курьеров")
    public void TwoSameCourierCreateTest(){

        ValidatableResponse responseFirst = courierClient.create(courier);

        assertEquals("Статус код неверный при создании курьера",
                HttpStatus.SC_CREATED, responseFirst.extract().statusCode());

        ValidatableResponse responseSecond = courierClient.create(courier);

        assertEquals("Статус код неверный при создании курьера c уже существующим логином",
                HttpStatus.SC_CONFLICT, responseSecond.extract().statusCode());


        assertEquals("Некорректное сообщение об ошибке при создании курьера с уже использованным логином ",
                "Этот логин уже используется. Попробуйте другой.",responseSecond.extract().path("message"));

    }

    @After
    public void tearDown() {

        ValidatableResponse loginResponse = courierClient.login(credsFrom(courier));
        courierId = loginResponse.extract().path("id");
        courierClient.delete(courierId);
    }

}