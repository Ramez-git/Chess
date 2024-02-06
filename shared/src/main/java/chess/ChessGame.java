package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static java.awt.geom.Path2D.contains;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }

    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
    public void switch_turn(){
        if(turn == TeamColor.WHITE){
            turn = TeamColor.BLACK;
        }
        else{
            turn = TeamColor.WHITE;

        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> result = new HashSet<>();
        if (board.getPiece(startPosition) == null) {
            return null;
        } else {
            ChessPiece Mypiece = board.getPiece(startPosition);
            TeamColor myColor = board.getPiece(startPosition).getTeamColor();
            for(var move: Mypiece.pieceMoves(board,startPosition)){
                ChessPosition mystart = move.getStartPosition();
                ChessPosition myend = move.getEndPosition();
                board.removePiece(mystart);
                var possibleotherpiece = board.getPiece(myend);
                board.removePiece(myend);
                board.addPiece(myend,Mypiece);
                if(!isInCheck(myColor)){
                    result.add(move);
                }
                board.removePiece(myend);
                board.addPiece(mystart,Mypiece);
                if(possibleotherpiece !=null){
                    board.addPiece(myend,possibleotherpiece);
                }
            }
        }
        return result;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(board.getPiece(move.getStartPosition()).getTeamColor()!=turn){
            throw new InvalidMoveException("not your turn");
        }
        Collection<ChessMove> myvalidmoves=validMoves(move.getStartPosition());
        if(myvalidmoves.contains(move)){
            ChessPiece mypiece = board.getPiece(move.getStartPosition());
            board.removePiece(move.getStartPosition());
            board.removePiece(move.getEndPosition());
            switch_turn();
            if(move.getPromotionPiece() == null){
            board.addPiece(move.getEndPosition(),mypiece);}
            else{
                board.addPiece(move.getEndPosition(),new ChessPiece(mypiece.getTeamColor(),move.getPromotionPiece()));

            }
        }
        else{
            throw new InvalidMoveException("not a valid move");
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition myKing = null;
        for (int row = 1; row <= 8; row++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition mypos=new ChessPosition(row,column);
                ChessPiece Pieceinquestion = board.getPiece(mypos);
                if (Pieceinquestion!= null && Pieceinquestion.getPieceType() == ChessPiece.PieceType.KING && Pieceinquestion.getTeamColor() == teamColor) {
                    myKing = mypos;
                }
            }
        }
        for (int row = 1; row <=8; row++) {
            for (int column = 1; column <=8; column++) {
                ChessPosition mypos=new ChessPosition(row,column);
                ChessPiece Pieceinquetion = board.getPiece(mypos);
                if (Pieceinquetion!=null && Pieceinquetion.getTeamColor() != teamColor) {
                    for(var moves:Pieceinquetion.pieceMoves(board,new ChessPosition(row,column))){
                        if(moves.getEndPosition().equals(myKing)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        if(isInCheck(teamColor)){
            ChessPiece myKing = null;
            ChessPosition mypos1=null;
            for (int row = 1; row <= 8; row++) {
                for (int column = 1; column <= 8; column++) {
                    ChessPosition mypos=new ChessPosition(row,column);
                    ChessPiece Pieceinquestion = board.getPiece(mypos);
                    if (Pieceinquestion!= null && Pieceinquestion.getPieceType() == ChessPiece.PieceType.KING && Pieceinquestion.getTeamColor() == teamColor) {
                        myKing = board.getPiece(mypos);
                        mypos1=mypos;
                    }
                }
            }
            for(var move:myKing.pieceMoves(board,mypos1)) {
                ChessPosition mystart = move.getStartPosition();
                ChessPosition myend = move.getEndPosition();
                board.removePiece(mystart);
                var possibleotherpiece = board.getPiece(myend);
                board.removePiece(myend);
                board.addPiece(myend, myKing);
                if (!isInCheck(teamColor)) {
                    return false;
                }
                board.removePiece(myend);
                board.addPiece(mystart, myKing);
                if (possibleotherpiece != null) {
                    board.addPiece(myend, possibleotherpiece);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for(int row=1;row<=8; row++){
            for(int column =1;column<=8;column++){
                var piece_in_question = board.getPiece(new ChessPosition(row,column));
                if(piece_in_question !=null && piece_in_question.getTeamColor() == teamColor){
                    var my_valid_moves=validMoves(new ChessPosition(row,column));
                    if(my_valid_moves.isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;

    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
