import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;
import ui.UIclient;

public class Main {
    public static void main(String[] args) throws ResponseException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var ui = new UIclient();
        ui.run();
    }
}