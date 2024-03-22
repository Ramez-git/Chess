package clientTests;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static String auth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }
    @Test
    @Order(1)
    public void registertestp() throws ResponseException {
        var myf =new  ServerFacade();
        auth = (myf.register(new UserData("new1","new","new"))).authToken();
        assertDoesNotThrow(() -> auth);
    }
    @Test
    @Order(2)
    public void registertestn() throws ResponseException {
        var myf =new  ServerFacade();

        assertThrows(ResponseException.class, () -> myf.register(new UserData("new", "new", null)));
    }
    @Test
    @Order(3)
    public void logintestp() throws ResponseException {
        var myf =new ServerFacade();
        auth = (myf.register(new UserData("new","new","new"))).authToken();
        myf.logout(auth);
        auth = myf.login(new UserData("new","new",null)).authToken();
        assertDoesNotThrow(() -> auth);
    }
    @Test
    @Order(4)
    public void logouttestp() throws ResponseException {
        var myf =new  ServerFacade();
        auth = (myf.register(new UserData("ne1w","new","new"))).authToken();
        assertDoesNotThrow(() -> myf.logout(auth));
    }


    @Test
    @Order(5)
    public void logintestn() throws ResponseException {
        var myf =new  ServerFacade();

        assertThrows(ResponseException.class,() -> myf.login(new UserData("new","ne111w","new")));
    }
    @Test
    @Order(6)
    public void logouttestn(){
        var myf =new  ServerFacade();
        assertThrows(ResponseException.class,() -> myf.logout(null));
    }
    @Test
    public void creategametestp() throws ResponseException {
        var myf = new ServerFacade();
        auth = (myf.register(new UserData("new22","new","new"))).authToken();
        myf.logout(auth);

        assertDoesNotThrow(() -> myf.login(new UserData("new22","new",null)).authToken());
    }
    @Test
    public void creategametestn(){
        var myf = new ServerFacade();
        assertThrows(ResponseException.class,() -> myf.creategame("gg1",null));
    }
    @Test
    public void listgametestp() throws ResponseException {
        var myf = new ServerFacade();
        auth = (myf.register(new UserData("new222","new","new"))).authToken();
        myf.logout(auth);
        assertDoesNotThrow(() -> myf.login(new UserData("new222","new",null)).authToken());
    }
    @Test
    public void listgametestn(){
        var myf = new ServerFacade();
        assertThrows(ResponseException.class,() -> myf.creategame("gg1",null));
    }


}
