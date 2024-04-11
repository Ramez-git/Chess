package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public ChessPiece[][] chessboard =
            {{null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null}};

    public ChessBoard() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(chessboard, that.chessboard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessboard);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessboard[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessboard[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        chessboard[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        chessboard[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        chessboard[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        chessboard[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        chessboard[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        chessboard[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        chessboard[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        chessboard[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        chessboard[1][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        chessboard[1][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        chessboard[1][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        chessboard[1][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        chessboard[1][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        chessboard[1][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        chessboard[1][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        chessboard[1][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);


        chessboard[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        chessboard[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        chessboard[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        chessboard[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        chessboard[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        chessboard[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        chessboard[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        chessboard[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        chessboard[6][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        chessboard[6][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        chessboard[6][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        chessboard[6][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        chessboard[6][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        chessboard[6][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        chessboard[6][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        chessboard[6][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
    }


    public void removePiece(ChessPosition myPosition) {
        chessboard[myPosition.getRow()][myPosition.getColumn()] = null;
    }
    public String toString1(ChessGame.TeamColor Color, Collection<ChessPosition> highlighted) {
        var sb = new StringBuilder();
        String letters = "";
        int[] columns = new int[0];
        int[] rows = new int[0];
        String sq = "";


        try {
            if (Color == ChessGame.TeamColor.WHITE) {
            sq = "\u001b[30;47m";
            letters = "    a  b  c  d  e  f  g  h    ";
            columns = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
            rows = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
            }
            else if(Color == ChessGame.TeamColor.BLACK){
                letters = "    h  g  f  e  d  c  b  a    ";
                columns = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
                rows = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
            }
            sb.append(letters).append("\u001b[0m").append(" \n");
            for (int i : rows) {
                var row = " " + (i + 1) + " ";
                sb.append(row).append("\u001b[0m");
                for (var j : columns) {
                    var sqcolor = sq;
                    if (highlighted != null && highlighted.contains(new ChessPosition(i + 1, j + 1))) {
                        sqcolor = "\u001b[32;43m";
                    }
                    var piece = this.chessboard[i][j];
                    if (piece != null) {
                        var color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? "\u001b[1;34m" : "\u001b[1;31m";
                        var p = pieces.get(piece.getPieceType());
                        sb.append(sqcolor).append(color).append(" ").append(p).append(" ").append("\u001b[0m");
                    } else {
                        sb.append(sqcolor).append("   ").append("\u001b[0m");
                    }
                    sq = sq.equals("\u001b[37;40m") ? "\u001b[30;47m" : "\u001b[37;40m";
                }
                sb.append(row).append("\u001b[0m").append('\n');
                sq = sq.equals("\u001b[37;40m") ? "\u001b[30;47m" : "\u001b[37;40m";
            }
            sb.append(letters).append("\u001b[0m").append("\n");
        } catch (Exception e) {
            System.out.println("err");
        }
        return sb.toString();
    }
    private static final Map<ChessPiece.PieceType, String> pieces = Map.of(
            ChessPiece.PieceType.KING, "K",
            ChessPiece.PieceType.QUEEN, "Q",
            ChessPiece.PieceType.BISHOP, "B",
            ChessPiece.PieceType.KNIGHT, "N",
            ChessPiece.PieceType.ROOK, "R",
            ChessPiece.PieceType.PAWN, "P"
    );
}
