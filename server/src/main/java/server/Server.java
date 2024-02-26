package server;

import com.google.gson.Gson;
import spark.*;

import java.lang.module.ResolutionException;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //register

        Spark.init();
        Spark.awaitInitialization();
        Spark.post("/user",this::addUser);
        Spark.post("/session",this::loginUser);
        Spark.post("/game",this::createGame);
        Spark.delete("/session", this::deleteSession);
        Spark.delete("/db", this::deleteEVERYTHING);
        Spark.put("/game",this::joinGame);
        Spark.get("/game", this::listGames);
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object addUser(Request req,Response res) throws ResolutionException{
        return new Gson().toJson("acknowledged addUser");
    }
    private Object loginUser(Request req, Response res) throws ResolutionException{
        return new Gson().toJson("acknowledged loginUser");
    }
    private Object deleteSession(Request req, Response res) throws ResolutionException{
        return new Gson().toJson("acknowledged deleteSession");
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
        return new Gson().toJson("acknowledged deleteEVERYTHING");
    }
}
