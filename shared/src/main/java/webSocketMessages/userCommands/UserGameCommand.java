package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public UserGameCommand(String authToken) {
        this.authToken = authToken;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;

    private final String authToken;

    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }
    public static class MakeMoveCommand extends UserGameCommand {
        public int gameID;
        public ChessMove move;
        public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
            super(authToken);
            this.commandType = CommandType.MAKE_MOVE;
            this.gameID = gameID;
            this.move = move;
        }
}
    public static class JoinPlayerCommand extends UserGameCommand {
        public int gameID;
        public ChessGame.TeamColor playerColor;
        public JoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor playerColor) {
            super(authToken);
            this.commandType = CommandType.JOIN_PLAYER;
            this.gameID = gameID;
            this.playerColor = playerColor;
        }
    }
    public static class JoinObserverCommand extends UserGameCommand {
        public int gameID;
        public JoinObserverCommand(String authToken, int gameID) {
            super(authToken);
            this.commandType = CommandType.JOIN_OBSERVER;
            this.gameID = gameID;
        }
    }
    public static class LeaveCommand extends UserGameCommand {
        public int gameID;
        public LeaveCommand(String authToken, int gameID) {
            super(authToken);
            this.commandType = CommandType.LEAVE;
            this.gameID = gameID;
        }
    }
    public static class ResignCommand extends UserGameCommand {
        public int gameID;
        public ResignCommand(String authToken, int gameID) {
            super(authToken);
            this.commandType = CommandType.RESIGN;
            this.gameID = gameID;
        }
    }
}
