package ui;

import chess.ChessGame;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dataAccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.wrapper;
import server.ServerFacade;
import websocket.webfacade;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.ServerException;
import java.util.*;

import static ui.EscapeSequences.*;

public class reply {
    private Scanner input;
    private UIclient currclient;
    private webfacade webf;

    public reply(String serverurl) throws DataAccessException {

        input = new Scanner(System.in);
        this.currclient = new UIclient(serverurl, this);
    }

    public void run() {
        var myserverf = new ServerFacade(8080);
        System.out.println(BLACK_QUEEN + " Welcome to 240 chess. Type Help to get started. " + BLACK_QUEEN);
        while (!currclient.quit) {
            if (currclient.clientstate != states.LOGEDOUT) {
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
                            System.out.println(myserverf.creategame(new gamename1(params[0]), currclient.auth.authToken()));
                        } catch (ResponseException e) {
                            System.out.println("err");
                        }
                    } else {
                        System.out.println("err");
                    }
                } else if (Objects.equals(cmd, "list")) {
                    try {
                        var f = myserverf.listgames1(currclient.auth.authToken());
                        System.out.println(f);
                    } catch (ResponseException e) {
                        System.out.println("err");
                    }

                } else if (Objects.equals(cmd, "redraw")) {
                    System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.WHITE, null));
                    System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.BLACK, null));
                } else if (Objects.equals(cmd, "resign")) {
                    System.out.println("are you sure?");
                    if (Objects.equals(input.nextLine(), "yes")) {
                        System.out.println("success");
                        currclient.mygame = null;
                        currclient.clientstate=states.LOGEDOUT;
                    }
                } else if (Objects.equals(cmd, "highlight")) {
                    if (params.length == 1) {
                        try {
                            String[] parts = params[0].split(",");
                            int row1 = Integer.parseInt(parts[0]);
                            int column1 = Integer.parseInt(parts[1]);
                            var x = new ChessPosition(row1, column1);
                            var validmoves = currclient.mygame.validMoves(x);
                            Collection<ChessPosition> moves = new ArrayList<>(List.of());
                            for (var move : validmoves) {
                                moves.add(move.getEndPosition());
                            }
                            if (currclient.clientstate==states.WHITE) {
                                System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.WHITE, moves));
                            } else {
                                System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.BLACK, moves));
                            }
                        } catch (RuntimeException e) {
                            if (currclient.clientstate==states.WHITE) {
                                System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.WHITE, null));
                            } else {
                                System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.BLACK, null));
                            }
                        }
                    }
                } else if (Objects.equals(cmd, "join")) {
                    if (params.length == 2) {
                        try {
                            var x = myserverf.joingame(currclient.auth.authToken(), new wrapper(params[1], params[0]));
                            System.out.println("success");
                            if (Objects.equals(params[1], "white")) {
                                currclient.mygame = new Gson().fromJson(x.toString(), GameData.class).game();
                                System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.WHITE, null));
                                currclient.clientstate=states.WHITE;
                                this.webf = new webfacade(currclient);
                                this.webf.joinPlayer(currclient.auth.authToken(), Integer.parseInt(params[0]), ChessGame.TeamColor.WHITE);
                                currclient.id=Integer.parseInt(params[0]);
                            } else {
                                var mygame = new Gson().fromJson(x.toString(), GameData.class).game().getBoard();
                                System.out.println(mygame.toString1(ChessGame.TeamColor.BLACK, null));
                                this.webf = new webfacade(currclient);
                                this.webf.joinPlayer(currclient.auth.authToken(), Integer.parseInt(params[0]), ChessGame.TeamColor.BLACK);
                                currclient.id=Integer.parseInt(params[0]);
                                currclient.clientstate=states.BLACK;
                            }
                            currclient.game = params[0];

                        } catch (ResponseException e) {
                            System.out.println("err");
                        } catch (DeploymentException | URISyntaxException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println("err");
                    }
                } else if (Objects.equals(cmd, "leave")) {
                    currclient.mygame = null;
                    currclient.clientstate=states.LOGEDIN;
                    System.out.println("success");
                } else if (Objects.equals(cmd, "move")) {
                    if (params.length == 2 && (currclient.clientstate==states.WHITE || currclient.clientstate==states.BLACK)) {
                        try {
                            String[] parts = params[0].split(",");
                            int row1 = Integer.parseInt(parts[0]);
                            int column1 = Integer.parseInt(parts[1]);
                            parts = params[1].split(",");
                            int row2 = Integer.parseInt(parts[0]);
                            int column2 = Integer.parseInt(parts[1]);
                            var x = new ChessPosition(row1, column1);
                            var validmoves = currclient.mygame.validMoves(x);
                            for (var move : validmoves) {
                                if (move.getEndPosition().getRow() + 1 == row2 && move.getEndPosition().getColumn() + 1 == column2) {
                                    currclient.mygame.makeMove(move);
                                    if (currclient.clientstate==states.WHITE) {
                                        System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.WHITE, null));
                                    } else if (currclient.clientstate==states.BLACK) {
                                        System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.BLACK, null));
                                    }
                                    webf.makeMove(currclient.auth.authToken(),currclient.id,move);
                                    break;
                                }
                            }


                        } catch (InvalidMoveException e) {
                            throw new RuntimeException("move not legal");
                        } catch (ServerException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else if (Objects.equals(cmd, "observe")) {
                    if (params.length == 1) {
                        try {
                            var x = myserverf.observer(currclient.auth.authToken(), new wrapper("", params[0]));
                            System.out.println("\n");
                            currclient.mygame = new Gson().fromJson(x.toString(), GameData.class).game();
                            System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.WHITE, null));
                            System.out.println(currclient.mygame.getBoard().toString1(ChessGame.TeamColor.BLACK, null));
                            currclient.clientstate = states.OBSERVING;
                            this.webf = new webfacade(currclient);
                            this.webf.joinObserver(currclient.auth.authToken(), Integer.parseInt(params[0]));
                            System.out.println("success");
                            currclient.id=Integer.parseInt(params[0]);
                        } catch (ResponseException e) {
                            System.out.println("err");
                        } catch (DeploymentException | URISyntaxException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println("err");
                    }
                } else if (Objects.equals(cmd, "logout")) {
                    try {
                        myserverf.logout(currclient.auth.authToken());
                        System.out.println("success");
                        currclient.clientstate = states.LOGEDOUT;
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
                            currclient.auth = new Gson().fromJson(String.valueOf(str), AuthData.class);
                            System.out.println("Success");
                            currclient.clientstate = states.LOGEDIN;
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
                            currclient.auth = new Gson().fromJson(String.valueOf(str), AuthData.class);
                            System.out.println("Success");
                            currclient.clientstate = states.LOGEDIN;
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

