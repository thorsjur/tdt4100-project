package chess.controller;

import java.io.File;
import java.io.IOException;

import chess.io.GameReaderWriter;
import chess.model.Colour;
import chess.model.Game.GameData;
import chess.model.GameManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;

public class LoadController {

    private GameManager gameManager;
    private File file;

    @FXML
    private Button okButton, cancelButton, loadButton;

    @FXML
    private ListView<GameData> listViewGameData;

    @FXML
    private void handleOnLoadButtonClick() {
        GameData data = listViewGameData.getSelectionModel().getSelectedItem();
        if (data != null) {
            gameManager.initializeGameFromPC(data.winner(), data.currentPieceConfiguration());
        }

        closeStage();
    }

    @FXML
    private void handleOnOkButtonClick() {
        closeStage();
    }

    @FXML
    private void handleOnOpenFileButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Text files", "*" + GameReaderWriter.FILE_EXTENSION));
        fileChooser.setInitialDirectory(new File(GameReaderWriter.DIRECTORY_PATH));

        file = fileChooser.showOpenDialog((Stage) cancelButton.getScene().getWindow());
        GameReaderWriter grw = new GameReaderWriter();

        listViewGameData.getItems().clear();
        try {
            for (GameData data : grw.getData(file)) {
                listViewGameData.getItems().add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problem loading file, please try again ...");
        }

    }

    @FXML
    private void handleOnCancelButtonClick() {
        closeStage();
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @FXML
    private void initialize() {
        GameReaderWriter grw = new GameReaderWriter();

        try {
            for (GameData data : grw.getData(null)) {
                listViewGameData.getItems().add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeStage();
            return;
        }

        listViewGameData.setCellFactory(new Callback<ListView<GameData>, ListCell<GameData>>() {
            @Override
            public ListCell<GameData> call(ListView<GameData> param) {
                return new GameCell();
            }

        });
    }

    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private class GameCell extends ListCell<GameData> {

        private Text date;
        private Text players;
        private Text winner;
        private Text turns;
        private HBox content;

        public GameCell() {
            super();
            date = new Text();
            players = new Text();
            winner = new Text();
            turns = new Text();

            VBox leftContent = new VBox(players, date);
            VBox rightContent = new VBox(winner, turns);
            rightContent.setAlignment(Pos.CENTER_RIGHT);

            content = new HBox(leftContent, rightContent);
            content.getStyleClass().add("game-cell");
            players.getStyleClass().add("game-cell-names");

            HBox.setHgrow(leftContent, Priority.ALWAYS);
            HBox.setHgrow(rightContent, Priority.ALWAYS);

        }

        @Override
        protected void updateItem(GameData data, boolean empty) {
            super.updateItem(data, empty);
            if (data != null && !empty) {
                date.setText(data.date());
                players.setText(data.playerOneName() + " vs. " + data.playerTwoName());
                Colour colourWinner = data.winner();
                winner.setText((colourWinner == null) ? "No winner" : colourWinner.toString());
                turns.setText("Turns: " + data.moves());

                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }
}
