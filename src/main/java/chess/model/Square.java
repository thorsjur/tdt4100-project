package chess.model;

import chess.model.Board.Coordinate;

public class Square {

    private Coordinate coordinate;
    private Piece piece;
    private Board board;
    private State state = State.DEFAULT;

    public enum State {
        DEFAULT, VIABLE_MOVE, VIABLE_TAKE, VIABLE_CASTLE_DESTINATION, SELECTED;
    }

    public Square(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Piece getPiece() {
        return piece;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public State getState() {
        return state;
    }

    public Colour getColour() {
        return ((coordinate.row() + coordinate.column()) % 2 == 0) ? Colour.WHITE : Colour.BLACK;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void removePiece() {
        piece = null;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void selectSquare() {
        Piece piece = getPiece();

        if (piece != null && piece.getColour() == board.getTurn()) {
            if (board.getSelectedSquare() != this) {
                board.removeAllHighlights();
                board.selectSquare(this);
            }
            board.selectSquare(this);
            setState(State.SELECTED);
            piece.highlightValidMoves();
        } else {
            deselectSelectedSquare(board);
            board.removeAllHighlights();
        }

    }

    public static void deselectSelectedSquare(Board board) {
        Square selectedSquare = board.getSelectedSquare();
        if (selectedSquare != null) {
            selectedSquare.setState(State.DEFAULT);
            ;
        }
        Square nullSquare = null;
        board.removeAllHighlights();
        board.selectSquare(nullSquare);
    }

    @Override
    public String toString() {
        Coordinate coordinate = getCoordinate();
        int row = coordinate.row();
        int col = coordinate.column();

        if (piece != null) {
            return piece.toString() + " at row: " + row + " | col:" + col;
        }
        return "No piece, " + " at row: " + row + " | col:" + col;
    }

}
