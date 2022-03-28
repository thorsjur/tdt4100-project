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

    public Coordinate getRelativeCoordinates(Coordinate coordinateDifference) {
        return getCoordinates().add(coordinateDifference);
    }

    public Piece getPieceRelativeToPosition(Coordinate coordinateDifference) {
        Coordinate relativeCoordinates = getRelativeCoordinates(coordinateDifference);
        return board.getPiece(relativeCoordinates);
    }

    public List<Move> getUnidirectionalMoves(Direction direction, Coordinate currentCoordinates, boolean take) {
        int[] directionVector = direction.getDirectionVector(board);
        Coordinate toCoordinates = currentCoordinates.addVector(directionVector);

        if (toCoordinates.row() > 7 || toCoordinates.row() < 0 || toCoordinates.column() > 7
                || toCoordinates.column() < 0) {
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

    public boolean equalsColourOf(Piece piece) {
        return this.getColour() == piece.getColour();
    }
}
