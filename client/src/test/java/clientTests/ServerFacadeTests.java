package clientTests;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static String auth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }
//
//    @AfterAll
//    static void stopServer() {
//        server.stop();
//    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }
    @Test
    public void registertestp() throws ResponseException {
        var myf =new  ServerFacade();
        auth = (myf.register(new UserData("new","new","new"))).authToken();
        assertDoesNotThrow(() -> auth);
    }
    @Test
    public void registertestn() throws ResponseException {
        var myf =new  ServerFacade();

        assertThrows(ResponseException.class, () -> myf.register(new UserData("new", "new", null)));
    }
    @Test
    public void logouttestp(){
        var myf =new  ServerFacade();
        assertDoesNotThrow(() -> myf.logout(auth));
    }

    @Test
    public void logintestp() throws ResponseException {
        var myf =new ServerFacade();
        auth = myf.login(new UserData("new","new","new")).authToken();
        assertDoesNotThrow(() -> auth);
    }
    @Test
    public void logintestn() throws ResponseException {
        var myf =new  ServerFacade();

        assertThrows(ResponseException.class,() -> myf.login(new UserData("new","ne111w","new")));
    }
    @Test
    public void logouttestn(){
        var myf =new  ServerFacade();
        assertThrows(ResponseException.class,() -> myf.logout(null));
    }
}
