import chess.ChessGame;
import chess.ChessPiece;
import ui.*;

public class Main {
    public static void main(String[] args){
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String serverurl = "http://localhost:8080";
        var ui = new replier(serverurl);
        ui.run();
    }
}