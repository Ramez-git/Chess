package webSocketMessages.userCommands;

import chess.ChessMove;

public class move extends UserGameCommand {
    public int gameID;
    public ChessMove move;
    public String check;
    public move(String authToken, int gameID, ChessMove move, String check) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
        this.check=check;
    }
}
