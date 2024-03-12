package server;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.lang.module.ResolutionException;
import java.sql.SQLException;
import java.util.Objects;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final AuthService authService;


    public Server(){
        try {
            final DataAccessAuth authdata = new mySqlAuth();
            this.authService = new AuthService(authdata);
            final DataAccessUser userdata = new mySqlUser(authService);
            this.userService = new UserService(userdata);
            final DataAccessgame gamedata = new mysqlGame();
            this.gameService = new GameService(gamedata);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);


        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //register

        Spark.init();
        Spark.awaitInitialization();
        //making endpoints
        Spark.post("/user", this::addUser);
        Spark.post("/session", this::loginUser);
        Spark.post("/game", this::createGame);
        Spark.delete("/session", this::deleteSession);
        Spark.delete("/db", this::deleteEVERYTHING);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);
        //end making endpoints
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //functions for endpoints
    private Object addUser(Request req, Response res) throws ResolutionException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        UserService userService1 = userService;
        try {
            var myauth = userService1.CreateUser(user);
            res.status(200);
            res.body(new Gson().toJson(myauth));
            return new Gson().toJson(myauth);
        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "Error: already taken")) {
                res.status(403);
                res.body(new Gson().toJson(new message("Error: already taken")));
                return new Gson().toJson(new message("Error: already taken"));

            } else if (Objects.equals(e.getMessage(), "Error: description")) {
                res.status(500);
                res.body(new Gson().toJson(new message("Error: description")));
                return new Gson().toJson(new message("Error: description"));
            } else if (Objects.equals(e.getMessage(), "Error: bad request")) {
                res.status(400);
                res.body(new Gson().toJson(new message("Error: bad request")));
                return new Gson().toJson(new message("Error: bad request"));
            } else {
                return new Gson().toJson("err handler on createuser");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private Object loginUser(Request req, Response res) throws ResolutionException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        UserService userService1 = userService;
        try {
            var myauth = userService1.login(user);
            res.status(200);
            res.body(new Gson().toJson(myauth));
            return new Gson().toJson(myauth);
        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "Error: unauthorized")) {
                res.status(401);
                res.body(new Gson().toJson(new message("Error: unauthorized")));
                return new Gson().toJson(new message("Error: unauthorized"));

            } else if (Objects.equals(e.getMessage(), "Error: description")) {
                res.status(500);
                res.body(new Gson().toJson(new message("Error: description")));
                return new Gson().toJson(new message("Error: description"));
            } else {
                return new Gson().toJson("err handler on login");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object deleteSession(Request req, Response res) throws ResolutionException {
        var theauth = req.headers("Authorization");
        var authService1 = authService;
        try {
            authService1.deleteSession(new AuthData(theauth, ""));
            res.status(200);
            return new Gson().toJson(null);
        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "Error: unauthorized")) {
                res.status(401);
                res.body(new Gson().toJson(new message("Error: unauthorized")));
                return new Gson().toJson(new message("Error: unauthorized"));
            } else {
                res.status(500);
                res.body(new Gson().toJson(new message("Error: description")));
                return new Gson().toJson(new message("Error: description"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object listGames(Request req, Response res) throws ResolutionException, DataAccessException {
        var authstr = req.headers("Authorization");
        var authService1 = authService;
        var gameService1 = gameService;
        var myauth = new AuthData(authstr, "");
        try {
            authService1.getusr(myauth);
            res.status(200);
            return new Gson().toJson(new GamesWrapper(gameService1.listGames()));
        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "usr does not exist")) {
                res.status(401);
                return new Gson().toJson(new message("Error: unauthorized"));
            } else {
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private Object createGame(Request req, Response res) throws ResolutionException, DataAccessException {
        try {
            var theauth = req.headers("Authorization");
            var authser = authService;
            authser.getusr(new AuthData(theauth, ""));
            var gameService1 = gameService;
            var game = new Gson().fromJson(req.body(), gamename.class);
            var id = gameService1.createGame(game.getGameName());
            return new Gson().toJson(new gameID(id));
        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "usr does not exist")) {
                res.status(401);
                return new Gson().toJson(new message("Error: unauthorized"));
            } else {
                res.status(500);
                res.body(new Gson().toJson(new message("Error: description")));
                return new Gson().toJson(new message("Error: description"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private Object joinGame(Request req, Response res) throws ResolutionException {
        var game = new Gson().fromJson(req.body(), gamecolorr.class);
        var authstr = req.headers("Authorization");
        var gameService1 = gameService;
        var authService1 = authService;
        try {
            if (Objects.equals(game.playerColor, "WHITE")) {
                var x = Integer.parseInt(game.gameID);
                var y = authService1.getusr(new AuthData(authstr, "")).username();
                gameService1.updateGame(x, y, "check");
                res.status(200);
                return "";
            } else if (Objects.equals(game.playerColor, "BLACK")) {
                var x = Integer.parseInt(game.gameID);
                var y = authService1.getusr(new AuthData(authstr, "")).username();
                gameService1.updateGame(x, "check", y);
                res.status(200);
                return "";
            } else {
                authService1.getusr(new AuthData(authstr, ""));
                var x = Integer.parseInt(game.gameID);
                gameService1.updateGame(x, null, null);
                res.status(200);
                return "";
            }

        } catch (DataAccessException | SQLException e) {
            if (Objects.equals(e.getMessage(), "usr does not exist")) {
                res.status(401);
                return new Gson().toJson(new message("Error: unauthorized"));
            } else if (Objects.equals(e.getMessage(), "game does not exist")) {
                res.status(400);
                return new Gson().toJson(new message("Error: bad request"));

            } else if (Objects.equals(e.getMessage(), "Error: already taken")) {
                res.status(403);
                return new Gson().toJson(new message("Error: already taken"));
            }
            throw new RuntimeException(e);
        }

    }

    private Object deleteEVERYTHING(Request req, Response res) throws ResolutionException {
        try {
            var authService1 = authService;
            var gameService1 = gameService;
            var userService1 = userService;
            authService1.deleteAll();
            gameService1.deleteAll();
            userService1.deleteAll();
            res.status(200);
            return new Gson().toJson(null);
        } catch (DataAccessException | SQLException e) {
            res.status(500);
            res.body(new Gson().toJson(new message("Error: description")));
            return new Gson().toJson(new message("Error: description"));
        }
    }

    public static class gamecolorr {
        @SerializedName("playerColor")
        private String playerColor;
        @SerializedName("gameID")
        private String gameID;

        public String getPlayerColor() {
            return playerColor;
        }

        public String getGameID() {
            return gameID;
        }
    }

    //end functions for endpoints
    public class gamename {
        @SerializedName("gameName")
        private String gameName;

        public String getGameName() {
            return gameName;
        }
    }

    public class message {
        @SerializedName("message")
        private String message;

        public message(String message) {
            this.message = message;
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

    public class gameID {
        @SerializedName("gameID")
        private Integer gameID;

        public gameID(int ID) {
            this.gameID = ID;
        }

        public Integer getGameID() {
            return gameID;
        }
    }
}
