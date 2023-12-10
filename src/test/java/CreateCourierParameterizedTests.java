import courier.CourierClient;
import data.Courier;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static data.CourierCreds.credsFrom;
import static org.junit.Assert.assertEquals;
import static utils.Utils.randomString;

@RunWith(Parameterized.class)
public class CreateCourierParameterizedTests {

    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    private String login;
    private String password;
    private String firstName;
    @Before
    public void setup(){
        courierClient = new CourierClient();
    }

    public CreateCourierParameterizedTests(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                {"",randomString(8),randomString(10)},
                {randomString(5),"",randomString(10)},
                {randomString(5),randomString(8),""},
        };
    }

    @Test
    @DisplayName("Create courier negative data")
    @Description("Создание курьера без необходимых данных в запросе")
    public void CreateCourierNegativeDataTest(){

        courier = new Courier(login,password,firstName);

        ValidatableResponse response = courierClient.create(courier);

        assertEquals("Статус код неверный при создании курьера без необходимых данных",
                HttpStatus.SC_BAD_REQUEST, response.extract().statusCode());


        assertEquals("Некорректное сообщение об ошибке при создании необходимых данных",
                "Недостаточно данных для создания учетной записи",response.extract().path("message"));


    }

    @After
    public void tearDown() {

        ValidatableResponse loginResponse = courierClient.login(credsFrom(courier));

        if (loginResponse.extract().statusCode() == HttpStatus.SC_OK){

            courierId = loginResponse.extract().path("id");
            courierClient.delete(courierId);

        }

    }
}
