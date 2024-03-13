package dataAccessTests;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import passoffTests.obfuscatedTestClasses.TestServerFacade;
import passoffTests.testClasses.TestException;
import passoffTests.testClasses.TestModels;
import server.Server;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class tests {
    private static TestModels.TestUser existingUser;

    private static TestModels.TestUser newUser;

    private static TestModels.TestCreateRequest createRequest;

    private static TestServerFacade serverFacade;
    private static Server server;

    private String existingAuth;
    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));

        existingUser = new TestModels.TestUser();
        existingUser.username = "olduser";
        existingUser.password = "existingUserPassword";
        existingUser.email = "eu@mail.com";

        newUser = new TestModels.TestUser();
        newUser.username = "NewUser";
        newUser.password = "newUserPassword";
        newUser.email = "nu@mail.com";

        createRequest = new TestModels.TestCreateRequest();
        createRequest.gameName = "testGame";
    }
    @BeforeEach
    public void setup() throws TestException {
        serverFacade.clear();

        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = existingUser.username;
        registerRequest.password = existingUser.password;
        registerRequest.email = existingUser.email;

        //one user already logged in
        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest);
        existingAuth = regResult.authToken;
    }
    @Test
    @Order(6)
    @DisplayName("Normal User Registration")
    public void Register(){
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = newUser.username;
        registerRequest.password = newUser.password;
        registerRequest.email = newUser.email;

        //submit register request
        TestModels.TestLoginRegisterResult registerResult = serverFacade.register(registerRequest);

        Assertions.assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode(),
                "Server response code was not 200 OK");
        Assertions.assertFalse(
                registerResult.message != null && registerResult.message.toLowerCase(Locale.ROOT).contains("error"),
                "Response gave an error message");
        Assertions.assertEquals(newUser.username, registerResult.username,
                "Response did not have the same username as was registered");
        Assertions.assertNotNull(registerResult.authToken, "Response did not contain an authentication string");

    }
}
