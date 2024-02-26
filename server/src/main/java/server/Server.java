package server;

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
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object addUser(Request req,Response res) throws ResolutionException{
        return "acknowledged";
    }

}
