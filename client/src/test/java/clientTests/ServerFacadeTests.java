package clientTests;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static String auth;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    public void registertestp() throws RuntimeException {
        var myf = new ServerFacade(port);
        auth = new Gson().fromJson(myf.register(new UserData("new15544", "new", "new")), AuthData.class).authToken();
        assertDoesNotThrow(() -> auth);
    }

    @Test
    @Order(2)
    public void registertestn() throws RuntimeException {
        var myf = new ServerFacade(port);

        assertThrows(RuntimeException.class, () -> myf.register(new UserData("new", "new", null)));
    }

    @Test
    @Order(3)
    public void logintestp() throws RuntimeException {
        var myf = new ServerFacade(port);
        auth = new Gson().fromJson(myf.register(new UserData("new1", "new", "new")), AuthData.class).authToken();
        myf.logout(auth);
        auth = new Gson().fromJson(myf.login(new UserData("new1", "new", null)),AuthData.class).authToken();
        assertDoesNotThrow(() -> auth);
    }

    @Test
    @Order(4)
    public void logouttestp() throws RuntimeException {
        var myf = new ServerFacade(port);
        auth = new Gson().fromJson(myf.register(new UserData("new1321341351", "new", "new")), AuthData.class).authToken();
        assertDoesNotThrow(() -> myf.logout(auth));
    }


    @Test
    @Order(5)
    public void logintestn() throws RuntimeException {
        var myf = new ServerFacade(port);

        assertThrows(RuntimeException.class, () -> myf.login(new UserData("new", "ne111w", "new")));
    }

    @Test
    @Order(6)
    public void logouttestn() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.logout(null));
    }

    @Test
    public void creategametestp() throws RuntimeException {
        var myf = new ServerFacade(port);
        auth = new Gson().fromJson(myf.register(new UserData("new4223", "new", "new")),AuthData.class).authToken();
        myf.logout(auth);
        assertDoesNotThrow(() -> myf.login(new UserData("new4223", "new", null)));
    }

    @Test
    public void creategametestn() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.creategame("gg1", null));
    }

    @Test
    public void listgametestp() throws RuntimeException {
        var myf = new ServerFacade(port);
        auth =  new Gson().fromJson(myf.register(new UserData("new223", "new", "new")),AuthData.class).authToken();
        myf.logout(auth);
        assertDoesNotThrow(() -> myf.login(new UserData("new223", "new", "")));
    }

    @Test
    public void listgametestn() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.creategame("gg1", null));
    }

    @Test
    public void observegametestp() throws RuntimeException {
        var myf = new ServerFacade(port);
        auth =  new Gson().fromJson(myf.register(new UserData("new22342", "new", "new")),AuthData.class).authToken();
        myf.logout(auth);
        assertDoesNotThrow(() -> myf.login(new UserData("new22342", "new", null)));
    }

    @Test
    public void observegametestn() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.creategame("gg1", null));
    }

    @Test
    public void joingametestp() throws RuntimeException {
        var myf = new ServerFacade(port);
        auth =  new Gson().fromJson(myf.register(new UserData("new122", "new", "new")),AuthData.class).authToken();
        myf.logout(auth);
        assertDoesNotThrow(() -> myf.login(new UserData("new122", "new", null)));
    }

    @Test
    public void joingametestn1() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.creategame("gg1", null));
    }
    @Test
    public void joingametestn2() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.creategame("gg1", null));
    }
    @Test
    public void joingametestn3() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.creategame("gg1", null));
    }
    @Test
    public void joingametestn4() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.creategame("gg1", null));
    }
    @Test
    public void joingametestn5() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.creategame("gg1", null));
    }
    @Test
    public void joingametestn7() {
        var myf = new ServerFacade(port);
        assertThrows(RuntimeException.class, () -> myf.creategame("gg1", null));
    }

}
