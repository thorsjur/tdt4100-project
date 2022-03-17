package chess.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chess.model.Board.Coordinate;

public class Pawn extends Piece {

    public Pawn(Colour colour, Board board) {
        super(colour, board);
    }

    @Override
    public List<Move> getValidMoves() {
        List<Move> moveList = new ArrayList<Move>();

        moveList.addAll(getPawnMoves());
        moveList.addAll(getPawnTakes());

        return moveList;
    }

    public boolean canPromote() {
        Coordinate coordinates = getCoordinates();
        return coordinates.row() == 0 || coordinates.row() == 7;
    }

    @Override
    public String toString() {
        return colour.toString() + "Pawn";
    }

    private List<Move> getPawnMoves() {
        List<Move> moveList = getUnidirectionalMoves(Direction.UP, getCoordinates(), false);
        if (hasMoved == false && moveList.size() >= 2) {
            return moveList.subList(0, 2);
        } else if (moveList.size() >= 1) {
            if (!hasMoved) {
                return moveList.subList(0, 1);
            } else {
                return new ArrayList<Move>(Arrays.asList(moveList.get(0)));
            }
        }
        return new ArrayList<Move>();
    }

    private List<Move> getPawnTakes() {
        List<Move> takeList = new ArrayList<Move>();

        for (Direction direction : new Direction[] { Direction.UP_LEFT, Direction.UP_RIGHT }) {
            List<Move> tempTakeList = getUnidirectionalMoves(direction, getCoordinates(), true);
            if (tempTakeList.size() > 0 && tempTakeList.get(0).getType() == Move.MoveType.TAKE) {
                takeList.add(tempTakeList.get(0));
            }
        }
        return takeList;
    }
}