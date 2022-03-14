package chess.model;

import java.util.HashMap;
import java.util.List;

public class GameManager {

    Game game;
    List<Square> squareList;
    boolean boardRotationEnabled;

    public GameManager(List<Square> squareList) {
        this.squareList = squareList;
    }

    public void startNewGame(String playerOneName, String playerTwoName, boolean boardRotationEnabled) {
        // TODO: Implementere navn
        game = new Game(squareList);
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

    public List<Square> getSquareList() {
        return squareList;
    }

    public Colour getWinner() {
        if (! game.isFinished()) {
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

    public HashMap<Square, String> getSquareToPathMap() {
        HashMap<Square, String> squarePathMap = new HashMap<Square, String>();

        for (Square square : squareList) {
            Piece pieceAtSquare = square.getPiece();
            if (pieceAtSquare == null) {
                squarePathMap.put(square, null);
                continue;
            }
            String imagePath = "src/main/resources/images/" + pieceAtSquare.toString() + ".png";
            squarePathMap.put(square, imagePath);
        }
        return squarePathMap;
    }



}
