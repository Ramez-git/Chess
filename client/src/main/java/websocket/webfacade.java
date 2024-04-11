package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import ui.UIclient;
import ui.reply;
import ui.states;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.ServerException;

public class webfacade extends Endpoint {
    Session session;
    UIclient currclient;
    public webfacade(UIclient currclient) throws URISyntaxException, DeploymentException, IOException {

        this.currclient = currclient;
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
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
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}
    private void error(String message) {
        String msg = new Gson().fromJson(message, String.class);
        System.err.println("ERROR: " + msg);
    }
    private void loadGame(String message) {
        if(currclient.clientstate == states.WHITE){
    currclient.mygame.getBoard().toString1(ChessGame.TeamColor.WHITE,null);
        }
        else{
            currclient.mygame.getBoard().toString1(ChessGame.TeamColor.BLACK,null);
        }
    }
    private void notification(String message) {
        String msg = new Gson().fromJson(message, String.class);
        System.out.println(msg);
    }
    public void makeMove(String auth, int gameID, ChessMove move) throws ServerException {
        try {
            UserGameCommand.MakeMoveCommand cmd = new UserGameCommand.MakeMoveCommand(auth, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException("err");
        }
    }
    public void joinPlayer(String auth, int gameID, ChessGame.TeamColor color) throws ServerException {
        try {
            UserGameCommand.JoinPlayerCommand cmd = new UserGameCommand.JoinPlayerCommand(auth, gameID, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException("err");
        }
    }
    public void joinObserver(String auth, int gameID) throws ServerException {
        try {
            UserGameCommand.JoinObserverCommand cmd = new UserGameCommand.JoinObserverCommand(auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException("err");
        }
    }
    public void leave(String auth, int gameID) throws ServerException {
        try {
            UserGameCommand.LeaveCommand cmd = new UserGameCommand.LeaveCommand(auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException("err");
        }
    }
    public void resign(String auth, int gameID) throws ServerException {
        try {
            UserGameCommand.ResignCommand cmd = new UserGameCommand.ResignCommand(auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException("err");
        }
    }
}
