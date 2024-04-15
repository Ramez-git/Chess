package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import websocket.WebSocketFacade;

import java.util.Objects;

public class ingame {
    public GameData game;
    public replier repl;
    private ChessGame.TeamColor color;
    private WebSocketFacade web;
    private String authToken;

    public ingame(GameData gameData, ChessGame.TeamColor color, user genClient) {
        this.color = color;
        this.game = gameData;
        this.repl = genClient.repl;
        this.web = new WebSocketFacade(genClient.url, this);
        this.authToken = genClient.authToken;

        if (color != null) {
            this.web.joinPlayer(this.authToken, this.game.gameID(), this.color);
        } else {
            this.web.joinObserver(this.authToken, this.game.gameID());
        }
    }

    public state eval(String input) {
        switch (input) {
            case "help" -> help();
            case "draw" -> draw();
            case "move" -> move();
            case "highlight" -> highlight();
            case "resign" -> resign();
            case "leave" -> leave();
            default -> repl.printErr("invalid instruction");
        }
        if (input.equals("leave")) {
            return state.LOGGEDIN;
        } else {
            return state.GAMEPLAY;
        }
    }

    private void help() {
        if (color != null) {
            repl.printMsg("Choose from one of the following options:\n" +
                    "\tHelp: see this message again\n" +
                    "\tDraw: Re-draw the chessboard\n" +
                    "\tMove: Make a move\n" +
                    "\tHighlight: Highlight all legal moves for a specified piece\n" +
                    "\tResign: Forfeit the match and end the game\n" +
                    "\tLeave: Leave the game");
        } else {
            repl.printMsg("Choose from one of the following options:\n" +
                    "\tHelp: see this message again\n" +
                    "\tDraw: Re-draw the chessboard\n" +
                    "\tHighlight: Highlight all legal moves for a specified piece\n" +
                    "\tLeave: Leave the game");
        }
    }

    public void draw() {
        repl.printMsg(game.game().getBoard().toString1(color,null));
    }

    private void move() {
        if (this.color == null) {
            repl.printErr("invalid instruction");
            return;
        }
        repl.printMsg("Enter the position of the piece you would like to move (i.e. d4).");
        ChessPosition startPosition = repl.scanPosition();
        repl.printMsg("Enter the position you would like to move this piece to (i.e. d4).");
        ChessPosition endPosition = repl.scanPosition();
        repl.printMsg("If this move results in a promotion, enter the promotion piece. Otherwise, press enter");
        String pieceStr = repl.scanWord();
        ChessPiece.PieceType promo = strToPiece(pieceStr);
        ChessMove move = new ChessMove(startPosition, endPosition, promo);

        if (!game.game().getBoard().getPiece(startPosition).pieceMoves(game.game().getBoard(), startPosition).contains(move)) {
            repl.printErr("Invalid move. Please enter a gameplay command to continue.");
        }
        String c ="";

        try {
            this.web.makeMove(authToken, game.gameID(), move,c);
        } catch (Exception e) {
            repl.printMsg("err");
        }
    }

    private void highlight() {
        repl.printMsg("Enter the position of the piece you would like to see move moves for.");
        ChessPosition position = repl.scanPosition();
        ChessGame.TeamColor color = this.color == null ? ChessGame.TeamColor.WHITE : this.color;
        repl.printMsg("");
    }

    private void resign() {
        if (this.color == null) {
            repl.printErr("invalid instruction");
            return;
        }
        try {
            web.resign(authToken, game.gameID());
        } catch (Exception e) {
            repl.printMsg("err");
        }
        repl.printMsg("success, game is over");
        if(Objects.equals(repl.scanWord(), "Join")){
            repl.scanWord();
            System.out.println("Game is over");
    }}

    private void leave() {
        try {
            web.leave(authToken, game.gameID());
        } catch (Exception e) {
            repl.printMsg("err");
        }
    }

    private ChessPiece.PieceType strToPiece(String pieceStr) {
        return switch (pieceStr) {
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            default -> null;
        };
    }
}
