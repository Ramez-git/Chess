package ui;

import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.wrapper;
import server.ServerFacade;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class user {
    public final replier repl;
    private final ServerFacade serverFacade;
    public String authToken = null;
    public String url;
    private ui.state state = ui.state.LOGGEDOUT;
    private String list;
    private ingame playclient;

    public user(String serverURL, replier repl) {
        serverFacade = new ServerFacade(8080);
        this.url = serverURL;
        this.repl = repl;
    }

    public void eval(String input) {
        if (state == ui.state.GAMEPLAY) {
            state = playclient.eval(input);
            return;
        }
        switch (input) {
            case "help" -> help();
            case "login" -> login();
            case "register" -> register();
            case "logout" -> logout();
            case "create" -> create();
            case "list" -> list();
            case "join" -> join();
            case "observe" -> observe();
            default -> repl.printMsg("err");
        }
    }

    private void help() {
        if (state == ui.state.LOGGEDOUT) {
            System.out.println(SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>"
                    + SET_TEXT_COLOR_WHITE + " - to create an account");
            System.out.println(SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>"
                    + SET_TEXT_COLOR_WHITE + " - to play chess");
            System.out.println(SET_TEXT_COLOR_BLUE + "quit"
                    + SET_TEXT_COLOR_WHITE + " - playing chess");
            System.out.println(SET_TEXT_COLOR_BLUE + "help"
                    + SET_TEXT_COLOR_WHITE + " - with possible commands\n");
        } else if (state == ui.state.LOGGEDIN) {
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
    }

    private void login() {
        if (state == ui.state.LOGGEDOUT) {
            repl.printMsg("Enter your username:");
            String username = repl.scanWord();
            repl.printMsg("Enter your password:");
            String password = repl.scanWord();

            var x =serverFacade.login(new UserData(username, password,""));
            authToken = new Gson().fromJson(x, AuthData.class).authToken();
            state = ui.state.LOGGEDIN;
        } else {
            repl.printMsg("err");
        }
    }

    private void register() {
        if (state == ui.state.LOGGEDOUT) {
            repl.printMsg("username:");
            String username = repl.scanWord();
            repl.printMsg("email:");
            String email = repl.scanWord();
            repl.printMsg("password:");
            String password = repl.scanWord();

            authToken = new Gson().fromJson(serverFacade.register(new UserData(username, password, email)),AuthData.class).authToken();
            state = ui.state.LOGGEDIN;
        } else {
            repl.printMsg("err");
        }
    }

    private void logout() {
        if (state == ui.state.LOGGEDIN) {
            serverFacade.logout(authToken);
            authToken = null;
            state = ui.state.LOGGEDOUT;
            repl.printMsg("bye");
        } else {
            repl.printMsg("err");
        }
    }

    private void create() {
        if (state == ui.state.LOGGEDIN) {
            repl.printMsg("name of your game:");
            String name = repl.scanWord();
            serverFacade.creategame(new gamename1(name), authToken);
            repl.printMsg("success");
        } else {
            repl.printMsg("err");
        }
    }

    private void list() {
        if (state == ui.state.LOGGEDIN) {
            this.list = serverFacade.listgames1(authToken);
            if (this.list.isEmpty()) {
                repl.printMsg("There are no games in the database");
                return;
            }
            else{
                repl.printMsg(list);
            }
        } else {
            repl.printMsg("err");
        }
    }

    private void join() {
        if (state == ui.state.LOGGEDIN) {
            repl.printMsg("game number:");
            int index = Integer.parseInt(repl.scanWord());
            repl.printMsg("Enter the color you wish to play as (white/black):");
            String colorStr = repl.scanWord();
            if (!(colorStr.equals("white") || colorStr.equals("black"))) {
                repl.printErr("invalid color");
                return;
            }

            serverFacade.joingame(authToken, new wrapper(colorStr,String.valueOf(index)));
            TeamColor color = colorStr.equals("white") ? TeamColor.WHITE : TeamColor.BLACK;
            playclient = new ingame(new Gson().fromJson(serverFacade.getgame(authToken,index),GameData.class), color, this);
            state = ui.state.GAMEPLAY;
        } else {
            repl.printMsg("err");
        }

    }

    private void observe() {
        if (state == ui.state.LOGGEDIN) {
            if (list == null) {
                repl.printErr("You must list the games before you can choose one to observe");
                return;
            }
            repl.printMsg("game number:");
            int index = Integer.parseInt(repl.scanWord()) - 1;
            serverFacade.observer(authToken,new wrapper("",String.valueOf(index)));

            GameData gameData = new Gson().fromJson(serverFacade.getgame(authToken,index),GameData.class);
            playclient = new ingame(gameData, null, this);
            state = ui.state.GAMEPLAY;
        } else {
            repl.printMsg("err");
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
