import data.Orders;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.OrderClient;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static order.OrderGenerator.randomOrder;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ListOrderTest {

    private OrderClient orderClient;
    private Orders orders;
    private int trackId;

    @Before
    public void setup(){
        orderClient = new OrderClient();
        orders = randomOrder();
        trackId = orderClient.create(orders).extract().path("track");
    }

    @Test
    @DisplayName("Get list order")
    @Description("Проверка получения списка заказов")
    public void getListOrdersTest(){

        ValidatableResponse response = orderClient.get();

        assertThat("Статус код неверный при получении списка заказов",
                response.extract().statusCode(), equalTo(HttpStatus.SC_OK));

        assertThat("Список заказов пустой",
                response.extract().path("orders"),notNullValue());

    }

    @After
    public void tearDown() {

        orderClient.delete(trackId);

    }
}
