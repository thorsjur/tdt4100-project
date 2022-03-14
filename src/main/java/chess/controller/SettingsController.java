package chess.controller;

import java.util.Optional;

import chess.model.Game;
import chess.model.GameManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsController {

    private GameManager gameManager;

    @FXML
    private AnchorPane root;

    @FXML
    private Button cancelButton, okButton, applyButton;

    @FXML
    private CheckBox rotationCheckBox;

    @FXML
    private VBox checkBoxes;

    @FXML
    private void handleOnCancelButtonClick() {
        closeStage();
    }

    @FXML
    private void handleOnApplyButtonClick() {
        Game game = gameManager.getGame();
        if (rotationCheckBox.isSelected()) {
            game.enableBoardRotation();
        } else {
            game.disableBoardRotation();
        }
        applyButton.setDisable(true);
    }

    @FXML
    private void handleOnOkButtonClick() {
        handleOnApplyButtonClick();
        handleOnCancelButtonClick();
    }

    @FXML
    private void handleOnCheckBoxClick() {
        if (applyButton.isDisabled()) {
            applyButton.setDisable(false);
        }
    }

    @FXML
    private void handleOnNewGameButtonClick() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("New Game");
        alert.setHeaderText("Are you sure you want to create a new game?");
        alert.setContentText("Unless you have saved, there is no way to continue the game!");
        alert.initOwner(root.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: Implementere navn
            gameManager.startNewGame("playerOneName", "playerTwoName", gameManager.isBoardRotationEnabled());
            closeStage();
        }
    }

    public void checkTrueCheckBoxes(boolean boardRotationEnabled) {
        rotationCheckBox.setSelected(boardRotationEnabled);
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
