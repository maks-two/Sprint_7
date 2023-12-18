import data.Orders;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.OrderClient;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static order.OrderGenerator.randomOrder;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ListOrderTest {

    private OrderClient orderClient;
    private Orders orders;
    private int trackId;

    @Before
    public void setup() {
        orderClient = new OrderClient();
        orders = randomOrder();
    }
    @Test
    @DisplayName("Get list order")
    @Description("Проверка получения списка заказов")
    public void getListOrdersTest() {
        trackId = orderClient.create(orders).extract().path("track");

        ValidatableResponse response = orderClient.get();

        assertThat("Статус код неверный при получении списка заказов",
                response.extract().statusCode(), equalTo(HttpStatus.SC_OK));

        List<String> responseList = response.extract().path("orders");
        assertThat("Список заказов пустой",
                responseList.size() > 0 );

        assertThat("У заказа нет трэка",
                trackId , isA(Integer.class));
    }
    @After
    public void tearDown() {
        orderClient.delete(trackId);
    }
}
