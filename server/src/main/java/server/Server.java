package server;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import service.*;
import spark.*;
//import MemoryDataAccess.*;
import java.lang.module.ResolutionException;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final clear clearServiceusr;
    private final clear clearServicegame;


    public Server(){
        final DataAccess userdata = new MemoryDataAccess();
        this.userService = new UserService(userdata);
        final DataAccess gamedata = new MemoryDataAccess();
        this.gameService = new GameService(gamedata);
        this.clearServiceusr = new clear(userdata);
        this.clearServicegame = new clear(gamedata);


    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //register

        Spark.init();
        Spark.awaitInitialization();
        //making endpoints
        Spark.post("/user",this::addUser);
        Spark.post("/session",this::loginUser);
        Spark.post("/game",this::createGame);
        Spark.delete("/session", this::deleteSession);
        Spark.delete("/db", this::deleteEVERYTHING);
        Spark.put("/game",this::joinGame);
        Spark.get("/game", this::listGames);
        //end making endpoints
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    //functions for endpoints
    private Object addUser(Request req,Response res) throws ResolutionException{
        var user = new Gson().fromJson(req.body(), UserData.class);
        UserService u =userService;
        try {
            var myauth=u.CreateUser(user);
            res.status(200);
            res.body(new Gson().toJson(myauth));
            return new Gson().toJson(myauth);
        } catch (DataAccessException e) {
            if(e.getMessage()=="Error: already taken"){
                res.status(403);
                res.body(new Gson().toJson("\"message\": \"Error: already taken\""));
                return new Gson().toJson("\"message\": \"Error: already taken\"");

            } else if (e.getMessage() =="Error: description" ) {
                res.status(500);
                res.body(new Gson().toJson("\"message\": \"Error: description\""));
                return new Gson().toJson("\"message\": \"Error: description\"");
            }
            else if(e.getMessage() == "Error: bad request"){
                res.status(400);
                res.body(new Gson().toJson("\"message\": \"Error: bad request\""));
                return new Gson().toJson("\"message\": \"Error: bad request\"");
            }
            else{
                return new Gson().toJson("err hanler on createuser");
            }

        }

    }
    private Object loginUser(Request req, Response res) throws ResolutionException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        UserService u = userService;
        try {
            var myauth=u.login(user);
            res.status(200);
            res.body(new Gson().toJson(myauth));
            return new Gson().toJson(myauth);
        } catch (DataAccessException e) {
            if(e.getMessage()=="Error: unauthorized"){
                res.status(401);
                res.body(new Gson().toJson("\"message\": \"Error: unauthorized\""));
                return new Gson().toJson("\"message\": \"Error: unauthorized\"");

            } else if (e.getMessage() =="Error: description" ) {
                res.status(500);
                res.body(new Gson().toJson("\"message\": \"Error: description\""));
                return new Gson().toJson("\"message\": \"Error: description\"");
            }
            else{
                return new Gson().toJson("err handler on login");
            }
        }
    }
    private Object deleteSession(Request req, Response res) throws ResolutionException{
        var theauth = new Gson().fromJson(req.body(), AuthData.class);
        var a = clearServiceusr;
        try{
            a.deleteSession(theauth);
            res.status(200);
            return null;
        } catch (DataAccessException e) {
            if(e.getMessage()=="User not logged in thus his authtoken can't be deleted"){
                res.status(401);
                res.body(new Gson().toJson("\"message\": \"Error: unauthorized\""));
                return new Gson().toJson("\"message\": \"Error: unauthorized\"");
            }
            else{
                res.status(500);
                res.body(new Gson().toJson("\"message\": \"Error: description\""));
                return new Gson().toJson("\"message\": \"Error: description\"");
            }
        }
    }
    private Object listGames(Request req, Response res) throws ResolutionException{
        return new Gson().toJson("acknowledged listGame");
    }
    private Object createGame(Request req, Response res) throws ResolutionException{
        return new Gson().toJson("acknowledged createGame");
    }
    private Object joinGame(Request req, Response res) throws ResolutionException{
        return new Gson().toJson("acknowledged joinGame");
    }
    private Object deleteEVERYTHING(Request req, Response res) throws ResolutionException{
        try{
            var c = clearServiceusr;
            var g = clearServicegame;
            c.DELETEALL();
            g.DELETEALL();
            res.status(200);
            return null;
        } catch (DataAccessException e) {
            res.status(500);
            res.body(new Gson().toJson("\"message\": \"Error: description\""));
            return new Gson().toJson("\"message\": \"Error: description\"");
        }
    }
    //end functions for endpoints
}
