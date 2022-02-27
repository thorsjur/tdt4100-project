package chess;

import java.util.List;
import java.util.ArrayList;

public class Pawn extends Piece {

    public Pawn(Colour colour, Board board) {
        super(colour, board);
    }

    public List<Move> getValidMoves() {
        List<Move> moveList = new ArrayList<Move>();
        moveList.addAll(getPawnMoves());
        moveList.addAll(getPawnTakes());
        return moveList;
    } 
 
    @Override
    public String toString() {
        return colour.toString() + "Pawn";
    }

    private List<Move> getPawnMoves() {
        List<Move> moveList = getUnidirectionalMoves(Direction.UP, getCoordinates(), false);
        if (hasMoved == false && moveList.size() >= 2) {
            return moveList.subList(0, 2);
        } else if (hasMoved == true && moveList.size() >= 1) {
            return moveList.subList(0, 1);
        }
        return new ArrayList<Move>();
    }

    private List<Move> getPawnTakes() {
        List<Move> takeList = new ArrayList<Move>();
        for (Direction direction : new Direction[]{Direction.UP_LEFT, Direction.UP_RIGHT}) {
            List<Move> tempTakeList = getUnidirectionalMoves(direction, getCoordinates(), true);
            if (tempTakeList.size() > 0 && tempTakeList.get(0).getType() == Move.MoveType.TAKE) {
                takeList.add(tempTakeList.get(0));
            }
        }
        return takeList;
    }
}