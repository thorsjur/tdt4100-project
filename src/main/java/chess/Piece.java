package chess;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Piece {

    enum Direction {
        UP(new int[] { -1, 0 }),
        DOWN(new int[] { 1, 0 }),
        RIGHT(new int[] { 0, 1 }),
        LEFT(new int[] { 0, -1 }),
        UP_RIGHT(new int[] { -1, 1 }),
        UP_LEFT(new int[] { -1, -1 }),
        DOWN_RIGHT(new int[] { 1, 1 }),
        DOWN_LEFT(new int[] { 1, -1 });

        private final int[] directionVector;
        private final int[] inverseDirectionVector;

        private Direction(int[] directionVector) {
            this.directionVector = directionVector;
            this.inverseDirectionVector = new int[]{-directionVector[0], -directionVector[1]};
        }

        public int[] getDirectionVector(Board board) {
            boolean boardRotationEnabled = board.isBoardRotationEnabled();
            Colour turn = board.getTurn();
            return ! boardRotationEnabled && turn == Colour.BLACK ? inverseDirectionVector : directionVector;
        }

        public int[] getInvertedDirectionVector() {
            return new int[]{-directionVector[0], -directionVector[1]};
        }
    }

    protected Colour colour;
    protected boolean hasMoved = false;
    protected Board board;
    protected Direction[] moveDirections;

    public Piece(Colour colour, Board board) {
        this.colour = colour;
        this.board = board;
    }

    public abstract List<Move> getValidMoves();

    public List<Move> getValidMoves(Move.MoveType type) {
        List<Move> moveList = getValidMoves();
        List<Move> moveListSpecificType = new ArrayList<>();
        for (Move move : moveList) {
            if (move.getType() == type) moveListSpecificType.add(move);
        }
        return moveListSpecificType;
    }

    public int[] getCoordinates() {
        return board.getCoordinatesOfPiece(this);
    }

    public Board getBoard() {
        return board;
    }

    public void highlightValidMoves() {
        List<Move> moveList = getValidMoves();
        for (Move move : moveList) {
            if (! move.leadsToCheck(board)) move.highlightMove(board);
        }
    }

    public void registerMove() {
        hasMoved = true;
    }

    public Piece getPieceRelativeToPosition(int[] coordinateDifference) {
        int[] fromCoordinates = getCoordinates();
        int[] toCoordinates = { fromCoordinates[0] + coordinateDifference[0], fromCoordinates[1] + coordinateDifference[1] };
        return board.getPiece(toCoordinates);
    }

    public List<Move> getUnidirectionalMoves(Direction direction, int[] currentCoordinates, boolean take) {
        int[] directionVector = direction.getDirectionVector(board);
        int[] toCoordinates = { currentCoordinates[0] + directionVector[0],
                currentCoordinates[1] + directionVector[1] };

        if (toCoordinates[0] > 7 || toCoordinates[0] < 0 || toCoordinates[1] > 7 || toCoordinates[1] < 0) {
            return new ArrayList<Move>();
        }
        Piece atPiece = board.getPiece(toCoordinates);
        if (atPiece != null && (!take || atPiece.getColour() == colour)) {
            return new ArrayList<Move>();
        } else if (take && atPiece != null && atPiece.getColour() != colour) {
            return new ArrayList<Move>(Arrays.asList(new Move(getCoordinates(), toCoordinates, Move.MoveType.TAKE)));
        }
        List<Move> moveList = new ArrayList<>(
                Arrays.asList(new Move(getCoordinates(), toCoordinates, Move.MoveType.MOVE)));
        moveList.addAll(getUnidirectionalMoves(direction, toCoordinates, take));
        return moveList;
    }

    public Colour getColour() {
        return colour;

    }
}
