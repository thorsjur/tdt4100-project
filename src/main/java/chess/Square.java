package chess;

import javafx.scene.layout.Pane;

public class Square extends Pane {

    private Piece piece;
    private Board board;

    public Piece getPiece() {
        return piece;
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

    public void setBackgroundColor(String hexColour) {
        setStyle("-fx-background-color: " + hexColour);
    }

    public void selectSquare() {
        Piece piece = getPiece();

        if (piece != null && piece.getColour() == board.getTurn()) {
            if (board.getSelectedSquare() != this) {
                board.removeAllHighlights();
                board.selectSquare(this);
            }
            board.selectSquare(this);
            setHighlight();
            piece.highlightValidMoves();
        } else {
            deselectSquare();
            board.removeAllHighlights();
        } 
        
    }

    public void deselectSquare() {
        Square selectedSquare = board.getSelectedSquare();
        if (selectedSquare != null) {
            selectedSquare.removeHighlight();
        }
        Square nullSquare = null;
        board.removeAllHighlights();
        board.selectSquare(nullSquare);
    }

    public void setHighlight(String hexColour) {
        setStyle("-fx-background-color: " + hexColour);
    }

    public void removeHighlight() {
        String hexColour = getColourOfSquare().getHexColour();
        setStyle("-fx-background-color: " + hexColour);
    }

    private void setHighlight() {
        setStyle("-fx-background-color: " + "#429d42");
    }

    public int[] getCoordinates() {
        String squareId = getId();
        int[] coordinates = { squareId.charAt(squareId.length() - 2) - '0',
                squareId.charAt(squareId.length() - 1) - '0' };
        return coordinates;
    }

    @Override
    public String toString() {
        int[] coordinates = getCoordinates();
        int row = coordinates[0];
        int col = coordinates[1];
        if (piece != null) {
            return piece.toString() + " at row: " + row + " | col:" + col;
        }
        return "No piece, " + " at row: " + row + " | col:" + col;
    }

    private Colour getColourOfSquare() {
        int[] coordinates = getCoordinates();
        int row = coordinates[0];
        int col = coordinates[1];
        return (row + col) % 2 == 0 ? Colour.WHITE : Colour.BLACK;
    }

}
