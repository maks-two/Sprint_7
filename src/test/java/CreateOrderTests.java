import data.Orders;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.OrderClient;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static order.OrderGenerator.randomOrder;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class CreateOrderTests {

    private OrderClient orderClient;
    private Orders orders;

    private List<String> colour;

    private int trackId;

    @Before
    public void setup(){
        orderClient = new OrderClient();
    }

    public CreateOrderTests(List<String> colour) {
        this.colour = colour;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("GREY","BLACK")},
                {List.of()},
        };
    }

    @Test
    @DisplayName("Create order")
    @Description("Проверка возможности создания заказа c выбором цвета")
    public void CreateOrderTest(){

        orders = randomOrder();

        orders.setColor(colour);

        ValidatableResponse response = orderClient.create(orders);

        trackId = response.extract().path("track");

        assertThat("Статус код неверный при создании заказа",
                response.extract().statusCode(), equalTo(HttpStatus.SC_CREATED));

        assertThat("Неверное сообщение при создании заказа",
                response.extract().path("track"),instanceOf(Integer.class));

    }

    @After
    public void tearDown() {

        orderClient.delete(trackId);

    }
}
