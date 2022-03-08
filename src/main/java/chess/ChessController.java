package chess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChessController {

    private GameManager gameManager;
    private List<Square> squareList = new ArrayList<>();

    @FXML
    private GridPane gridPane;

    @FXML
    private Button settingsButton; 

    private void updateBoard() {
        HashMap<Square, String> squarePathMap = gameManager.getSquareToPathMap();

        for (Square square : squarePathMap.keySet()) {
            ImageView pieceImageView = (ImageView) square.getChildren().get(0);
            if (squarePathMap.get(square) == null) {
                pieceImageView.setImage(null);
            } else {
                String imagePath = squarePathMap.get(square);
                pieceImageView.setImage(new Image(new File(imagePath).toURI().toString()));
            }
        }
    }

    @FXML
    private void handleOnPieceClick(MouseEvent event) {
        Game game = gameManager.getGame();
        Square selectedSquare = game.getSelectedSquare();
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
        if (selectedPiece != null && game.isValidMove(selectedPiece, coordinates)) {
            game.makeMove(selectedPiece, coordinates);
            if (game.checkForMate()) {
                initializeGameFinished();
            }
            if (selectedPiece instanceof Pawn && ((Pawn) selectedPiece).canPromote()) {
                pawnPromotion((Pawn) selectedPiece);
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

        settingsController.setGameManager(gameManager);
        settingsController.checkTrueCheckBoxes(gameManager.isBoardRotationEnabled());
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.showAndWait();
    }

    private void pawnPromotion(Pawn pawn) {
        Stage pawnPromotionStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PawnPromotion.fxml"));
        Scene scene;
        
        try {
            scene = new Scene(loader.load());
            pawnPromotionStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't read PawnPromotion.fxml");
            return;
        }
        pawnPromotionStage.setResizable(false);

        PawnPromotionController pawnPromotionController = loader.getController();
        pawnPromotionController.setPawn(pawn);
        pawnPromotionController.initializePieces();

        scene.setFill(Color.TRANSPARENT);

        pawnPromotionStage.initStyle(StageStyle.TRANSPARENT);
        pawnPromotionStage.initModality(Modality.APPLICATION_MODAL);
        pawnPromotionStage.showAndWait();
    }

    private void initializeGameFinished() {
        Stage finishStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameFinished.fxml"));

        finishStage.setTitle(gameManager.getTurn().toString());
        finishStage.getIcons().add(new Image(new File("src/images/WhiteKing.png").toURI().toString()));
        try {
            finishStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            System.out.print("Can't load file GameFinished.fxml! ");
            e.printStackTrace();
        }
        finishStage.setResizable(false);
        GameFinishedController gameFinishedController = loader.getController();

        gameFinishedController.setGameManager(gameManager);
        gameFinishedController.setLabel();

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
        gameManager = new GameManager(squareList);

        // TODO: implementere navn
        gameManager.startNewGame("playerOneName", "playerTwoName", false);


        // Neste del delvis hentet fra https://edencoding.com/periodic-background-tasks/
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Board board = gameManager.getBoard();
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