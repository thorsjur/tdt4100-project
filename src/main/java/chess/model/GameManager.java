package chess.model;

public class GameManager {

    Game game;
    boolean boardRotationEnabled;

    public GameManager() {
    }

    public void startNewGame(String playerOneName, String playerTwoName, boolean boardRotationEnabled) {
        // TODO: Implementere navn
        game = new Game();
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
