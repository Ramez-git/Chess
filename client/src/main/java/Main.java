import chess.ChessGame;
import chess.ChessPiece;
import dataAccess.DataAccessException;
import exception.ResponseException;
import ui.UIclient;

public class Main {
    public static void main(String[] args) throws ResponseException, DataAccessException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var ui = new UIclient();
        ui.run();
    }
}