package chess;

import java.util.List;
import java.util.ArrayList;

public class Pawn extends Piece{

    public Rook(Colour colour, Board board, int[] coordinates) {
        super(colour, board, coordinates);
    }
    
    public List<Move> getLegalMoves() {
        List<Move> list = new ArrayList<Move>();
        return list;
    }
}
