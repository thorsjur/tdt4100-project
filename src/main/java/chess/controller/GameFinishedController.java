package chess.controller;

import chess.io.GameReaderWriter;
import chess.model.Colour;
import chess.model.Game;
import chess.model.GameManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GameFinishedController {
    
    private GameManager gameManager;

    @FXML
    Label winnerLabel;

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @FXML
    private void handleOnExitButtonClick() {
        // Flytter exit til hovedcontrolleren (ChessController)
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });

        closeStage();
    }

    @FXML
    private void handleOnSaveButtonClick() {
        GameReaderWriter grw = new GameReaderWriter();
        grw.save(gameManager.getGame());
    }

    @FXML
    public void handleOnNewGameButtonClick() {
        Game prevGame = gameManager.getGame();
        gameManager.startNewGame(prevGame.getPlayerOneName(), prevGame.getPlayerTwoName(), gameManager.isBoardRotationEnabled());
        closeStage();
    }

    private void closeStage() {
        // Rekke metoder for å hente vinduet; henter først noden som ble trykket på, så scenen og deretter vinduet.
        Stage stage = (Stage) winnerLabel.getScene().getWindow();
        stage.close();
    }

    public void setLabel() {
        Colour winner = gameManager.getWinner();
        if (winner == null) {
            winnerLabel.setText("Stalemate!");
        } else {
            winnerLabel.setText(winner.toString() + " wins!");
        }
    }

}
