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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static utils.Utils.randomInt;

public class DeleteCourierTest {

    private CourierClient courierClient;
    private Courier courier;
    private int courierId;


    @Before
    public void setUp() {
        courier = randomCourier();
        courierClient = new CourierClient();
        courierClient.create(courier);
        ValidatableResponse loginResponse = courierClient.login(credsFrom(courier));
        courierId = loginResponse.extract().path("id");
    }

    @Test
    @DisplayName("Delete courier with wrong id")
    @Description("Проверка удаления курьера с несуществующим id")
    public void DeleteCourierWithWrongIdTest(){

        ValidatableResponse Response = courierClient.delete(randomInt(200000,100000));

        assertThat("Статус код неверный при удалении курьера",
                Response.extract().statusCode(), equalTo(HttpStatus.SC_NOT_FOUND));

        assertThat("Неверное сообщение при успешной авторизации курьера",
                Response.extract().path("message"),equalTo("Курьера с таким id нет."));

    }

    @Test
    @DisplayName("Delete courier")
    @Description("Проверка удаления курьера")
    public void DeleteCourierTest(){

        ValidatableResponse Response = courierClient.delete(courierId);

        assertThat("Статус код неверный при удалении курьера",
                Response.extract().statusCode(), equalTo(HttpStatus.SC_OK));

        assertThat("Неверное сообщение при успешной авторизации курьера",
                Response.extract().path("ok"),equalTo(true));

    }


    @After
    public void tearDown() {

        courierClient.delete(courierId);
    }
}
