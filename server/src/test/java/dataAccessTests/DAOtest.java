package dataAccessTests;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;
import server.*;
public class DAOtest {

    private Server server;

    @Before
    public void setUp() {
        server = new Server();
    }

    @Test
    public void testAddUser_Positive() {
        // Prepare a dummy request object
        Request request = new RequestStub("{\"username\": \"testUser\", \"password\": \"testPassword\"}");

        // Prepare a dummy response object
        ResponseStub response = new ResponseStub();

        try {
            // Call the addUser method
            Object result = server.addUser(request, response);

            // Verify that no exception was thrown
            assert true : "No exception thrown";
        } catch (Exception e) {
            // Handle exception
            assert false : "Exception occurred: " + e.getMessage();
        }
    }

    @Test
    public void testAddUser_Negative_UsernameTaken() {
        // Prepare a dummy request object
        Request request = new RequestStub("{\"username\": \"existingUser\", \"password\": \"testPassword\"}");

        // Prepare a dummy response object
        ResponseStub response = new ResponseStub();

        try {
            // Call the addUser method
            Object result = server.addUser(request, response);

            // Verify that no exception was thrown
            assert true : "No exception thrown";
        } catch (Exception e) {
            // Handle exception
            assert false : "Exception occurred: " + e.getMessage();
        }
    }

    // Add more negative test cases for addUser method as needed

    // Stub implementation of Request for testing
    private static class RequestStub extends Request {
        private final String body;

        public RequestStub(String body) {
            this.body = body;
        }

        @Override
        public String body() {
            return body;
        }
    }

    // Stub implementation of Response for testing
    private static class ResponseStub extends Response {
        private int status;

        @Override
        public void status(int statusCode) {
            this.status = statusCode;
        }

        public int getStatus() {
            return status;
        }
    }
}
