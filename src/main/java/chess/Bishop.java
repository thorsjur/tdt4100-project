package chess;

import java.util.List;
import java.util.ArrayList;

public class Bishop extends Piece{

    public Bishop(Colour colour) {
        super(colour);
    }
    
    public List<Move> getValidMoves(Board board) {
        List<Move> list = new ArrayList<Move>();
        return list;
    }

    @Override
    public String toString() {
        return colour.toString() + "Bishop";
    }
}
