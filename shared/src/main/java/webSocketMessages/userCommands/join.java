package webSocketMessages.userCommands;

import chess.ChessGame;

public class join extends UserGameCommand {
    public int gameID;
    public ChessGame.TeamColor playerColor;
    public join(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }
}
