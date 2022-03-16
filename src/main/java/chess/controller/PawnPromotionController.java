package chess.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import chess.model.Board;
import chess.model.Pawn;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PawnPromotionController {

    Pawn pawn;

    @FXML HBox images;

    @FXML
    public void handleOnPieceClick(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getPickResult().getIntersectedNode();
        promotePawn(clickedImageView.getId());


        Stage stage = (Stage) clickedImageView.getScene().getWindow();
        stage.close();
    }

    public void setPawn(Pawn pawn) {
        this.pawn = pawn;
    }

    public void initializePieces() {
        List<ImageView> imageList = new ArrayList<ImageView>();
        
        for (Node child : images.getChildren()) {
            if (child instanceof ImageView) {
                imageList.add((ImageView) child);
            }
        }
        String[] imageEndPaths = { "Queen", "Rook", "Knight", "Bishop" };
        for (int i = 0; i < 4; i++) {
            String colour = pawn.getColour().toString();
            String imagePath = "src/main/resources/images/" + colour + imageEndPaths[i] + ".png";
            ImageView imageView = imageList.get(i);
            imageView.setImage(new Image(new File(imagePath).toURI().toString()));
        }
    }

    private void promotePawn(String clickedPiece) {
        Board board = pawn.getBoard();
        char pieceChar;

        switch (clickedPiece) {
            case "rook":
                pieceChar = 'R';
                break;
            case "queen":
                pieceChar = 'Q';
                break;
            case "bishop":
                pieceChar = 'B';
                break;
            case "knight":
                pieceChar = 'K';
                break;
            default:
                pieceChar = 'P';
        }
        board.promotePawn(pawn, pieceChar);
    }

    

    
}
