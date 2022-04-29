package chess.controller;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import chess.model.Piece;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PawnPromotionController {

    Piece pawn;
    char pieceChar;

    @FXML
    HBox images;

    @FXML
    public void handleOnPieceClick(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getPickResult().getIntersectedNode();
        promotePawn(clickedImageView.getId());

        Stage stage = (Stage) clickedImageView.getScene().getWindow();
        stage.close();
    }

    public void setPawn(Piece pawn) {
        this.pawn = pawn;
    }

    public void initializePieces() {
        List<ImageView> imageViewList = images.getChildren().stream()
                .filter(child -> child instanceof ImageView)
                .map(child -> (ImageView) child)
                .collect(Collectors.toList());

        String[] imageEndPaths = { "Queen", "Rook", "Knight", "Bishop" };
        for (int i = 0; i < 4; i++) {
            String colour = pawn.getColour().toString();
            String imagePath = "src/main/resources/images/" + colour + imageEndPaths[i] + ".png";
            ImageView imageView = imageViewList.get(i);
            imageView.setImage(new Image(new File(imagePath).toURI().toString()));
        }
    }

    private void promotePawn(String clickedPiece) {
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
                pieceChar = 'N';
                break;
            default:
                pieceChar = 'P';
        }
        this.pieceChar = pieceChar;
    }

    public char getReturn() {
        return pieceChar;
    }

}
