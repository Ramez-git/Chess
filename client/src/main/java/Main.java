import chess.ChessGame;
import chess.ChessPiece;
import dataAccess.DataAccessException;
import exception.ResponseException;
import ui.*;

public class Main {
    public static void main(String[] args) throws ResponseException, DataAccessException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String serverurl = "http://localhost:8080";
        var ui = new reply(serverurl);
        ui.run();
    }
}