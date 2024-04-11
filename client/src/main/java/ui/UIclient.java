package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dataAccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.wrapper;
import server.Server;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class UIclient {
    boolean logged_in = false;
    boolean quit = false;
    private AuthData auth;
    private Scanner input;
    private String WHITE;
    private String BLACK;
    private String game;
    private Server server;
    private GameData mygames;

    public UIclient() throws DataAccessException {
        this.server = new Server();
        System.out.println(BLACK_QUEEN + " Welcome to 240 chess. Type Help to get started. " + BLACK_QUEEN);
        input = new Scanner(System.in);

    }
    public void run() throws ResponseException {
        var myserverf = new ServerFacade(8080);
        while (!quit) {
            if (logged_in) {
                System.out.print("[LOGGED_IN] >>> ");
                var tokens = input.nextLine().toLowerCase().split(" ");
                var cmd = (tokens.length > 0) ? tokens[0] : "help";
                var params = Arrays.copyOfRange(tokens, 1, tokens.length);
                if (Objects.equals(cmd, "help")) {
                    System.out.println(SET_TEXT_COLOR_BLUE + "create <NAME>"
                            + SET_TEXT_COLOR_WHITE + " - a game");
                    System.out.println(SET_TEXT_COLOR_BLUE + "list"
                            + SET_TEXT_COLOR_WHITE + " - games");
                    System.out.println(SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK|<empty>]"
                            + SET_TEXT_COLOR_WHITE + " - a game");
                    System.out.println(SET_TEXT_COLOR_BLUE + "observe <ID>"
                            + SET_TEXT_COLOR_WHITE + " - a game");
                    System.out.println(SET_TEXT_COLOR_BLUE + "logout"
                            + SET_TEXT_COLOR_WHITE + " - when you are done");
                    System.out.println(SET_TEXT_COLOR_BLUE + "quit"
                            + SET_TEXT_COLOR_WHITE + " - playing chess");
                    System.out.println(SET_TEXT_COLOR_BLUE + "help"
                            + SET_TEXT_COLOR_WHITE + " - with possible commands\n");
                }

                else if (Objects.equals(cmd, "create")) {
                    if (params.length == 1) {
                        try {
                            System.out.println(myserverf.creategame(new gamename1(params[0]), auth.authToken()));
                        } catch (ResponseException e) {
                            System.out.println("err");
                        }
                    } else {
                        System.out.println("err");
                    }
                } else if (Objects.equals(cmd, "list")) {
                    try {
                        var f = myserverf.listgames1(auth.authToken());
                        //System.out.println(f.get());
                        System.out.println(f);
                    } catch (ResponseException e) {
                        System.out.println("err");
                    }
                } else if (Objects.equals(cmd, "join")) {
                    if (params.length == 2) {
                        try {
                                var x = myserverf.joingame(auth.authToken(), new wrapper(params[1], params[0]));
                                System.out.println("success");
                                if (Objects.equals(params[1], "white")) {
                                    var myboard = new Gson().fromJson(x.toString(), GameData.class).game().getBoard();
                                    System.out.println(myboard.toString1(ChessGame.TeamColor.WHITE,null));
                                    WHITE = auth.authToken();
                                    game = params[0];
                                } else {
                                    var myboard = new Gson().fromJson(x.toString(), GameData.class).game().getBoard();
                                    System.out.println(myboard.toString1(ChessGame.TeamColor.BLACK,null));
                                    BLACK = auth.authToken();
                                    game = params[0];
                                }
                                game = params[0];

                        } catch (ResponseException e) {
                            System.out.println("err");
                        }
                    } else {
                        System.out.println("err");
                    }
                }

                else if (Objects.equals(cmd, "observe")) {
                    if (params.length == 1) {
                        try{
                            var x = myserverf.observer(auth.authToken(), new wrapper("", params[0]));
                            System.out.println("\n");
                            var myboard = new Gson().fromJson(x.toString(), GameData.class).game().getBoard();
                            System.out.println(myboard.toString1(ChessGame.TeamColor.WHITE,null));
                            System.out.println(myboard.toString1(ChessGame.TeamColor.BLACK,null));
                            System.out.println("success");
                        }
                        catch (ResponseException e){
                            System.out.println("err");}
                    } else {
                        System.out.println("err");
                    }
                } else if (Objects.equals(cmd, "logout")) {
                    try {
                        myserverf.logout(auth.authToken());
                        System.out.println("success");
                        logged_in = false;
                    } catch (ResponseException e) {
                        System.out.println("err");
                    }
                } else if (Objects.equals(cmd, "quit")) {
                    break;
                }
            } else {
                System.out.print("[LOGGED_OUT] >>> ");
                var tokens = input.nextLine().toLowerCase().split(" ");
                var cmd = (tokens.length > 0) ? tokens[0] : "help";
                var params = Arrays.copyOfRange(tokens, 1, tokens.length);
                if (Objects.equals(cmd, "help")) {
                    System.out.println(SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>"
                            + SET_TEXT_COLOR_WHITE + " - to create an account");
                    System.out.println(SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>"
                            + SET_TEXT_COLOR_WHITE + " - to play chess");
                    System.out.println(SET_TEXT_COLOR_BLUE + "quit"
                            + SET_TEXT_COLOR_WHITE + " - playing chess");
                    System.out.println(SET_TEXT_COLOR_BLUE + "help"
                            + SET_TEXT_COLOR_WHITE + " - with possible commands\n");
                } else if (Objects.equals(cmd, "quit")) {
                    break;
                } else if (Objects.equals(cmd, "register")) {
                    if (params.length == 3) {
                        try {
                            var str = myserverf.register(new UserData(params[0], params[1], params[2]));
                            auth = new Gson().fromJson(String.valueOf(str), AuthData.class);
                            System.out.println("Success");
                            logged_in = true;
                        } catch (ResponseException e) {
                            System.out.println("err");
                        }
                    } else {
                        throw new RuntimeException("not enough params register user");
                    }
                } else if (Objects.equals(cmd, "login")) {
                    if (params.length == 2) {
                        try {
                            var str = myserverf.login(new UserData(params[0], params[1], null));
                            auth = new Gson().fromJson(String.valueOf(str), AuthData.class);
                            System.out.println("Success");
                            logged_in = true;
                        } catch (ResponseException e) {
                            System.out.println("err");
                        }
                    } else {
                        System.out.println("err");
                    }

                }
            }

        }
    }

    public class gamename1 {
        @SerializedName("gameName")
        private String gameName;

        public gamename1(String gamename) {
            this.gameName = gamename;
        }

        public String getGameName() {
            return gameName;
        }
    }

}
