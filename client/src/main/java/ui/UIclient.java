package ui;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dataAccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.Server;
import server.ServerFacade;
import service.AuthService;
import service.GameService;
import service.UserService;

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

    public UIclient() throws DataAccessException {
        this.server = new Server();
        System.out.println(BLACK_QUEEN + " Welcome to 240 chess. Type Help to get started. " + BLACK_QUEEN);
        input = new Scanner(System.in);

    }

    public static void printChessboardWhite() {
        char[][] letters = {
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'e'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
        System.out.println(" a b c d e f g h");
        for (int i = 0; i < 8; i++) {
            System.out.print(i + 1);
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    System.out.print("\u001B[47m");
                } else {
                    System.out.print("\u001B[40m");
                }
                if (i <= 5) {
                    System.out.print(SET_TEXT_COLOR_RED + letters[i][j] + " ");
                } else {
                    System.out.print(SET_TEXT_COLOR_BLUE + letters[i][j] + " ");
                }

            }
            System.out.println("\u001B[0m");
        }
        System.out.println(" a b c d e f g h");
    }

    public static void printChessboardBlack() {
        char[][] letters = {
                {'R', 'N', 'B', 'K', 'Q', 'B', 'N', 'R'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {'r', 'n', 'b', 'k', 'q', 'b', 'n', 'r'}
        };

        System.out.println(" h g f e d c b a");
        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i);
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    System.out.print("\u001B[47m");
                } else {
                    System.out.print("\u001B[40m");
                }
                if (i <= 5) {

                    System.out.print(SET_TEXT_COLOR_BLUE + letters[i][j] + " ");
                } else {
                    System.out.print(SET_TEXT_COLOR_RED + letters[i][j] + " ");
                }

            }
            System.out.println("\u001B[0m");
        }
        System.out.println(" h g f e d c b a");
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
                } else if (Objects.equals(cmd, "create")) {
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
                        var f = myserverf.listgames(auth.authToken());
                        //System.out.println(f.get());
                        System.out.println(f);
                    } catch (ResponseException e) {
                        System.out.println("err");
                    }
                } else if (Objects.equals(cmd, "join")) {
                    if (params.length == 2) {
                        try {
                            if ((Objects.equals(auth.authToken(), WHITE) || Objects.equals(auth.authToken(), BLACK)) && Objects.equals(game, params[0])) {
                                if (Objects.equals(params[1], "white")) {
                                    printChessboardWhite();
                                    printChessboardBlack();

                                } else {
                                    printChessboardBlack();
                                    printChessboardWhite();
                                }
                            } else {
                                myserverf.joingame(auth.authToken(), new wrapper(params[1], params[0]));
                                System.out.println("success");
                                if (Objects.equals(params[1], "white")) {
                                    printChessboardWhite();
                                    printChessboardBlack();
                                    WHITE = auth.authToken();
                                    game = params[0];
                                } else {
                                    printChessboardBlack();
                                    printChessboardWhite();
                                    BLACK = auth.authToken();
                                    game = params[0];
                                }
                                game = params[0];
                            }
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
                        myserverf.observer(auth.authToken(), new wrapper("", params[0]));
                            printChessboardWhite();
                            printChessboardBlack();
                        System.out.println("success");
                        System.out.println(server.getgame());
                        }
                        catch (ResponseException e){
                            System.out.println("err");
                        } catch (DataAccessException e) {
                            System.out.println("getgame err");
                        }
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

    public class wrapper {
        @SerializedName("playerColor")
        private String playerColor;
        @SerializedName("gameID")
        private String gameID;

        public wrapper(String playerColor, String gameID) {
            this.playerColor = playerColor;
            this.gameID = gameID;
        }
    }
    public class GamesWrapper {
        @SerializedName("games")
        private GameData[] games;

        public GamesWrapper(GameData[] games) {
            this.games = games;
        }

        public GameData[] getGames() {
            return games;
        }
    }

}
