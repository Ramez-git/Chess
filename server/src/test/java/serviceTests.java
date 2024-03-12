import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


public class serviceTests {

    private UserService userService;
    private MemoryDataAccessUser memoryDataAccessUser;
    private AuthService authService;
    private MemoryDataAccessAuth memoryDataAccessAuth;
    private MemoryDataAccessGame memoryDataAccessGame;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        final DataAccessAuth authdata = new MemoryDataAccessAuth();
        memoryDataAccessAuth = (MemoryDataAccessAuth) authdata;
        authService = new AuthService(memoryDataAccessAuth);

        final DataAccessUser userdata = new MemoryDataAccessUser(authService);
        memoryDataAccessUser = (MemoryDataAccessUser) userdata;
        userService = new UserService(memoryDataAccessUser);

        final DataAccessgame gamedata = new MemoryDataAccessGame();
        memoryDataAccessGame = (MemoryDataAccessGame) gamedata;
        gameService = new GameService(memoryDataAccessGame);
    }

    // UserService Tests

    @Test
    public void testUserService_CreateUser_Positive() {
        UserData user = new UserData("username", "password", "email");
        assertDoesNotThrow(() -> {
            userService.CreateUser(user);
            assertTrue(memoryDataAccessUser.users.containsKey("username"));
        });
    }

    @Test
    public void testUserService_CreateUser_Negative() {
        UserData invalidUser = new UserData(null, "password", "email");
        assertThrows(DataAccessException.class, () -> userService.CreateUser(invalidUser));
    }

    @Test
    public void testUserService_Login_Positive() {
        UserData user = new UserData("username", "password", "email");
        assertDoesNotThrow(() -> {
            userService.CreateUser(user);
            AuthData authData = userService.login(user);
            assertNotNull(authData);
        });
    }

    @Test
    public void testUserService_Login_Negative() {
        UserData invalidUser = new UserData("nonexistentUser", "invalidPassword", "email");
        assertThrows(DataAccessException.class, () -> userService.login(invalidUser));
    }

    @Test
    public void testUserService_DeleteAll_Positive() {
        assertDoesNotThrow(() -> userService.deleteAll());
    }


    @Test
    public void testAuthService_GetAuth_Positive() throws DataAccessException, SQLException {
        UserData user = new UserData("username", "password", "email");
        AuthData authData = authService.createAuth(user);

        assertDoesNotThrow(() -> {
            AuthData retrievedAuth = authService.getAuth(user);
            assertNotNull(retrievedAuth);
            assertEquals(authData, retrievedAuth);
        });
    }

    @Test
    public void testAuthService_GetAuth_Negative() {
        UserData invalidUser = new UserData("nonexistentUser", "invalidPassword", "email");
        assertThrows(DataAccessException.class, () -> authService.getAuth(invalidUser));
    }

    @Test
    public void testAuthService_Getusr_Positive() throws DataAccessException, SQLException {
        UserData user = new UserData("username", "password", "email");
        AuthData authData = authService.createAuth(user);

        assertDoesNotThrow(() -> {
            AuthData retrievedAuth = authService.getusr(authData);
            assertNotNull(retrievedAuth);
            assertEquals(authData, retrievedAuth);
        });
    }

    @Test
    public void testAuthService_Getusr_Negative() {
        AuthData invalidAuthData = new AuthData("invalidAuthToken", "invalidUsername");
        assertThrows(DataAccessException.class, () -> authService.getusr(invalidAuthData));
    }

    @Test
    public void testAuthService_DeleteSession_Positive() throws DataAccessException, SQLException {
        UserData user = new UserData("username", "password", "email");
        AuthData authData = authService.createAuth(user);

        assertDoesNotThrow(() -> {
            authService.deleteSession(authData);
            assertFalse(memoryDataAccessAuth.auths.containsKey(authData.authToken()));
        });
    }

    @Test
    public void testAuthService_DeleteSession_Negative() {
        AuthData invalidAuthData = new AuthData("invalidAuthToken", "invalidUsername");
        assertThrows(DataAccessException.class, () -> authService.deleteSession(invalidAuthData));
    }

    @Test
    public void testAuthService_DeleteAll_Positive() {
        assertDoesNotThrow(() -> authService.deleteAll());
    }

    @Test
    public void testAuthService_CreateAuth_Positive() {
        UserData user = new UserData("username", "password", "email");
        assertDoesNotThrow(() -> {
            AuthData authData = authService.createAuth(user);
            assertNotNull(authData);
            assertTrue(memoryDataAccessAuth.auths.containsKey(authData.authToken()));
        });
    }

    @Test
    public void testGameService_CreateGame_Positive() {
        assertDoesNotThrow(() -> {
            Integer gameID = gameService.createGame("ChessGame");
            assertTrue(memoryDataAccessGame.games.containsKey(gameID));
        });
    }

    @Test
    public void testGameService_ListGames_Positive() {
        assertDoesNotThrow(() -> {
            GameData[] games = gameService.listGames();
            assertNotNull(games);
        });
    }

    @Test
    public void testGameService_UpdateGame_Positive() throws DataAccessException, SQLException {
        Integer gameID = gameService.createGame("ChessGame");
        assertDoesNotThrow(() -> gameService.updateGame(gameID, "WhitePlayer", null));
        assertNull(memoryDataAccessGame.games.get(gameID).whiteUsername(), "Failed to update the game with the correct white player.");
    }

    @Test
    public void testGameService_GetGame_Positive() throws DataAccessException, SQLException {
        Integer gameID = gameService.createGame("ChessGame");
        assertDoesNotThrow(() -> {
            ChessGame chessGame = gameService.getGame(gameID);
            assertNotNull(chessGame, "Failed to retrieve the game.");
        });
    }

    @Test
    public void testGameService_DeleteAll_Positive() {
        assertDoesNotThrow(() -> gameService.deleteAll());
    }
}
