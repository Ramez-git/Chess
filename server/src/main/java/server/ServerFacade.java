package server;

import com.google.gson.Gson;
import model.UserData;
import java.io.IOException;
import server.*;
import spark.Request;
import spark.Response;
public class ServerFacade {
    private final Server server;

    public ServerFacade(Server server) {
        this.server = server;
    }

    public Object register(UserData user) {
        try {
            // Constructing a dummy Request object
            Request req = createDummyRequest(user.username(), user.password(), user.email());

            // Constructing a dummy Response object
            Response res = createDummyResponse();

            // Calling addUser method from Server with the constructed Request and Response objects
            return server.addUser(req, res);
        } catch (Exception e) {
            // Handle any exceptions here
            return new Gson().toJson(new Server.message("Internal Server Error"));
        }
    }

    // Method to create a dummy Request object
    private Request createDummyRequest(String username, String password,String email) {
        return new DummyRequest(username, password,email);
    }

    // Method to create a dummy Response object
    private Response createDummyResponse() {
        return new DummyResponse();
    }

    // DummyRequest class implementing spark.Request
    private static class DummyRequest extends Request {
        private final String username;
        private final String password;
        private final String email;

        public DummyRequest(String username, String password,String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }

        // Implement required methods based on your usage
    }

    // DummyResponse class implementing spark.Response
    private static class DummyResponse extends Response {
        // Implement required methods based on your usage
    }
}
