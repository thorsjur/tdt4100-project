package chess;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsController {

    private Game game;

    @FXML
    private Button cancelButton, okButton, applyButton;

    @FXML
    private CheckBox rotationCheckBox, exampleOne, exampleTwo;

    @FXML
    private VBox checkBoxes;

    @FXML
    private void handleOnCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleOnApplyButtonClick() {
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

    public void checkTrueCheckBoxes(boolean boardRotationEnabled) {
        rotationCheckBox.setSelected(boardRotationEnabled);
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
