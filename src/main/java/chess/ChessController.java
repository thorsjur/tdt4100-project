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
import javafx.scene.control.Button;
import java.io.File;
import javafx.scene.input.MouseEvent;

public class ChessController {

    private Game game;
    private List<Square> squareList = new ArrayList<>();
    private List<ImageView> imageViewList = new ArrayList<>();
    private Piece selectedPiece;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button nextTurnButton;

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
        Board board = game.getBoard();
        Square selectedSquare = board.getSelectedSquare();
        Square clickedSquare = (Square) event.getPickResult().getIntersectedNode().getParent();
        Piece clickedPiece = clickedSquare.getPiece();

        if (selectedSquare == null) {
            clickedSquare.selectSquare(game.getTurn());
            if (clickedPiece != null && clickedPiece.getColour() == game.getTurn())
                clickedPiece.highlightValidMoves();
            updateBoard();
            return;
        }

        if (selectedSquare == clickedSquare) {
            clickedSquare.deselectSquare();
            board.removeAllHighlights();
            updateBoard();
            return;
        }

        selectedPiece = selectedSquare.getPiece();
        selectedPiece.highlightValidMoves();
        int[] coordinates = clickedSquare.getCoordinates();
        if (selectedPiece != null && board.isValidMove(selectedPiece, coordinates)) {
            game.makeMove(selectedPiece, coordinates);
            board.rotateBoard();
            game.nextTurn();
            clickedSquare.deselectSquare();
            board.removeAllHighlights();
            updateBoard();
            return;
        }
        
        selectedSquare.deselectSquare();
        clickedSquare.selectSquare(game.getTurn());
        board.removeAllHighlights();
        if (clickedPiece != null && clickedPiece.getColour() == game.getTurn())
            clickedPiece.highlightValidMoves();
        updateBoard();
    }

    @FXML
    public void handleOnButtonClick(MouseEvent event) {
        Board board = game.getBoard();
        game.getBoard().rotateBoard();
        game.nextTurn();
        board.deselectSquare();
        board.removeAllHighlights();
        updateBoard();
    }

    private void initializeSquaresAndPieces() {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Square) {
                Square square = (Square) node;
                squareList.add(square);
                imageViewList.add((ImageView) square.getChildren().get(0));
            }
        }
    }

    @FXML
    public void initialize() {
        initializeSquaresAndPieces();
        game = new Game(squareList);
        updateBoard();
    }

}