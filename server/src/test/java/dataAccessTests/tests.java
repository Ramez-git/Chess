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
    @Order(1)
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
    @Test
    @Order(2)
    @DisplayName("Register Bad Request")
    public void failRegister() throws TestException {
        //attempt to register a user without a password
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = newUser.username;
        registerRequest.password = null;
        registerRequest.email = newUser.email;

        TestModels.TestLoginRegisterResult registerResult = serverFacade.register(registerRequest);

        Assertions.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, serverFacade.getStatusCode(),
                "Server response code was not 400 Bad Request");
        Assertions.assertTrue(registerResult.message.toLowerCase(Locale.ROOT).contains("error"),
                "Response missing error message");
        Assertions.assertNull(registerResult.username, "Response incorrectly contained username");
        Assertions.assertNull(registerResult.authToken, "Response incorrectly contained authentication string");
    }
    @Test
    @Order(3)
    @DisplayName("Normal User Login")
    public void successLogin() throws TestException {
        TestModels.TestLoginRequest loginRequest = new TestModels.TestLoginRequest();
        loginRequest.username = existingUser.username;
        loginRequest.password = existingUser.password;

        TestModels.TestLoginRegisterResult loginResult = serverFacade.login(loginRequest);

        Assertions.assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode(),
                "Server response code was not 200 OK");
        Assertions.assertFalse(
                loginResult.message != null && loginResult.message.toLowerCase(Locale.ROOT).contains("error"),
                "Response returned error message");
        Assertions.assertEquals(existingUser.username, loginResult.username,
                "Response did not give the same username as user");
        Assertions.assertNotNull(loginResult.authToken, "Response did not return authentication String");
    }
    @Test
    @Order(4)
    @DisplayName("Login Wrong Password")
    public void loginWrongPassword() throws TestException {
        TestModels.TestLoginRequest loginRequest = new TestModels.TestLoginRequest();
        loginRequest.username = existingUser.username;
        loginRequest.password = newUser.password;

        TestModels.TestLoginRegisterResult loginResult = serverFacade.login(loginRequest);

        Assertions.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, serverFacade.getStatusCode(),
                "Server response code was not 401 Unauthorized");
        Assertions.assertTrue(loginResult.message.toLowerCase(Locale.ROOT).contains("error"),
                "Response missing error message");
        Assertions.assertNull(loginResult.username, "Response incorrectly returned username");
        Assertions.assertNull(loginResult.authToken, "Response incorrectly return authentication String");
    }
    @Test
    @Order(5)
    @DisplayName("Valid Creation")
    public void goodCreate() throws TestException {
        TestModels.TestCreateResult createResult = serverFacade.createGame(createRequest, existingAuth);

        Assertions.assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode(),
                "Server response code was not 200 OK");
        Assertions.assertFalse(
                createResult.message != null && createResult.message.toLowerCase(Locale.ROOT).contains("error"),
                "Result returned an error message");
        Assertions.assertNotNull(createResult.gameID, "Result did not return a game ID");
        Assertions.assertTrue(createResult.gameID > 0, "Result returned invalid game ID");
    }
    @Test
    @Order(6)
    @DisplayName("Watch Bad Authentication")
    public void badAuthWatch() throws TestException {
        //create game
        TestModels.TestCreateResult createResult = serverFacade.createGame(createRequest, existingAuth);

        //make watch request
        TestModels.TestJoinRequest watchRequest = new TestModels.TestJoinRequest();
        watchRequest.gameID = createResult.gameID;

        //try watch
        TestModels.TestResult watchResult = serverFacade.verifyJoinPlayer(watchRequest, existingAuth + "bad stuff");

        //check failed
        Assertions.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, serverFacade.getStatusCode(),
                "Server response code was not 401 Unauthorized");
        Assertions.assertTrue(
                watchResult.message != null && watchResult.message.toLowerCase(Locale.ROOT).contains("error"),
                "Invalid Request didn't return an error message");
    }
    @Test
    @Order(7)
    @DisplayName("Join Created Game")
    public void goodJoin() throws TestException {
        //create game
        TestModels.TestCreateResult createResult = serverFacade.createGame(createRequest, existingAuth);

        //join as white
        TestModels.TestJoinRequest joinRequest = new TestModels.TestJoinRequest();
        joinRequest.gameID = createResult.gameID;
        joinRequest.playerColor = ChessGame.TeamColor.WHITE;

        //try join
        TestModels.TestResult joinResult = serverFacade.verifyJoinPlayer(joinRequest, existingAuth);

        //check
        Assertions.assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode(),
                "Server response code was not 200 OK");
        Assertions.assertFalse(
                joinResult.message != null && joinResult.message.toLowerCase(Locale.ROOT).contains("error"),
                "Result returned an error message");

        TestModels.TestListResult listResult = serverFacade.listGames(existingAuth);

        Assertions.assertEquals(1, listResult.games.length);
        Assertions.assertEquals(existingUser.username, listResult.games[0].whiteUsername);
        Assertions.assertNull(listResult.games[0].blackUsername);
    }


    @Test
    @Order(8)
    @DisplayName("Join Bad Authentication")
    public void badAuthJoin() throws TestException {
        //create game
        TestModels.TestCreateResult createResult = serverFacade.createGame(createRequest, existingAuth);

        //join as white
        TestModels.TestJoinRequest joinRequest = new TestModels.TestJoinRequest();
        joinRequest.gameID = createResult.gameID;
        joinRequest.playerColor = ChessGame.TeamColor.WHITE;

        //try join
        TestModels.TestResult joinResult = serverFacade.verifyJoinPlayer(joinRequest, existingAuth + "bad stuff");

        //check
        Assertions.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, serverFacade.getStatusCode(),
                "Server response code was not 401 Unauthorized");
        Assertions.assertTrue(
                joinResult.message != null && joinResult.message.toLowerCase(Locale.ROOT).contains("error"),
                "Invalid Request didn't return an error message");
    }
    @Test
    @Order(9)
    @DisplayName("List Multiple Games")
    public void gamesList() throws TestException {
        //register a few users to create games
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = "a";
        registerRequest.password = "A";
        registerRequest.email = "a.A";
        TestModels.TestLoginRegisterResult userA = serverFacade.register(registerRequest);

        registerRequest.username = "b";
        registerRequest.password = "B";
        registerRequest.email = "b.B";
        TestModels.TestLoginRegisterResult userB = serverFacade.register(registerRequest);

        registerRequest.username = "c";
        registerRequest.password = "C";
        registerRequest.email = "c.C";
        TestModels.TestLoginRegisterResult userC = serverFacade.register(registerRequest);

        //create games

        //1 as black from A
        createRequest.gameName = "I'm numbah one!";
        TestModels.TestCreateResult game1 = serverFacade.createGame(createRequest, userA.authToken);

        //1 as white from B
        createRequest.gameName = "Lonely";
        TestModels.TestCreateResult game2 = serverFacade.createGame(createRequest, userB.authToken);

        //1 of each from C
        createRequest.gameName = "GG";
        TestModels.TestCreateResult game3 = serverFacade.createGame(createRequest, userC.authToken);
        createRequest.gameName = "All by myself";
        TestModels.TestCreateResult game4 = serverFacade.createGame(createRequest, userC.authToken);

        //A join game 1 as black
        TestModels.TestJoinRequest joinRequest = new TestModels.TestJoinRequest();
        joinRequest.playerColor = ChessGame.TeamColor.BLACK;
        joinRequest.gameID = game1.gameID;
        serverFacade.verifyJoinPlayer(joinRequest, userA.authToken);

        //B join game 2 as white
        joinRequest.playerColor = ChessGame.TeamColor.WHITE;
        joinRequest.gameID = game2.gameID;
        serverFacade.verifyJoinPlayer(joinRequest, userB.authToken);

        //C join game 3 as white
        joinRequest.playerColor = ChessGame.TeamColor.WHITE;
        joinRequest.gameID = game3.gameID;
        serverFacade.verifyJoinPlayer(joinRequest, userC.authToken);

        //A join game3 as black
        joinRequest.playerColor = ChessGame.TeamColor.BLACK;
        joinRequest.gameID = game3.gameID;
        serverFacade.verifyJoinPlayer(joinRequest, userA.authToken);

        //C play self in game 4
        joinRequest.playerColor = ChessGame.TeamColor.WHITE;
        joinRequest.gameID = game4.gameID;
        serverFacade.verifyJoinPlayer(joinRequest, userC.authToken);
        joinRequest.playerColor = ChessGame.TeamColor.BLACK;
        joinRequest.gameID = game4.gameID;
        serverFacade.verifyJoinPlayer(joinRequest, userC.authToken);

        //create expected entry items
        Collection<TestModels.TestListResult.TestListEntry> expectedList = new HashSet<>();

        //game 1
        TestModels.TestListResult.TestListEntry entry = new TestModels.TestListResult.TestListEntry();
        entry.gameID = game1.gameID;
        entry.gameName = "I'm numbah one!";
        entry.blackUsername = userA.username;
        entry.whiteUsername = null;
        expectedList.add(entry);

        //game 2
        entry = new TestModels.TestListResult.TestListEntry();
        entry.gameID = game2.gameID;
        entry.gameName = "Lonely";
        entry.blackUsername = null;
        entry.whiteUsername = userB.username;
        expectedList.add(entry);

        //game 3
        entry = new TestModels.TestListResult.TestListEntry();
        entry.gameID = game3.gameID;
        entry.gameName = "GG";
        entry.blackUsername = userA.username;
        entry.whiteUsername = userC.username;
        expectedList.add(entry);

        //game 4
        entry = new TestModels.TestListResult.TestListEntry();
        entry.gameID = game4.gameID;
        entry.gameName = "All by myself";
        entry.blackUsername = userC.username;
        entry.whiteUsername = userC.username;
        expectedList.add(entry);

        //list games
        TestModels.TestListResult listResult = serverFacade.listGames(existingAuth);
        Assertions.assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode(),
                "Server response code was not 200 OK");
        Collection<TestModels.TestListResult.TestListEntry> returnedList =
                new HashSet<>(Arrays.asList(listResult.games));

        //check
        Assertions.assertEquals(expectedList, returnedList, "Returned Games list was incorrect");
    }
    @Test
    @Order(10)
    @DisplayName("Multiple Clears")
    public void multipleClear() throws TestException {

        //clear multiple times
        serverFacade.clear();
        serverFacade.clear();
        TestModels.TestResult result = serverFacade.clear();

        //make sure returned good
        Assertions.assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode(),
                "Server response code was not 200 OK");
        Assertions.assertFalse(result.message != null && result.message.toLowerCase(Locale.ROOT).contains("error"),
                "Clear Result returned an error message");
    }
}
