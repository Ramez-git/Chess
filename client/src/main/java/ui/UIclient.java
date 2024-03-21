package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;

import server.ServerFacade;
import server.Server.GamesWrapper;
public class UIclient {
    boolean logged_in=false;
    boolean quit =false;
    private AuthData auth;
    private Scanner input;
public UIclient(){
    System.out.println(BLACK_QUEEN + " Welcome to 240 chess. Type Help to get started. "+ BLACK_QUEEN);
    input = new Scanner(System.in);

}
public void run() throws ResponseException {
    var myserverf = new ServerFacade();
    while (!quit){
        if(logged_in){
            System.out.print("[LOGGED_IN] >>> ");
            var tokens = input.nextLine().toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(Objects.equals(cmd, "help")){
                System.out.println(SET_TEXT_COLOR_BLUE+"create <NAME>"
                        + SET_TEXT_COLOR_WHITE+ " - a game");
                System.out.println(SET_TEXT_COLOR_BLUE+"list"
                        + SET_TEXT_COLOR_WHITE+ " - games");
                System.out.println(SET_TEXT_COLOR_BLUE+"join <ID> [WHITE|BLACK|<empty>]"
                        + SET_TEXT_COLOR_WHITE+ " - a game");
                System.out.println(SET_TEXT_COLOR_BLUE+"observe <ID>"
                        + SET_TEXT_COLOR_WHITE+ " - a game");
                System.out.println(SET_TEXT_COLOR_BLUE+"logout"
                        + SET_TEXT_COLOR_WHITE+ " - when you are done");
                System.out.println(SET_TEXT_COLOR_BLUE+"quit"
                        + SET_TEXT_COLOR_WHITE+ " - playing chess");
                System.out.println(SET_TEXT_COLOR_BLUE+"help"
                        + SET_TEXT_COLOR_WHITE+ " - with possible commands\n");
            }
            else if(Objects.equals(cmd, "create")){
                if(params.length == 1){
                    System.out.println(myserverf.creategame(params[0],auth.authToken()));
                }
                else{
                    throw new RuntimeException("not enough params create game");
                }
            } else if (Objects.equals(cmd, "list")) {
                var f = myserverf.listgames(auth.authToken());
                System.out.println(f);
            } else if (Objects.equals(cmd, "join")) {
                if(params.length == 2){
                    myserverf.joingame(auth.authToken(),params[1]);
                }
                else{
                    throw new RuntimeException("not enough params create game");
                }
            } else if (Objects.equals(cmd, "observe")) {
                if(params.length == 1){
                    myserverf.observer(auth.authToken(), params[0]);
                    System.out.println("success");
                }
                else{
                    throw new RuntimeException("not enough params create game");
                }
            } else if (Objects.equals(cmd, "logout")) {
                myserverf.logout(auth.authToken());
                System.out.println("success");
                logged_in = false;
            } else if (Objects.equals(cmd, "quit")) {
                break;
            }
        }
        else{
            System.out.print("[LOGGED_OUT] >>> ");
            var tokens = input.nextLine().toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(Objects.equals(cmd, "help")){
                System.out.println(SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL>"
                        + SET_TEXT_COLOR_WHITE+ " - to create an account");
                System.out.println(SET_TEXT_COLOR_BLUE+"login <USERNAME> <PASSWORD>"
                        + SET_TEXT_COLOR_WHITE+ " - to play chess");
                System.out.println(SET_TEXT_COLOR_BLUE+"quit"
                        + SET_TEXT_COLOR_WHITE+ " - playing chess");
                System.out.println(SET_TEXT_COLOR_BLUE+"help"
                        + SET_TEXT_COLOR_WHITE+ " - with possible commands\n");
            }
            else if (Objects.equals(cmd, "quit")){
                break;
            }
            else if(Objects.equals(cmd, "register")){
                if(params.length == 3){
                    var str = myserverf.register(new UserData(params[0],params[1],params[2]));
                    auth = new Gson().fromJson(String.valueOf(str), AuthData.class);
                    System.out.println("Success");
                    logged_in= true;
                }
                else{
                    throw new RuntimeException("not enough params register user");
                }
            } else if (Objects.equals(cmd, "login")) {
                if(params.length == 2){
                    var str = myserverf.login(new UserData(params[0],params[1],null));
                    auth = new Gson().fromJson(String.valueOf(str), AuthData.class);
                    System.out.println("Success");
                    logged_in=true;
                }
                else{
                    throw new RuntimeException("not enough params login user");
                }
            }
        }
    }
}

}
