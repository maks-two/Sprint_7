package data;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CourierCreds {
    private final String login;
    private final String password;

    public static CourierCreds credsFrom(Courier courier) {
        return new CourierCreds(courier.getLogin(), courier.getPassword());
    }
}
