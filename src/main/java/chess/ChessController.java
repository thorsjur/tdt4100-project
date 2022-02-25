package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javafx.scene.Node;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
import java.io.File;
import javafx.scene.input.MouseEvent;

public class ChessController {
    @FXML
    private TextField firstNumber, secondNumber, operator;

    private Game game;

    private List<Pane> paneList = new ArrayList<>();
    private List<ImageView> imageViewList = new ArrayList<>();
    private Pane selectedPane;
    private Piece selectedPiece;
    
    @FXML
    GridPane gridPane;

    public void updateBoard() {
        HashMap<String, ImageView> imageMap = new HashMap<String, ImageView>();
        for (ImageView imageView : imageViewList) {
            String imageViewName = imageView.idProperty().getValue();
            imageMap.put(imageViewName.substring(imageViewName.length() - 2, imageViewName.length()), imageView);
        }

        Piece[][] boardGrid = game.getBoard().getGrid();
        for (String key : imageMap.keySet()) {
            int row = key.charAt(0) - '0';
            int col = key.charAt(1) - '0';
            ImageView currentImageView = imageMap.get(key);
            Piece piece = boardGrid[row][col];
            String imagePath = "";

            if (piece != null) {
                imagePath = "src/images/" + piece.toString() + ".png";
                currentImageView.setImage(new Image(new File(imagePath).toURI().toString()));
            } else {
                currentImageView.setImage(null);
            }
        }
   
    }

    @FXML
    public void handleOnPieceClick(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getPickResult().getIntersectedNode();
        if (selectedPane == null) {
            selectPane(clickedImageView);
            return;
        }
        if (selectedPane == clickedImageView.getParent()) {
            deselectPane();
            return;
        }
        int[] coordinatesOfClickedPane = Board.getCoordinatesOfSquare(clickedImageView.getId());
        if (game.getBoard().isValidMove(selectedPiece, coordinatesOfClickedPane)) {
            game.makeMove(selectedPiece, coordinatesOfClickedPane);
            deselectPane();
        } else {
            deselectPane();
            selectPane(clickedImageView);
        }
        updateBoard(); 
    }

    private void selectPane(ImageView selectedImageView) {
        String imageViewId = selectedImageView.getId();
        selectedPiece = game.getBoard().getPiece(Board.getCoordinatesOfSquare(imageViewId));
        
        if (selectedImageView == null || selectedPiece == null || selectedPiece.getColour() != game.getTurn()) {
            return;
        }
        selectedPane = (Pane) selectedImageView.getParent();
        highlightPane(selectedPane);
    }

    private void deselectPane() {
        if (selectedPane != null) {
            removeHighlightPane(selectedPane);
            selectedPane = null;
        }
        selectedPiece = null;
    }

    private void highlightPane(Pane pane) {
        if (pane == null) {
            return;
        }
        pane.setStyle("-fx-background-color: " + "#429d42");
    }

    private void removeHighlightPane(Pane pane) {
        if (pane == null) {
            return;
        }
        String paneId = pane.getId();
        String hexColour = Board.getColourOfSquare(Board.getCoordinatesOfSquare(paneId)).getHexColour();
        pane.setStyle("-fx-background-color: " + hexColour);
    }

    private void initializePanesAndImageViews() {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Pane) {
                Pane pane = (Pane) node;
                paneList.add(pane);

                imageViewList.add((ImageView) pane.getChildren().get(0));
            }
        }
    }

    @FXML
    public void initialize() {
        initializePanesAndImageViews();
        game = new Game();
        this.updateBoard();

        

        paneList.get(1).setStyle("-fx-background-color: #ffffff");

        System.out.println(paneList);
        }

    

}