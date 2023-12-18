package order;

import data.OrderCreds;
import data.Orders;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import utils.Specification;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String PATH = "api/v1/orders";
    private static final String PATH_TRACK = "/api/v1/orders/track";
    private static final String DELETE_PATH = "api/v1/orders/cancel";
    private static final String ACCEPT_PATH = "api/v1/orders/accept/";



    @Step("Send post request to /api/v1/orders")
    public ValidatableResponse create(Orders order) {

        return given()
                .spec(Specification.requestSpecification())
                .and()
                .body(order)
                .when()
                .post(PATH)
                .then();
    }

    @Step("Send put request to /api/v1/orders/cancel")
    public ValidatableResponse delete(int trackId) {

        OrderCreds orderCreds = new OrderCreds(trackId);

        return given()
                .spec(Specification.requestSpecification())
                .and()
                .body(orderCreds)
                .when()
                .put(DELETE_PATH)
                .then();
    }

    @Step("Send get request to /api/v1/orders")
    public ValidatableResponse get(){
        return given()
                .spec(Specification.requestSpecification())
                .get(PATH)
                .then();
    }

    @Step("Send get request to /api/v1/orders")
    public ValidatableResponse get_track(String courierId){
        return given()
                .spec(Specification.requestSpecification())
                .queryParam("t", courierId)
                .when()
                .get(PATH_TRACK)
                .then();
    }

    @Step("Send put request to api/v1/orders/accept/")
    public ValidatableResponse acceptOrder(String orderId, String courierId) {

        return given()
                .spec(Specification.requestSpecification())
                .queryParam("courierId", courierId)
                .when()
                .put(ACCEPT_PATH + Integer.parseInt(orderId))
                .then();
    }

}
