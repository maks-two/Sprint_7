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

public class AcceptTheOrderTest {
    private CourierClient courierClient;
    private Courier courier;

    private int courierId;
    private OrderClient orderClient;
    private Orders orders;
    private int trackId;
    private int orderId;

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

        ValidatableResponse GetOrderResponse = orderClient.get_track(String.valueOf(trackId));
        orderId = GetOrderResponse.extract().path("order.id");
    }
    @Test
    @DisplayName("Accept order")
    @Description("Проверка принятия заказа")
    public void getListOrdersTest(){

        ValidatableResponse response = orderClient.acceptOrder(String.valueOf(orderId),String.valueOf(courierId));

        assertThat("Статус код неверный при принятии заказа",
                response.extract().statusCode(), equalTo(HttpStatus.SC_OK));

        assertThat("Неверное сообщение при принятии заказа",
                response.extract().path("ok"),equalTo(true));

    }

    @Test
    @DisplayName("Accept order without courierId")
    @Description("Проверка принятия заказа без id курьера")
    public void getListOrdersWithoutCourierIdTest(){

        ValidatableResponse response = orderClient.acceptOrder(String.valueOf(orderId),"");

        assertThat("Статус код неверный при принятии заказа",
                response.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));

        assertThat("Неверное сообщение при принятии заказа",
                response.extract().path("message"),equalTo("Недостаточно данных для поиска"));

    }

    @Test
    @DisplayName("Accept order with invalid courierId")
    @Description("Проверка принятия заказа c несуществующим id курьера")
    public void getListOrdersWithInvalidCourierIdTest(){

        ValidatableResponse response = orderClient.acceptOrder(String.valueOf(orderId),String.valueOf(randomInt(2000,1000)));

        assertThat("Статус код неверный при принятии заказа",
                response.extract().statusCode(), equalTo(HttpStatus.SC_NOT_FOUND));

        assertThat("Неверное сообщение при принятии заказа",
                response.extract().path("message"),equalTo("Курьера с таким id не существует"));

    }

    @Test
    @DisplayName("Accept order with invalid orderId")
    @Description("Проверка принятия заказа c несуществующим id заказа")
    public void getListOrdersWithInvalidOrderIdTest(){

        ValidatableResponse response = orderClient.acceptOrder(String.valueOf(-1),String.valueOf(courierId));

        assertThat("Статус код неверный при принятии заказа",
                response.extract().statusCode(), equalTo(HttpStatus.SC_NOT_FOUND));

        assertThat("Неверное сообщение при принятии заказа",
                response.extract().path("message"),equalTo("Заказа с таким id не существует"));

    }


    @After
    public void tearDown() {

        orderClient.delete(trackId);
        courierClient.delete(courierId);
    }
}
