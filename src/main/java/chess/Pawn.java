package chess;

import java.util.List;
import java.util.ArrayList;

public class Pawn extends Piece{

    public Pawn(Colour colour) {
        super(colour);
    }
    
    public List<Move> getValidMoves(Board board) {
        List<Move> list = new ArrayList<Move>();
        int[] fromCoordinates = getCoordinates(board);
        int[] toCoordinates = {fromCoordinates[0] - 1, fromCoordinates[1]};
        int[] toCoordinates2 = {fromCoordinates[0] - 2, fromCoordinates[1]};
        list.add(new Move(fromCoordinates, toCoordinates, Move.MoveType.MOVE));
        list.add(new Move(fromCoordinates, toCoordinates2, Move.MoveType.TAKE));
        for (Move move : list) {
            System.out.println(move);
        }
        return list;
    }

    @Override
    public String toString() {
        return colour.toString() + "Pawn";
    }
}
