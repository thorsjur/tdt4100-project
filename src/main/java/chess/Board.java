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

        //setter springere
        grid[0][1] = new Knight(Colour.BLACK);
        grid[0][6] = new Knight(Colour.BLACK);
        grid[7][1] = new Knight(Colour.WHITE);
        grid[7][6] = new Knight(Colour.WHITE);

        //setter løpere
        grid[0][2] = new Bishop(Colour.BLACK);
        grid[0][5] = new Bishop(Colour.BLACK);
        grid[7][2] = new Bishop(Colour.WHITE);
        grid[7][5] = new Bishop(Colour.WHITE);

        //setter konger
        grid[0][4] = new King(Colour.BLACK);
        grid[7][4] = new King(Colour.WHITE);



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
