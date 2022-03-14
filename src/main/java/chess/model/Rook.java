package chess.model;

import java.util.List;
import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(Colour colour, Board board) {
        super(colour, board);
        moveDirections = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
    }
    
    @Override
    public List<Move> getValidMoves() {
        List<Move> moveList = new ArrayList<Move>();
        for (Direction direction : moveDirections) {
            moveList.addAll(getUnidirectionalMoves(direction, getCoordinates(), true));
        }
        return moveList;
    }

    @Override
    public String toString() {
        return colour.toString() + "Rook";
    }
}
