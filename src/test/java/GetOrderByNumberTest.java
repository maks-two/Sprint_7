import courier.CourierClient;
import data.Courier;
import data.Orders;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.OrderClient;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static courier.CourierGenerator.randomCourier;
import static data.CourierCreds.credsFrom;
import static order.OrderGenerator.randomOrder;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static utils.Utils.randomInt;

public class GetOrderByNumberTest {

    private CourierClient courierClient;
    private Courier courier;
    private OrderClient orderClient;
    private Orders orders;
    private int trackId;
    private int courierId;

    @Before
    public void setup(){

        courier = randomCourier();
        courierClient = new CourierClient();
        courierClient.create(courier);

        ValidatableResponse loginResponse = courierClient.login(credsFrom(courier));
        courierId = loginResponse.extract().path("id");

        orderClient = new OrderClient();
        orders = randomOrder();
        trackId = orderClient.create(orders).extract().path("track");

    }

    @Test
    @DisplayName("Get order by Id")
    @Description("Проверка получения заказа по его номеру")
    public void getOrderByNumberTest(){

        ValidatableResponse getOrderResponse = orderClient.get_track(String.valueOf(trackId));


        assertThat("Статус код неверный при получении заказа по его номеру",
                getOrderResponse.extract().statusCode(), equalTo(HttpStatus.SC_OK));

        assertThat("Неверное сообщение при получении заказа по его номеру",
                getOrderResponse.extract().path("order"),notNullValue());

    }

    @Test
    @DisplayName("Get order by invalid Id")
    @Description("Проверка получения заказа по несуществующему номеру")
    public void getOrderByInvalidNumberTest(){

        ValidatableResponse getOrderResponse = orderClient.get_track(String.valueOf(randomInt(200000,500)));


        assertThat("Статус код неверный при запросе с несуществующим номером заказа",
                getOrderResponse.extract().statusCode(), equalTo(HttpStatus.SC_NOT_FOUND));

        assertThat("Неверное сообщение при запросе с несуществующим номером заказа",
                getOrderResponse.extract().path("message"),equalTo("Заказ не найден"));

    }

    @Test
    @DisplayName("Get order Without Id")
    @Description("Проверка получения заказа без номера")
    public void getOrderWithoutNumberTest(){

        ValidatableResponse getOrderResponse = orderClient.get_track("");


        assertThat("Статус код неверный при запросе заказа без номера",
                getOrderResponse.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));

        assertThat("Неверное сообщение при запросе заказа без номера",
                getOrderResponse.extract().path("message"),equalTo("Недостаточно данных для поиска"));

    }

    @After
    public void tearDown() {

        orderClient.delete(trackId);
        courierClient.delete(courierId);
    }
}
