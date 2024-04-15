package websocket;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import ui.ingame;
import ui.replier;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint {
    Session curr;
    replier rep;
    ingame client;

    public WebSocketFacade(String url, ingame client) {
        this.rep = client.repl;
        this.client = client;

        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.curr = container.connectToServer(this, socketURI);

            this.curr.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType msgType = serverMessage.getServerMessageType();
                    switch (msgType) {
                        case ERROR -> error(message);
                        case LOAD_GAME -> loadGame(message);
                        case NOTIFICATION -> notification(message);
                    }
                }
            });
        } catch (DeploymentException e) {
            rep.printMsg("err deployment");
        } catch (URISyntaxException e) {
            rep.printMsg("err URI");
        } catch (IOException e) {
            rep.printMsg("err IOException");
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    private void error(String message) {
        ErrorMessage msg = new Gson().fromJson(message, ErrorMessage.class);
        rep.printMsg("\b\b\b");
        rep.printErr(msg.errorMessage);
        rep.printPrompt();
    }

    private void loadGame(String message) {
        LoadGameMessage msg = new Gson().fromJson(message, LoadGameMessage.class);
        client.game = msg.game;
        client.draw();
        rep.printPrompt();
    }

    private void notification(String message) {
        NotificationMessage msg = new Gson().fromJson(message, NotificationMessage.class);
        rep.printMsg(msg.message);
        rep.printPrompt();
    }

    public void makeMove(String auth, int gameID, ChessMove move,String Check){
        try {
            webSocketMessages.userCommands.move cmd = new move(auth, gameID, move, Check);
            this.curr.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            rep.printMsg("err");
        }
    }

    public void joinPlayer(String auth, int gameID, ChessGame.TeamColor color){
        try {
            join cmd = new join(auth, gameID, color);
            this.curr.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            rep.printMsg("err");
        }
    }

    public void joinObserver(String auth, int gameID) {
        try {
            observer cmd = new observer(auth, gameID);
            this.curr.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            rep.printMsg("err");
        }
    }

    public void leave(String auth, int gameID){
        try {
            leave cmd = new leave(auth, gameID);
            this.curr.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            rep.printMsg("err");
        }
    }

    public void resign(String auth, int gameID){
        try {
            resign cmd = new resign(auth, gameID);
            this.curr.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            rep.printMsg("err");
        }
    }
}
