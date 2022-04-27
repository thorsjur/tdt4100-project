package chess.model;

import java.util.List;
import java.util.stream.Collectors;

import chess.model.Board.Coordinate;

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
            this.inverseDirectionVector = new int[] { -directionVector[0], -directionVector[1] };
        }

        public int[] getDirectionVector(Board board) {
            boolean boardRotationEnabled = board.isBoardRotationEnabled();
            Colour turn = board.getTurn();
            return ((!boardRotationEnabled && turn == Colour.BLACK) ? inverseDirectionVector : directionVector);
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
        return getValidMoves().stream()
                    .filter(move -> move.getType() == type)
                    .collect(Collectors.toList());
    }

    public Coordinate getCoordinates() {
        return board.getPieceCoordinate(this);
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Colour getColour() {
        return colour;
    }

    public void highlightValidMoves() {
        getValidMoves().stream()
                .filter(move -> !move.leadsToCheck(board))
                .forEach(move -> move.highlightMove(board));
    }

    public void registerMove() {
        this.hasMoved = true;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    protected Coordinate getRelativeCoordinates(int[] vector) throws IndexOutOfBoundsException {
        return getCoordinates().addVector(vector);
    }

    protected Piece getPieceRelativeToPosition(int[] vector) {
        try {
            Coordinate relativeCoordinates = getRelativeCoordinates(vector);
            return board.getPiece(relativeCoordinates);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    protected List<Move> getUnidirectionalMoves(Direction direction, Coordinate currentCoordinates, boolean canTake) {
        int[] directionVector = direction.getDirectionVector(board);
        Coordinate toCoordinates;
        try {
            toCoordinates = currentCoordinates.addVector(directionVector);
        } catch (IndexOutOfBoundsException e) {
            return new ArrayList<Move>();
        }

        Piece atPiece = board.getPiece(toCoordinates);
        if (atPiece != null && (!canTake || atPiece.getColour() == colour)) {
            return new ArrayList<Move>();
        } else if (canTake && atPiece != null && atPiece.getColour() != colour) {
            return new ArrayList<Move>(Arrays.asList(new Move(getCoordinates(), toCoordinates, Move.MoveType.TAKE)));
        }
        List<Move> moveList = new ArrayList<>(
                Arrays.asList(new Move(getCoordinates(), toCoordinates, Move.MoveType.MOVE)));
        moveList.addAll(getUnidirectionalMoves(direction, toCoordinates, canTake));
        return moveList;
    }

    protected boolean equalsColourOf(Piece piece) {
        return this.getColour() == piece.getColour();
    }
}
