package chess;

import java.util.List;
import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(Colour colour, Board board) {
        super(colour, board);
        moveDirections = new Direction[]{Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_RIGHT, Direction.DOWN_LEFT};
    }

    public List<Move> getValidMoves() {
        List<Move> moveList = new ArrayList<Move>();
        int[] coordinates = getCoordinates();
        for (Direction direction : moveDirections) {
            moveList.addAll(getUnidirectionalMoves(direction, coordinates, true));
        }
        return moveList;
    }

    @Override
    public String toString() {
        return colour.toString() + "Bishop";
    }
}
