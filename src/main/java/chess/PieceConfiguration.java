package chess;

public class PieceConfiguration {

    private Piece[][] pieceGrid;
    private PieceConfiguration nextGame;
    private PieceConfiguration previousGame;
    private boolean isBoardRotated;
    private Colour turn = Colour.WHITE;
    public int boardNumber = 0;

    public PieceConfiguration(Piece[][] pieceGrid) {
        this.pieceGrid = pieceGrid;
    }

    public PieceConfiguration(PieceConfiguration previousGame, Piece[][] pieceGrid, Colour turn, boolean isBoardRotated) {
        this.pieceGrid = pieceGrid;
        this.previousGame = previousGame;
        this.isBoardRotated = isBoardRotated;
        this.turn = turn;
        previousGame.setNextGame(this);

        boardNumber = previousGame.boardNumber + 1;
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

    public int countPreviousPieceConfigurations() {
        PieceConfiguration root = this;
        while (root.hasPreviousGame()) {
            root = root.getPreviousGame();
        }

        int i = 0;
        while (root.hasNextGame()) {
            root = root.getNextGame();
            System.out.println(root.boardNumber);
            i++;
        }

        return i;
    }

    @Override
    public String toString() {
        return String.valueOf(boardNumber) + " " + turn.toString() + " " + String.valueOf(hasPreviousGame()) + " " + String.valueOf(hasNextGame());
    }
}
