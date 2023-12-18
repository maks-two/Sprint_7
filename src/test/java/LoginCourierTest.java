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
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static utils.Utils.randomString;

public class LoginCourierTest {
    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courier = randomCourier();
        courierClient = new CourierClient();
        courierClient.create(courier);
    }

    @Test
    @DisplayName("Login courier")
    @Description("Проверка авторизации курьером")
    public void loginCourierTest(){

        ValidatableResponse Response = courierClient.login(credsFrom(courier));

        assertThat("Статус код неверный при авторизации курьера",
                Response.extract().statusCode(), equalTo(HttpStatus.SC_OK));

        assertThat("Неверное сообщение при успешной авторизации курьера",
                Response.extract().path("id"),instanceOf(Integer.class));

    }

    @Test
    @DisplayName("Login courier with wrong Login")
    @Description("Проверка авторизации курьером с неправильным логином")
    public void loginCourierWithWrongLoginTest(){

        Courier courierWithWrongLogin = new Courier(randomString(5), courier.getPassword(), courier.getFirstName());

        ValidatableResponse Response = courierClient.login(credsFrom(courierWithWrongLogin));

        assertThat("Статус код неверный при авторизации курьера",
                Response.extract().statusCode(), equalTo(HttpStatus.SC_NOT_FOUND));

        assertThat("Неверное сообщение при успешной авторизации курьера",
                Response.extract().path("message"),equalTo("Учетная запись не найдена"));

    }

    @Test
    @DisplayName("Login courier with wrong Password")
    @Description("Проверка авторизации курьером с неправильным паролем")
    public void loginCourierWithWrongPasswordTest(){

        Courier courierWithWrongLogin = new Courier(courier.getPassword(),randomString(8) , courier.getFirstName());

        ValidatableResponse Response = courierClient.login(credsFrom(courierWithWrongLogin));

        assertThat("Статус код неверный при авторизации курьера",
                Response.extract().statusCode(), equalTo(HttpStatus.SC_NOT_FOUND));

        assertThat("Неверное сообщение при успешной авторизации курьера",
                Response.extract().path("message"),equalTo("Учетная запись не найдена"));

    }

    @Test
    @DisplayName("Login courier without Password")
    @Description("Проверка авторизации курьером без паролем")
    public void loginCourierWithoutPasswordTest(){

        Courier courierWithWrongLogin = new Courier(courier.getPassword(),"" , courier.getFirstName());

        ValidatableResponse Response = courierClient.login(credsFrom(courierWithWrongLogin));

        assertThat("Статус код неверный при авторизации курьера",
                Response.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));

        assertThat("Неверное сообщение при успешной авторизации курьера",
                Response.extract().path("message"),equalTo("Недостаточно данных для входа"));

    }

    @Test
    @DisplayName("Login courier without Login")
    @Description("Проверка авторизации курьером без логина")
    public void loginCourierWithoutLoginTest(){

        Courier courierWithWrongLogin = new Courier("",courier.getPassword() , courier.getFirstName());

        ValidatableResponse Response = courierClient.login(credsFrom(courierWithWrongLogin));

        assertThat("Статус код неверный при авторизации курьера",
                Response.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));

        assertThat("Неверное сообщение при успешной авторизации курьера",
                Response.extract().path("message"),equalTo("Недостаточно данных для входа"));

    }


    @After
    public void tearDown() {

        ValidatableResponse loginResponse = courierClient.login(credsFrom(courier));
        courierId = loginResponse.extract().path("id");
        courierClient.delete(courierId);
    }

}
