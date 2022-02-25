package chess;

import java.util.List;

public abstract class Piece {

    Colour colour;
    boolean hasMoved;
    
    public Piece(Colour colour) {
        this.colour = colour;
    }

    public abstract List<Move> getValidMoves(Board board);

    public Colour getColour() {
        return colour;
    }
}
