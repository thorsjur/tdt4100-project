package chess;

import java.util.List;
import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(Colour colour, Board board) {
        super(colour, board);
        moveDirections = Direction.values();
    }

    public List<Move> getValidMoves() {
        List<Move> list = new ArrayList<Move>();
        for (Direction direction : moveDirections) {
            list.addAll(getUnidirectionalMoves(direction, getCoordinates(), true));
        }
        return list;
    }

    @Override
    public String toString() {
        return colour.toString() + "Queen";
    }
}
