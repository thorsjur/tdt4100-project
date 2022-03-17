package chess.model;

public class PieceConfiguration {

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
        previousGame.setNextGame(this);
    }

    public Piece[][] getPieceGrid() {
        return pieceGrid;
    }

    public Colour getTurn() {
        return turn;
    }

    public void setNextGame(PieceConfiguration nextGame) {
        this.nextGame = nextGame;
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
}
