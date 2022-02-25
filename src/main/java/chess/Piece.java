package chess;

import java.util.List;

public abstract class Piece {

    Colour colour;
    boolean hasMoved;
    
    public Piece(Colour colour) {
        this.colour = colour;
    }

    public abstract List<Move> getValidMoves(Board board);

    public int[] getCoordinates(Board board) {
        for (Square[] row : board.getChessBoard()) {
            for (Square square : row) {
                if (square.getPiece() == this) {
                    return square.getCoordinates();
                }
            }
        }
        return null; 
    }

    public void highlightValidMoves(Board board) {
        List<Move> moveList = getValidMoves(board);
        for (Move move : moveList) {
            move.highlightMove(board);
        }
    }

    public Colour getColour() {
        return colour;
    }
}
