package chess;

import java.util.List;
import java.util.ArrayList;

public class Rook extends Piece{

    public Rook(Colour colour) {
        super(colour);
    }
    
    public List<Move> getValidMoves(Board board) {
        List<Move> list = new ArrayList<Move>();
        return list;
    }

    @Override
    public String toString() {
        return colour.toString() + "Rook";
    }
}
