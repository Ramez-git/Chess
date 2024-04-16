package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import service.AuthService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.HashMap;


@WebSocket
public class WebSocketHandler {
    private final HashMap<Integer, manager> connections;
    private final mySqlAuth myauth;
    private final mysqlGame mygame;
    private final mySqlUser myuser;
    private final AuthService authService;

    public WebSocketHandler() throws DataAccessException {
        connections = new HashMap<Integer, manager>();
        final DataAccessAuth authdata = new mySqlAuth();
        this.authService = new AuthService(authdata);
        myauth = new mySqlAuth();
        mygame = new mysqlGame(authService);
        myuser = new mySqlUser(authService);

    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(session, message);
            case JOIN_OBSERVER -> joinObserver(session, message);
            case MAKE_MOVE -> move(session, message);
            case LEAVE -> leave(session, message);
            case RESIGN -> resign(session, message);
        }
    }
    private void joinPlayer(Session session, String msg) throws IOException {
        join cmd = new Gson().fromJson(msg, join.class);
        try {
            GameData gameData = getGame(cmd.gameID);
            AuthData authData = myauth.getusr(new AuthData(cmd.getAuthString(), ""));

            if (cmd.playerColor == ChessGame.TeamColor.WHITE) {
                if (!authData.username().equals(gameData.whiteUsername())) {
                    ErrorMessage err = new ErrorMessage("Error: you are not the " +
                            cmd.playerColor +
                            " player in this game");
                    session.getRemote().sendString(new Gson().toJson(err));
                    return;
                }
            }
            else {
                if (!authData.username().equals(gameData.blackUsername())) {
                    ErrorMessage err = new ErrorMessage("Error: you are not the " +
                            cmd.playerColor +
                            " player in this game");
                    session.getRemote().sendString(new Gson().toJson(err));
                    return;
                }
            }
            addConnection(session, cmd.gameID, authData.username());
            LoadGameMessage load = new LoadGameMessage(gameData);
            session.getRemote().sendString(new Gson().toJson(load));
            NotificationMessage notification = new NotificationMessage(authData.username() +
                    "joined the game as the " +
                    cmd.playerColor + " player");
            this.connections.get(cmd.gameID).broadcast(authData.username(), notification);
        }
        catch (Exception ex) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }
    private void joinObserver(Session session, String msg) throws IOException {
        observer cmd = new Gson().fromJson(msg, observer.class);
        try {
            GameData gameData = getGame(cmd.gameID);
            AuthData authData = myauth.getusr(new AuthData(cmd.getAuthString(), ""));
            addConnection(session, cmd.gameID, authData.username());
            LoadGameMessage load = new LoadGameMessage(gameData);
            session.getRemote().sendString(new Gson().toJson(load));
            NotificationMessage notification = new NotificationMessage(authData.username() +
                    "joined the game as the an observer");
            this.connections.get(cmd.gameID).broadcast(authData.username(), notification);
        }
        catch (Exception ex) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }
    private void addConnection(Session session, int gameID, String username) {
        if (this.connections.containsKey(gameID)) {
            this.connections.get(gameID).add(username, session);
        }
        else {
            manager manager = new manager();
            manager.add(username, session);
            this.connections.put(gameID, manager);
        }
    }

    private void move(Session session, String msg) throws IOException {
        move cmd = new Gson().fromJson(msg, move.class);
        try {
            GameData gameData = getGame(cmd.gameID);
            AuthData authData = myauth.getusr(new AuthData(cmd.getAuthString(), ""));
            ChessGame.TeamColor pieceColor = gameData.game().getBoard().getPiece(cmd.move.getStartPosition()).getTeamColor();
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                if (gameData.whiteUsername()!= null&&!authData.username().equals(gameData.whiteUsername())) {
                    ErrorMessage err = new ErrorMessage("Error: you are not the " +
                            pieceColor +
                            " player in this game");
                    session.getRemote().sendString(new Gson().toJson(err));
                    return;
                }
            }
            else {
                if (gameData.blackUsername()!=null&&!authData.username().equals(gameData.blackUsername())) {
                    ErrorMessage err = new ErrorMessage("Error: you are not the " +
                            pieceColor +
                            " player in this game");
                    session.getRemote().sendString(new Gson().toJson(err));
                    return;
                }
            }


            gameData.game().makeMove(cmd.move);
            mygame.updateGame2(gameData.gameID(), gameData.game());
            NotificationMessage notification = new NotificationMessage(authData.username() + " just made a move.");
            connections.get(cmd.gameID).broadcast(authData.username(), notification);
            if(gameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)||gameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)){
                NotificationMessage notification1 = new NotificationMessage("CheckMate");
                connections.get(cmd.gameID).broadcast(null, notification1);
            }
            else if(gameData.game().isInCheck(ChessGame.TeamColor.WHITE)||gameData.game().isInCheck(ChessGame.TeamColor.BLACK)){
                NotificationMessage notification1 = new NotificationMessage("Check");
                connections.get(cmd.gameID).broadcast(null, notification1);
            }
            LoadGameMessage load = new LoadGameMessage(gameData);
            session.getRemote().sendString(new Gson().toJson(load));
            connections.get(cmd.gameID).broadcast(authData.username(), load);
        }
        catch (Exception ex) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }
    private void leave(Session session, String msg) throws IOException {
        try {
            leave cmd = new Gson().fromJson(msg, leave.class);
            AuthData authData = myauth.getusr(new AuthData(cmd.getAuthString(), ""));
            GameData gameData = getGame(cmd.gameID);
            if (authData.username().equals(gameData.whiteUsername())) {
                mygame.leaveGame(gameData.gameID(), "white");
            }
            else if (authData.username().equals(gameData.blackUsername())) {
                mygame.leaveGame(gameData.gameID(), "black");
            }

            connections.get(cmd.gameID).remove(authData.username());
            NotificationMessage notification = new NotificationMessage(authData.username() + " left the game.");
            connections.get(cmd.gameID).broadcast(authData.username(), notification);
        }
        catch (Exception ex) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }
    private void resign(Session session, String msg) throws IOException {
        try {
            leave cmd = new Gson().fromJson(msg, leave.class);
            AuthData authData = myauth.getusr(new AuthData(cmd.getAuthString(), ""));
            GameData gameData = getGame(cmd.gameID);

            if (!(authData.username().equals(gameData.blackUsername()) ||
                    authData.username().equals(gameData.whiteUsername()))) {
                ErrorMessage err = new ErrorMessage("You are not a player in this game");
                session.getRemote().sendString(new Gson().toJson(err));
                return;
            }

            if (gameData.game().getTeamTurn() == null) {
                ErrorMessage err = new ErrorMessage("This game is already over");
                session.getRemote().sendString(new Gson().toJson(err));
                return;
            }
            gameData.game().makeMove(new ChessMove(new ChessPosition(2, 2), new ChessPosition(4, 2), null));
            gameData.game().setTeamTurn(null);
            mygame.updateGame2(gameData.gameID(), null);
            NotificationMessage notification = new NotificationMessage(authData.username() + " just resigned.");
            session.getRemote().sendString(new Gson().toJson(notification));
            connections.get(cmd.gameID).broadcast(authData.username(), notification);
        } catch (Exception ex) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }
    private GameData getGame(int gameID) throws DataAccessException {
        return mygame.getGamefull(gameID);
    }
}
