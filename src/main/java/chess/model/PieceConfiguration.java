package chess.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PieceConfiguration implements Iterable<Piece> {

    private Piece[][] pieceGrid;
    private PieceConfiguration nextGame;
    private PieceConfiguration previousGame;
    private boolean isBoardRotated;
    private Colour turn = Colour.WHITE;

    public PieceConfiguration(Piece[][] pieceGrid) {
        this.pieceGrid = pieceGrid;
    }

    public PieceConfiguration(PieceConfiguration previousGame, Piece[][] pieceGrid, Colour turn,
            boolean isBoardRotated) {
        this.pieceGrid = pieceGrid;
        this.previousGame = previousGame;
        this.isBoardRotated = isBoardRotated;
        this.turn = turn;
        if (previousGame != null) {
            previousGame.setNextGame(this);
        }
    }

    public Piece[][] getPieceGrid() {
        return pieceGrid;
    }

    public Colour getTurn() {
        return turn;
    }

    public void setNextGame(PieceConfiguration nextGame) {
        this.nextGame = nextGame;

        if (nextGame != null && nextGame.getPreviousGame() != this && nextGame != this) {
            nextGame.setPreviousGame(this);
        }
    }

    public void setPreviousGame(PieceConfiguration previousGame) {
        this.previousGame = previousGame;

        if (previousGame != null && previousGame.getNextGame() != this && previousGame != this) {
            previousGame.setNextGame(this);
        }
    }

    public void setPieceGrid(Piece[][] pieceGrid) {
        this.pieceGrid = pieceGrid;
    }

    public boolean hasNextGame() {
        return nextGame != null;
    }

    public boolean hasPreviousGame() {
        return previousGame != null;
    }

    public PieceConfiguration getNextGame() {
        return nextGame;
    }

    public PieceConfiguration getPreviousGame() {
        return previousGame;
    }

    public PieceConfiguration getCurrentPieceConfiguration() {
        PieceConfiguration currentConfiguration = this;
        while (currentConfiguration.hasNextGame()) currentConfiguration = currentConfiguration.getNextGame();

        return currentConfiguration;
    }

    public PieceConfiguration getRoot() {
        PieceConfiguration root = this;
        while (root.hasPreviousGame()) root = root.getPreviousGame();
        return root;
    }

    public boolean isBoardRotated() {
        return isBoardRotated;
    }

    public boolean isAtCurrentBoard() {
        return nextGame == null;
    }

    @Override
    public String toString() {
        return turn.toString() + " " + String.valueOf(hasPreviousGame()) + " " + String.valueOf(hasNextGame());
    }

    public int countPieceConfigurations() {
        PieceConfiguration root = this;
        while (root.hasPreviousGame()) {
            root = root.getPreviousGame();
        }

        int count = 0;
        while (root.hasNextGame()) {
            count++;
            root = root.getNextGame();
        }

        return count;
    }

    @Override
    public Iterator<Piece> iterator() {
        return new Iterator<Piece>() {

            private int row = 0;
            private int col = 0;
            private final Piece[][] board = pieceGrid;

            @Override
            public boolean hasNext() {
                return row < 8 && col < 8;
            }

            @Override
            public Piece next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Iterator exhausted");
                }
                Piece piece = board[row][col];
                
                if (col < 7) {
                    col++;
                } else if (col == 7 && row < 8) {
                    col = 0;
                    row++;
                } else {
                    row++;
                }

                return piece;
            }

        };
    }
}
