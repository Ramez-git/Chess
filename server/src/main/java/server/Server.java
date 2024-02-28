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
import java.util.Collection;
import java.util.Objects;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final AuthService authService;


    public Server() {
        final DataAccessAuth authdata = new MemoryDataAccessAuth();
        this.authService = new AuthService(authdata);
        final DataAccessUser userdata = new MemoryDataAccessUser(authService);
        this.userService = new UserService(userdata);
        final DataAccessgame gamedata = new MemoryDataAccessGame();
        this.gameService = new GameService(gamedata);


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
        UserService u = userService;
        try {
            var myauth = u.CreateUser(user);
            res.status(200);
            res.body(new Gson().toJson(myauth));
            return new Gson().toJson(myauth);
        } catch (DataAccessException e) {
            if (e.getMessage() == "Error: already taken") {
                res.status(403);
                res.body(new Gson().toJson("\"message\": \"Error: already taken\""));
                return new Gson().toJson(new message("Error: already taken"));

            } else if (e.getMessage() == "Error: description") {
                res.status(500);
                res.body(new Gson().toJson(new message("Error: description")));
                return new Gson().toJson(new message("Error: description"));
            } else if (e.getMessage() == "Error: bad request") {
                res.status(400);
                res.body(new Gson().toJson(new message("Error: bad request")));
                return new Gson().toJson(new message("Error: bad request"));
            } else {
                return new Gson().toJson("err handler on createuser");
            }

        }

    }

    private Object loginUser(Request req, Response res) throws ResolutionException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        UserService u = userService;
        try {
            var myauth = u.login(user);
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
        }
    }

    private Object deleteSession(Request req, Response res) throws ResolutionException {
        var theauth = req.headers("Authorization");
        var a = authService;
        try {
            a.deleteSession(new AuthData(theauth, ""));
            res.status(200);
            return new Gson().toJson("logged out");
        } catch (DataAccessException e) {
            if (e.getMessage() == "Error: unauthorized") {
                res.status(401);
                res.body(new Gson().toJson(new message("Error: unauthorized")));
                return new Gson().toJson("\"message\": \"Error: unauthorized\"");
            } else {
                res.status(500);
                res.body(new Gson().toJson("\"message\": \"Error: description\""));
                return new Gson().toJson("\"message\": \"Error: description\"");
            }
        }
    }

    private Object listGames(Request req, Response res) throws ResolutionException, DataAccessException {
        var authstr = req.headers("Authorization");
        var x = authService;
        var g = gameService;
        var myauth = new AuthData(authstr, "");
        try {
            x.getusr(myauth);

            return new Gson().toJson(new GamesWrapper(g.listGames()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private Object createGame(Request req, Response res) throws ResolutionException, DataAccessException {
        var g = gameService;
        var game = new Gson().fromJson(req.body(), gamename.class);
        var id = g.createGame(game.getGameName());
        return id;
    }

    private Object joinGame(Request req, Response res) throws ResolutionException {
        var game = new Gson().fromJson(req.body(), gamecolorr.class);
        var authstr = req.headers("Authorization");
        var g = gameService;
        var a = authService;
        try {
            if (Objects.equals(game.playerColor, "WHITE")) {
                var x = Integer.parseInt(game.gameID);
                var y = a.getusr(new AuthData(authstr, "")).username();
                g.updateGame(x, y, "check");
                return new Gson().toJson("it worked");
            } else if (Objects.equals(game.playerColor, "BLACK")) {
                var x = Integer.parseInt(game.gameID);
                var y = a.getusr(new AuthData(authstr, "")).username();
                g.updateGame(x, "check", y);
            } else {
                var x = Integer.parseInt(game.gameID);
                g.updateGame(x, "", "");
            }

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Object deleteEVERYTHING(Request req, Response res) throws ResolutionException {
        try {
            var c = authService;
            var g = gameService;
            var x = userService;
            c.deleteAll();
            g.deleteAll();
            x.deleteAll();
            res.status(200);
            return new Gson().toJson(null);
        } catch (DataAccessException e) {
            res.status(500);
            res.body(new Gson().toJson("\"message\": \"Error: description\""));
            return new Gson().toJson("\"message\": \"Error: description\"");
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
        private Collection<GameData> games;

        public GamesWrapper(Collection<GameData> games) {
            this.games = games;
        }

        public Collection<GameData> getGames() {
            return games;
        }
    }

}
