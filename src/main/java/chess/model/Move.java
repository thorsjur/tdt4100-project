package chess.model;

import chess.model.Board.Coordinate;

public class Move {

    enum MoveType {
        TAKE,
        MOVE,
        EN_PASSANT,
        CASTLE;
    }

    private MoveType type;
    private Coordinate fromCoordinates;
    private Coordinate toCoordinates;

    public Move(Coordinate fromCoordinates, Coordinate toCoordinates) {
        this.fromCoordinates = fromCoordinates;
        this.toCoordinates = toCoordinates;
    }

    public Move(Coordinate fromCoordinates, Coordinate toCoordinates, MoveType type) {
        this(fromCoordinates, toCoordinates);
        this.type = type;
    }

    public Coordinate getFromCoordinates() {
        return fromCoordinates;
    }

    public Coordinate getToCoordinates() {
        return toCoordinates;
    }

    public MoveType getType() {
        return type;
    }

    public void highlightMove(Board board) {
        Square square = board.getSquare(toCoordinates);
        Square.State state;

        switch (getType()) {
            case MOVE:
                state = Square.State.VIABLE_MOVE;
                break;
            case TAKE:
                state = Square.State.VIABLE_TAKE;
                break;
            case CASTLE:
                state = Square.State.VIABLE_CASTLE_DESTINATION;
                break;
            case EN_PASSANT:
                state = Square.State.VIABLE_EN_PASSANT;
                break;
            default:
                state = Square.State.DEFAULT;
                break;
        }

        square.setState(state);
    }

    public boolean leadsToCheck(Board board) {
        Piece piece = board.getPiece(fromCoordinates);
        return board.checkNextBoardForCheck(piece, toCoordinates);
    }

    public static Move getValidMove(Piece piece, Coordinate toCoordinates) {
        return piece.getValidMoves().stream()
            .filter(move -> move.getToCoordinates().equals(toCoordinates))
            .findFirst()
            .orElse(null);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move)) {
            return false;
        }

        Move move = (Move) obj;
        return move.getToCoordinates().equals(getToCoordinates())
                && move.getFromCoordinates().equals(getFromCoordinates());
    }

    @Override
    public String toString() {
        return "from row: " + fromCoordinates.row() 
                + " column: " + fromCoordinates.column() 
                + " | to row: " + toCoordinates.row()
                + " column: " + toCoordinates.column();
    }
}
