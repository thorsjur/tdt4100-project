package chess;

import java.util.ArrayList;
import java.util.List;

public class Board {
    
    private Square[][] chessBoard = new Square[8][8];
    private Board previousBoard;
    private Board nextBoard;
    private Square selectedSquare;

    public Board(List<Square> squareList, Square[][] chessBoard) {
        this.chessBoard = chessBoard;
    }

    public Board(List<Square> squareList) {
        this.initializeBoard(squareList);
    }

    public Square[][] getChessBoard() {
        return chessBoard;
    }

    public Piece[][] getGrid() {
        Piece[][] grid = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = chessBoard[i][j].getPiece();
            }
        }
        return grid;
    }

    public Piece getPiece(int[] coordinates) {
        return getGrid()[coordinates[0]][coordinates[1]];
    }

    public Square getSquare(int[] coordinates) {
        return chessBoard[coordinates[0]][coordinates[1]];
    }

    public static Colour getColourOfSquare(int[] coordinates) {
        int row = coordinates[0];
        int col = coordinates[1];
        return (row + col) % 2 == 0 ? Colour.WHITE : Colour.BLACK;
    }

    public static int[] getCoordinatesOfSquare(String fxIdName) {
        int row = fxIdName.charAt(fxIdName.length() - 2) - '0';
        int col = fxIdName.charAt(fxIdName.length() - 1) - '0';
        int[] coordinates = {row, col};
        return coordinates;
    }

    public void movePiece(Piece piece, int[] toCoordinates) {
        int[] coordinatesOfPiece = getCoordinatesOfPiece(piece);
        setPiece(piece, toCoordinates);
        removePiece(coordinatesOfPiece);
    }

    public int[] getCoordinatesOfPiece(Piece piece) {
        if (piece == null) return null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (getGrid()[i][j] == piece) {
                    int[] coordinates = {i, j};
                    return coordinates;
                }
            }
        }
        return null;
    }

    public boolean isValidMove(Piece piece, int[] toCoordinates) {
        if (piece == null) {
            return false;
        }
        List<Move> moveList = piece.getValidMoves(this);
        for (Move move : moveList) {
            if (move.equals(new Move(piece.getCoordinates(this), toCoordinates))) {
                return true;
            }
        }
        return false;
    }

    private void setPiece(Piece piece, int[] toCoordinates) {
        int toRow = toCoordinates[0];
        int toCol = toCoordinates[1];
        getChessBoard()[toRow][toCol].setPiece(piece);
    }

    private void removePiece(int[] coordinates) {
        getChessBoard()[coordinates[0]][coordinates[1]].setPiece(null);
    }

    public Square getSelectedSquare() {
        return selectedSquare;
    }

    public void selectSquare(int[] coordinates) {
        selectedSquare = getSquare(coordinates);
    }

    public void selectSquare(Square square) {
        selectedSquare = square;
    }

    public void removeAllHighlights() {
        for (Square[] row : chessBoard) {
            for (Square square : row) {
                if (selectedSquare != null && selectedSquare == square) {
                    continue;
                }
                square.removeHighlight();
            }
        }
    }

    private void initializeBoard(List<Square> squareList) {
        for (Square square : squareList) {
            int[] coordinate = square.getCoordinates();
            chessBoard[coordinate[0]][coordinate[1]] = square;
            square.setBoard(this);
        }

        // Setter bønder
        for (int i = 0; i < chessBoard.length; i++) {
            chessBoard[1][i].setPiece(new Pawn(Colour.BLACK));
            chessBoard[6][i].setPiece(new Pawn(Colour.WHITE));
        }

        // Setter tårn
        chessBoard[0][0].setPiece(new Rook(Colour.BLACK));
        chessBoard[0][7].setPiece(new Rook(Colour.BLACK));
        chessBoard[7][0].setPiece(new Rook(Colour.WHITE));
        chessBoard[7][7].setPiece(new Rook(Colour.WHITE));

        // setter springere
        chessBoard[0][1].setPiece(new Knight(Colour.BLACK));
        chessBoard[0][6].setPiece(new Knight(Colour.BLACK));
        chessBoard[7][1].setPiece(new Knight(Colour.WHITE));
        chessBoard[7][6].setPiece(new Knight(Colour.WHITE));

        // setter løpere
        chessBoard[0][2].setPiece(new Bishop(Colour.BLACK));
        chessBoard[0][5].setPiece(new Bishop(Colour.BLACK));
        chessBoard[7][2].setPiece(new Bishop(Colour.WHITE));
        chessBoard[7][5].setPiece(new Bishop(Colour.WHITE));

        // setter konger
        chessBoard[0][4].setPiece(new King(Colour.BLACK));
        chessBoard[7][4].setPiece(new King(Colour.WHITE));

        // setter dronninger
        chessBoard[0][3].setPiece(new Queen(Colour.BLACK));
        chessBoard[7][3].setPiece(new Queen(Colour.WHITE));

    }


    @Override
    public String toString() {
        String newString = "";
        for (Piece[] row : getGrid()) {
            newString += "\n";
            for (Piece piece : row) {
                if (piece != null) {
                    newString +=" " + piece.toString() + " ";
                }
            }
        }

        return newString;
    }
}
