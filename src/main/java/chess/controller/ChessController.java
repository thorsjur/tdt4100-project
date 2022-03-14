package chess.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chess.model.Board;
import chess.model.Game;
import chess.model.GameManager;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.Square;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChessController {

    private GameManager gameManager;

    @FXML
    private VBox root;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button settingsButton; 

    @FXML
    private Button previousBoardButton;

    @FXML
    private Button nextBoardButton;

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

        if (! gameManager.isAtCurrentBoard()) {
            gameManager.goToCurrentBoard();
        }
        if (selectedSquare == null) {
            clickedSquare.selectSquare();
            return;
        }
        if (selectedSquare == clickedSquare) {
            Square.deselectSelectedSquare(game.getBoard());
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
        
        Square.deselectSelectedSquare(game.getBoard());
        clickedSquare.selectSquare();
    }

    @FXML
    private void handleOnSettingsButtonClick(MouseEvent event) throws IOException {
        Stage settingsStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chess/Settings.fxml"));

        settingsStage.setTitle("Settings");
        
        settingsStage.getIcons().add(new Image(new File("src/main/resources/images/WhiteKing.png").toURI().toString()));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chess/PawnPromotion.fxml"));
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

    @FXML
    private void handleOnPreviousBoardButtonClick() {
        gameManager.goToPreviousBoard();
    }

    @FXML
    private void handleOnNextBoardButtonClick() {
        gameManager.goToNextBoard();
    }

    @FXML
    private void handleOnReloadButtonClick() {
        gameManager.goToCurrentBoard();
    }

    private void initializeGameFinished() {
        Stage finishStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chess/GameFinished.fxml"));

        finishStage.setTitle(gameManager.getTurn().toString());
        finishStage.getIcons().add(new Image(new File("src/main/resources/images/WhiteKing.png").toURI().toString()));
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

    private List<Square> getSquaresInGridPane() {
        return Stream.of(gridPane.getChildren().toArray())
                .filter(node -> (node instanceof Square))
                .map(node -> (Square) node)
                .collect(Collectors.toList());
    }

    @FXML
    public void initialize() {
        gameManager = new GameManager(getSquaresInGridPane());

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

        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.LEFT) {
                    gameManager.goToPreviousBoard();
                } else if (event.getCode() == KeyCode.RIGHT) {
                    gameManager.goToNextBoard();
                }
                event.consume();
            }
        });

    }
}