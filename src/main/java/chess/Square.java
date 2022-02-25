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

    public void setBoard(Board board) {
        this.board = board;
    }
    
    public void setBackgroundColor(String hexColour) {
        setStyle("-fx-background-color: " + hexColour);
    }

    public void selectSquare() {
        board.selectSquare(this);
        setHighlight();
    }

    public void selectSquare(Colour turn) {
        if (piece != null && piece.getColour() == turn) {
            selectSquare();
        } else {
            deselectSquare();
        }
    }

    public void deselectSquare() {
        Square selectedSquare = board.getSelectedSquare();
        if (selectedSquare != null) {
            selectedSquare.removeHighlight();
        }
        Square nullSquare = null;
        board.selectSquare(nullSquare);
    }

    public void setHighlight(String hexColour) {
        setStyle("-fx-background-color: " + hexColour);
    }

    public void removeHighlight() {
        String squareId = getId();
        String hexColour = Board.getColourOfSquare(Board.getCoordinatesOfSquare(squareId)).getHexColour();
        setStyle("-fx-background-color: " + hexColour);
    }

    private void setHighlight() {
        setStyle("-fx-background-color: " + "#429d42");
    }


    public int[] getCoordinates() {
        String squareId = getId();
        int[] coordinates = {squareId.charAt(squareId.length() - 2) - '0', squareId.charAt(squareId.length() - 1) - '0'};
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


}
