package dataAccessTests;
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
public class tests {
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
    @Test
    public void createauthsuccess() throws SQLException, DataAccessException {
        authService.createAuth(new UserData("usr","pass","myemail"));
    }
    @Test
    public void createauthfail() throws SQLException, DataAccessException {
        try{
        authService.createAuth(new UserData(null,null,"myemail"));}
        catch (DataAccessException e){
            assertEquals("Error: description",e.getMessage());
        }
    }
    @Test
    public void getusrsuccess() throws SQLException, DataAccessException {
        var x =userService.CreateUser(new UserData("dude","pass","mail"));
        authService.getusr(x);
    }
    @Test
    public void getusrfail() throws SQLException, DataAccessException {
        try{
        authService.getusr(new AuthData(null,null));
    } catch (DataAccessException e) {
            assertEquals("usr does not exist",e.getMessage());
        }
    }
    @Test
    public void getAuthSuccess() throws SQLException, DataAccessException {
        userService.CreateUser(new UserData("dude","pass","mail"));
        authService.getAuth(new UserData("dude","pass","mail"));
    }
    @Test
    public void getAuthfail(){
        try{
            authService.getAuth(new UserData("dude","pass","mail"));
        } catch (DataAccessException | SQLException e) {
            assertEquals("User is not logged in getAuth",e.getMessage());
        }
    }
    @Test
    public void deleteSessionSuccess() throws SQLException, DataAccessException {
        var x = userService.CreateUser(new UserData("dude","pass","mail"));
        authService.deleteSession(x);
    }
    @Test
    public void deleteSessionFail() throws SQLException, DataAccessException {
        try{
            authService.deleteSession(new AuthData(null,null));
        } catch (DataAccessException | SQLException e) {
            assertEquals("Error: description",e.getMessage());
        }
    }
    @Test
    public void deleteAllsuccess() throws SQLException, DataAccessException {
        authService.deleteAll();
    }

}