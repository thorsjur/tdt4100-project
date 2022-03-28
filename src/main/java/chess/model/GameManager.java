package chess.model;

public class GameManager {

    private Game game;
    private boolean boardRotationEnabled = false;
    private boolean displayModeEnabled = false;


    public void startNewGame(String playerOneName, String playerTwoName, boolean boardRotationEnabled) {
        game = new Game(playerOneName, playerTwoName);
        game.getBoard().setBoardRotation(boardRotationEnabled);

        this.displayModeEnabled = false;
    }

    public void initializeGameFromPC(Colour winner, PieceConfiguration pieceConfiguration) {
        Game prevGame = game;
        game = new Game(winner, pieceConfiguration);
        game.setPlayerOneName(prevGame.getPlayerOneName());
        game.setPlayerTwoName(prevGame.getPlayerTwoName());

        displayModeEnabled = winner != null;
        game.getBoard().setBoardRotation(boardRotationEnabled);
    }

    public Board getBoard() {
        return game.getBoard();
    }

    public Game getGame() {
        return game;
    }

    public Colour getTurn() {
        return getBoard().getTurn();
    }

    public Colour getWinner() {
        if (!game.isFinished()) {
            throw new IllegalStateException("Current game is not finished!");
        }

        return game.getWinner();
    }

    public boolean isAtCurrentBoard() {
        return game.getBoard().getPieceConfiguration().isAtCurrentBoard();
    }

    public boolean isDisplayModeEnabled() {
        return displayModeEnabled;
    }

    public void goToCurrentBoard() {
        getBoard().goToCurrentPieceConfiguration();
        if (game.getSelectedSquare() != null) {
            Square.deselectSelectedSquare(game.getBoard());
        }
    }

    public void goToPreviousBoard() {
        getBoard().goToPreviousPieceConfiguration();
        if (game.getSelectedSquare() != null) {
            Square.deselectSelectedSquare(game.getBoard());
        }
    }

    public void goToNextBoard() {
        getBoard().goToNextPieceConfiguration();
        if (game.getSelectedSquare() != null) {
            Square.deselectSelectedSquare(game.getBoard());
        }
    }

    public boolean isBoardRotationEnabled() {
        return game.getBoard().isBoardRotationEnabled();
    }

}
