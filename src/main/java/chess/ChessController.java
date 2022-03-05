package chess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChessController {

    private Game game;
    private List<Square> squareList = new ArrayList<>();

    @FXML
    private GridPane gridPane;

    @FXML
    private Button settingsButton; 

    public Game getGame() {
        return game;
    }

    private void updateBoard() {
        Square[][] squareGrid = game.getBoard().getChessBoard();
        for (Square[] row : squareGrid) {
            for (Square square : row) {
                Piece piece = square.getPiece();
                ImageView imageView = (ImageView) square.getChildren().get(0);
                if (piece != null) {
                    String imagePath = "src/images/" + piece.toString() + ".png";
                    imageView.setImage(new Image(new File(imagePath).toURI().toString()));
                } else {
                    imageView.setImage(null);
                }
            }
        }
    }

    @FXML
    private void handleOnPieceClick(MouseEvent event) {
        Board board = game.getBoard();
        Square selectedSquare = board.getSelectedSquare();
        Square clickedSquare = (Square) event.getPickResult().getIntersectedNode().getParent();

        if (selectedSquare == null) {
            clickedSquare.selectSquare();
            return;
        }

        if (selectedSquare == clickedSquare) {
            clickedSquare.deselectSquare();
            return;
        }

        Piece selectedPiece = selectedSquare.getPiece();
        int[] coordinates = clickedSquare.getCoordinates();
        if (selectedPiece != null && board.isValidMove(selectedPiece, coordinates)) {
            game.makeMove(selectedPiece, coordinates);
            if (board.isKingMated()) {
                initializeGameFinished();
            }
            return;
        }
        
        selectedSquare.deselectSquare();
        clickedSquare.selectSquare();
    }

    @FXML
    private void handleOnSettingsButtonClick(MouseEvent event) throws IOException {
        Stage settingsStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));

        settingsStage.setTitle("Settings");
        
        settingsStage.getIcons().add(new Image(new File("src/images/WhiteKing.png").toURI().toString()));
        settingsStage.setScene(new Scene(loader.load()));
        settingsStage.setResizable(false);
        SettingsController settingsController = loader.getController();

        settingsController.setGame(game);
        settingsController.checkTrueCheckBoxes(game.getBoard().isBoardRotationEnabled());
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.showAndWait();
    }

    private void initializeGameFinished() {
        Stage finishStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameFinished.fxml"));

        finishStage.setTitle(game.getTurn().toString());
        finishStage.getIcons().add(new Image(new File("src/images/WhiteKing.png").toURI().toString()));
        try {
            finishStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            System.out.print("Can't load file GameFinished.fxml! ");
            e.printStackTrace();
        }
        finishStage.setResizable(false);
        GameFinishedController gameFinishedController = loader.getController();

        gameFinishedController.setGame(game);

        finishStage.initModality(Modality.APPLICATION_MODAL);
        finishStage.showAndWait();
        
    }

    private void initializeSquaresAndPieces() {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Square) {
                Square square = (Square) node;
                squareList.add(square);
            }
        }
    }

    @FXML
    public void initialize() {
        initializeSquaresAndPieces();
        game = new Game(squareList);

        // Neste del delvis hentet fra https://edencoding.com/periodic-background-tasks/
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Board board = game.getBoard();
                if (board.isChanged()) {
                    Platform.runLater(() -> updateBoard());
                    board.setChanged(false);
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 33L);
    }
}