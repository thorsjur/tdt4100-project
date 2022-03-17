package chess.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import chess.io.GameReaderWriter;
import chess.model.Board;
import chess.model.Board.Coordinate;
import chess.model.Colour;
import chess.model.Game;
import chess.model.GameManager;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.Square;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.Pane;
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
        displayPieces();
        displaySquares();
    }

    private void displaySquares() {
        Map<Pane, Square> paneToSquareMap = gridPane.getChildren().stream()
                .filter(node -> node instanceof Pane)
                .map(node -> (Pane) node)
                .collect(Collectors.toMap(pane -> pane,
                        pane -> gameManager.getBoard()
                                .getSquare(new Coordinate(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane)))));

        for (Pane pane : paneToSquareMap.keySet()) {
            ObservableList<String> styleClassList = pane.getStyleClass();

            styleClassList.clear();

            Square square = paneToSquareMap.get(pane);
            String styleClass;
            switch (square.getState()) {
                case VIABLE_MOVE:
                    styleClass = "viable-move-square";
                    break;
                case VIABLE_TAKE:
                    styleClass = "viable-take-square";
                    break;
                case VIABLE_CASTLE_DESTINATION:
                    styleClass = "viable-castle-destination-square";
                    break;
                case SELECTED:
                    styleClass = "selected-square";
                    break;
                default:
                    styleClass = null;
                    break;
            }

            if (styleClass != null) {
                styleClassList.add(styleClass);
            } else {
                String defaultSquareStyle = (square.getColour() == Colour.WHITE) ? "white-square" : "black-square";
                styleClassList.add(defaultSquareStyle);
            }
        }
    }

    private void displayPieces() {
        Map<ImageView, Square> imageViewToSquareMap = gridPane.getChildren().stream()
                .filter(node -> node instanceof Pane)
                .map(node -> (Pane) node)
                .collect(Collectors.toMap(pane -> (ImageView) pane.getChildren().get(0),
                        pane -> gameManager.getBoard()
                                .getSquare(new Coordinate(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane)))));

        String imagePathBasis = "src/main/resources/images/";
        for (ImageView imageView : imageViewToSquareMap.keySet()) {
            Square square = imageViewToSquareMap.get(imageView);
            Piece piece = square.getPiece();

            Image image = (piece != null)
                    ? new Image(new File(imagePathBasis + piece.toString() + ".png").toURI().toString())
                    : null;
            imageView.setImage(image);
        }
    }

    @FXML
    private void handleOnPieceClick(MouseEvent event) {
        Game game = gameManager.getGame();
        if (! gameManager.isAtCurrentBoard()) {
            gameManager.goToCurrentBoard();
        }

        Pane clickedPane = (Pane) event.getPickResult().getIntersectedNode().getParent();
        Square clickedSquare = game
                .selectSquare(new Coordinate(GridPane.getRowIndex(clickedPane), GridPane.getColumnIndex(clickedPane)));

        Piece piece = clickedSquare.getPiece();
        if (piece instanceof Pawn && ((Pawn)piece).canPromote()) {
            pawnPromotion((Pawn) piece);
        }

        if (game.checkForMate()) {
            initializeGameFinished();
        }
    }

    @FXML
    private void handleOnSaveButtonClick() {
        GameReaderWriter gameReaderWriter = new GameReaderWriter();
        try {
            gameReaderWriter.save(gameManager.getGame());
            System.out.println("Game saved successfully ...");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @FXML
    public void initialize() {
        gameManager = new GameManager();

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