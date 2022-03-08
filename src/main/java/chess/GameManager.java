package chess;

import java.util.HashMap;
import java.util.List;

public class GameManager {

    Game currentGame;
    List<Square> squareList;
    boolean boardRotationEnabled;

    public GameManager(List<Square> squareList) {
        this.squareList = squareList;
    }

    public void startNewGame(String playerOneName, String playerTwoName, boolean boardRotationEnabled) {
        // TODO: Implementere navn
        currentGame = new Game(squareList);
        currentGame.getBoard().setBoardRotation(boardRotationEnabled);
    }

    public Board getBoard() {
        return currentGame.getBoard();
    }

    public Game getGame() {
        return currentGame;
    }

    public Colour getTurn() {
        return getBoard().getTurn();
    }

    public List<Square> getSquareList() {
        return squareList;
    }

    public Colour getWinner() {
        if (! currentGame.isFinished()) {
            throw new IllegalStateException("Current game is not finished!");
        }

        return currentGame.getWinner();
    }

    public boolean isBoardRotationEnabled() {
        return currentGame.getBoard().isBoardRotationEnabled();
    }

    public HashMap<Square, String> getSquareToPathMap() {
        HashMap<Square, String> squarePathMap = new HashMap<Square, String>();

        for (Square square : squareList) {
            Piece pieceAtSquare = square.getPiece();
            if (pieceAtSquare == null) {
                squarePathMap.put(square, null);
                continue;
            }
            String imagePath = "src/images/" + pieceAtSquare.toString() + ".png";
            squarePathMap.put(square, imagePath);
        }
        return squarePathMap;

        
    }

}
