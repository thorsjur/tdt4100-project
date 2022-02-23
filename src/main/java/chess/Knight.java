package chess;

import java.util.List;
import java.util.ArrayList;

public class Knight extends Piece{

    public Knight(Colour colour) {
        super(colour);
    }
    
    public List<Move> getValidMoves(Board board, int[] coordinates) {
        List<Move> list = new ArrayList<Move>();
        return list;
    }

    @Override
    public String toString() {
        return colour.toString() + "Knight";
    }
}