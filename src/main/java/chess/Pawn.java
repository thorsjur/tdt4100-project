package chess;

import java.util.List;
import java.util.ArrayList;

public class Pawn extends Piece{

    public Pawn(Colour colour) {
        super(colour);
    }
    
    public List<Move> getValidMoves(Board board) {
        List<Move> list = new ArrayList<Move>();
        int[] fromCoordinates = {6, 5};
        int[] toCoordinates = {5, 5};
        list.add(new Move(fromCoordinates, toCoordinates));
        return list;
    }

    @Override
    public String toString() {
        return colour.toString() + "Pawn";
    }
}
