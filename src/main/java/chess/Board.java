package chess;

import java.util.ArrayList;
import java.util.List;

public class Board {
    
    private Piece[][] grid = new Piece[8][8];
    private Board previousBoard;
    private Board nextBoard;

    public Board(Piece[][] grid) {
        this.grid = grid;
    }

    public Board() {
        this.initializeBoard();
    }

    public Piece[][] getGrid() {
        return grid;
    }

    public Piece getPiece(int[] coordinates) {
        return grid[coordinates[0]][coordinates[1]];
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (grid[i][j] == piece) {
                    int[] coordinates = {i, j};
                    return coordinates;
                }
            }
        }
        return null;
    }

    public boolean isValidMove(Piece piece, int[] toCoordinates) {
        List<Move> moves = piece.getValidMoves(this);
        for (Move move : moves) {
            if (move.equals(new Move(getCoordinatesOfPiece(piece), toCoordinates))) {
                return true;
            }
        }
        return false;
    }

    private void setPiece(Piece piece, int[] toCoordinates) {
        int toRow = toCoordinates[0];
        int toCol = toCoordinates[1];
        grid[toRow][toCol] = piece;
    }

    private void removePiece(int[] coordinates) {
        grid[coordinates[0]][coordinates[1]] = null;
    }

    private void initializeBoard() {
        // Setter ut bønder
        for (int i = 0; i < grid.length; i++) {
            grid[1][i] = new Pawn(Colour.BLACK);
            grid[6][i] = new Pawn(Colour.WHITE);
        }

        // Setter tårn
        grid[0][0] = new Rook(Colour.BLACK);
        grid[0][7] = new Rook(Colour.BLACK);
        grid[7][0] = new Rook(Colour.WHITE);
        grid[7][7] = new Rook(Colour.WHITE);

        // setter springere
        grid[0][1] = new Knight(Colour.BLACK);
        grid[0][6] = new Knight(Colour.BLACK);
        grid[7][1] = new Knight(Colour.WHITE);
        grid[7][6] = new Knight(Colour.WHITE);

        // setter løpere
        grid[0][2] = new Bishop(Colour.BLACK);
        grid[0][5] = new Bishop(Colour.BLACK);
        grid[7][2] = new Bishop(Colour.WHITE);
        grid[7][5] = new Bishop(Colour.WHITE);

        // setter konger
        grid[0][4] = new King(Colour.BLACK);
        grid[7][4] = new King(Colour.WHITE);

        // setter dronninger
        grid[0][3] = new Queen(Colour.BLACK);
        grid[7][3] = new Queen(Colour.WHITE);

    }


    @Override
    public String toString() {
        String newString = "";
        for (Piece[] row : grid) {
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
