package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> result = new HashSet<>();
        ChessPiece piece_in_question = board.chessboard[myPosition.getRow() - 1][myPosition.getColumn() - 1];
        int row = myPosition.getRow() - 1;
        int column = myPosition.getColumn() - 1;
        switch (piece_in_question.getPieceType()) {
            case KING -> {
                for (int row1 = row - 1; row1 <= row + 1; row1++) {
                    if (row1 < 0 || row1 >= 8) {
                        continue;
                    } else {
                        for (int column1 = column - 1; column1 <= column + 1; column1++) {
                            if (column1 < 0 || column1 >= 8 || (row1 == row && column1 == column)) {
                                continue;
                            } else {
                                if (board.chessboard[row1][column1] == null || !board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                                    result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                                }
                            }
                        }
                    }
                }
            }
            case QUEEN -> {
//using the bishop first:
                for (int row1 = row + 1, column1 = column + 1; row1 < 8 && column1 < 8; row1++, column1++) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (int row1 = row - 1, column1 = column - 1; row1 >= 0 && column1 >= 0; row1--, column1--) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (int row1 = row - 1, column1 = column + 1; row1 >= 0 && column1 < 8; row1--, column1++) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (int row1 = row + 1, column1 = column - 1; row1 < 8 && column1 >= 0; row1++, column1--) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }

                // now using the rook
                int column1 = column;
                for (int row1 = row + 1; row1 < 8; row1++) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (int row1 = row - 1; row1 >= 0; row1--) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                int row1 = row;
                for (column1 = column - 1; column1 >= 0; column1--) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (column1 = column + 1; column1 < 8; column1++) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
            }

            case BISHOP -> {
                for (int row1 = row + 1, column1 = column + 1; row1 < 8 && column1 < 8; row1++, column1++) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (int row1 = row - 1, column1 = column - 1; row1 >= 0 && column1 >= 0; row1--, column1--) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (int row1 = row - 1, column1 = column + 1; row1 >= 0 && column1 < 8; row1--, column1++) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (int row1 = row + 1, column1 = column - 1; row1 < 8 && column1 >= 0; row1++, column1--) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
            }
            case KNIGHT -> {
                int row1 = row;
                int column1 = column;
                if (row1 + 1 < 8 && column1 + 2 < 8) {
                    row1++;
                    column1 += 2;
                    if (board.chessboard[row1][column1] == null || !board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    }
                }
                row1 = row;
                column1 = column;
                if (row1 + 2 < 8 && column1 + 1 < 8) {
                    row1 += 2;
                    column1++;
                    if (board.chessboard[row1][column1] == null || !board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    }
                }
                row1 = row;
                column1 = column;
                if (row1 + 2 < 8 && column1 - 1 >= 0) {
                    row1 += 2;
                    column1--;
                    if (board.chessboard[row1][column1] == null || !board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    }
                }
                row1 = row;
                column1 = column;
                if (row1 + 1 < 8 && column1 - 2 >= 0) {
                    row1++;
                    column1 -= 2;
                    if (board.chessboard[row1][column1] == null || !board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    }
                }
                row1 = row;
                column1 = column;
                if (row1 - 1 >= 0 && column1 - 2 >= 0) {
                    row1--;
                    column1 -= 2;
                    if (board.chessboard[row1][column1] == null || !board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    }
                }
                row1 = row;
                column1 = column;
                if (row1 - 2 >= 0 && column1 - 1 >= 0) {
                    row1 -= 2;
                    column1--;
                    if (board.chessboard[row1][column1] == null || !board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    }
                }
                row1 = row;
                column1 = column;
                if (row1 - 2 >= 0 && column1 + 1 < 8) {
                    row1 -= 2;
                    column1++;
                    if (board.chessboard[row1][column1] == null || !board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    }
                }
                row1 = row;
                column1 = column;
                if (row1 - 1 >= 0 && column1 + 2 < 8) {
                    row1--;
                    column1 += 2;
                    if (board.chessboard[row1][column1] == null || !board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    }
                }

            }
            case ROOK -> {

                int column1 = column;
                for (int row1 = row + 1; row1 < 8; row1++) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (int row1 = row - 1; row1 >= 0; row1--) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                int row1 = row;
                for (column1 = column - 1; column1 >= 0; column1--) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
                for (column1 = column + 1; column1 < 8; column1++) {
                    if (board.chessboard[row1][column1] == null) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));

                    } else if (!board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 1, column1 + 1), null));
                        break;

                    } else if (board.chessboard[row1][column1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                        break;
                    }
                }
            }

            case PAWN -> {
                int row1 = row;
                int column1 = column;
                if (board.chessboard[row1+1][column1] == null) {
                    if (row1 + 1 == 7) {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 2, column1 + 1), PieceType.QUEEN));
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 2, column1 + 1), PieceType.ROOK));
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 2, column1 + 1), PieceType.KNIGHT));
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 2, column1 + 1), PieceType.BISHOP));
                    } else {
                        result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 2, column1 + 1), null));
                    }
                }
                if ((row1<7 && column1<7)&& !(board.chessboard[row1+1][column1+1] == null) && !board.chessboard[row1 + 1][column1 + 1].getTeamColor().equals(piece_in_question.getTeamColor())) {
                    result.add(new ChessMove(new ChessPosition(row + 1, column + 1), new ChessPosition(row1 + 2, column1 + 2), null));
                }
            }

        }
        return result;
    }
}
