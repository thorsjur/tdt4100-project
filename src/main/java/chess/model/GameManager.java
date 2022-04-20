package chess.model;

import java.io.IOException;

import chess.io.ChessIO;
import chess.model.Board.Coordinate;

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

    public void saveGame(ChessIO chessIO) {
        try {
            chessIO.save(game);
            System.err.println("Game saved successfully ...");
        } catch (IOException e) {
            System.err.println("Unable to save game, please try again ...");
        }
    }

    public Board getBoard() {
        return game.getBoard();
    }

    public Game getGame() {
        return game;
    }

    public Square getSquareAtCoordinate(int row, int col) {
        return getBoard().getSquare(new Coordinate(row, col));
    }

    public Colour getTurn() {
        return getBoard().getTurn();
    }

    public boolean isGameFinished() {
        return game.checkForMate();
    }

    public boolean isPawnApplicableForPromotion(Piece piece) {
        return (piece instanceof Pawn && ((Pawn) piece).canPromote());
    }

    public void promotePawn(Piece pawn, char pieceChar) {
        if (!isPawnApplicableForPromotion(pawn)) throw new IllegalArgumentException("Feil brikke!");

        getBoard().promotePawn((Pawn) pawn, pieceChar);
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

    public Square selectSquare(int row, int col) {
        Coordinate coordinates = new Coordinate(row, col);
        Board board = game.getBoard();
        Square selectedSquare = board.getSelectedSquare();
        Square newSelectedSquare = board.getSquare(coordinates);

        if (selectedSquare == null) {
            newSelectedSquare.selectSquare();
            return newSelectedSquare;
        }
        if (selectedSquare == newSelectedSquare) {
            Square.deselectSelectedSquare(getBoard());
            return newSelectedSquare;
        }

        Piece selectedPiece = selectedSquare.getPiece();
        if (selectedPiece != null && getGame().isValidMove(selectedPiece, coordinates)) {
            getGame().makeMove(selectedPiece, coordinates);

            // Hvis brettet roteres etter hver tur, vil selve ruten forholde seg på samme plass,
            // men brikkene vil flyttes, så for å passe på at "rett" rute gis, må den invertere
            // rad og rekke.
            if (board.isBoardRotationEnabled()) {
                Coordinate squareCoordinate = newSelectedSquare.getCoordinate();
                return board.getSquare(new Coordinate(7 - squareCoordinate.row(), 7 - squareCoordinate.column()));
            }

            return newSelectedSquare;
        }

        Square.deselectSelectedSquare(getBoard());
        newSelectedSquare.selectSquare();
        return newSelectedSquare;

    }


}
