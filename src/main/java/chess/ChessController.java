package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
import java.io.File;

public class ChessController {
    @FXML
    private TextField firstNumber, secondNumber, operator;

    private Game game;

    @FXML
    private ImageView 
        image00, image01, image02, image03, image04, image05, image06, image07,
        image10, image11, image12, image13, image14, image15, image16, image17,
        image20, image21, image22, image23, image24, image25, image26, image27,
        image30, image31, image32, image33, image34, image35, image36, image37,
        image40, image41, image42, image43, image44, image45, image46, image47,
        image50, image51, image52, image53, image54, image55, image56, image57,
        image60, image61, image62, image63, image64, image65, image66, image67,
        image70, image71, image72, image73, image74, image75, image76, image77;

    private List<ImageView> imageViewList;
    
    @FXML
    GridPane gridPane;

    @FXML
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
                System.out.println(imagePath);
            } else {
                currentImageView.setImage(null);
            }
        }

        
        
    }

    @FXML
    public void initialize() {
        /* imageTest.setImage(new Image(new File("images/WhitePawn.png").toURI().toString())); */
        
        imageViewList = Arrays.asList(
            image00, image01, image02, image03, image04, image05, image06, image07,
            image10, image11, image12, image13, image14, image15, image16, image17,
            image20, image21, image22, image23, image24, image25, image26, image27,
            image30, image31, image32, image33, image34, image35, image36, image37,
            image40, image41, image42, image43, image44, image45, image46, image47,
            image50, image51, image52, image53, image54, image55, image56, image57,
            image60, image61, image62, image63, image64, image65, image66, image67,
            image70, image71, image72, image73, image74, image75, image76, image77
        );
        game = new Game();
        this.updateBoard();
        /* image32.setImage(new Image(new File("src/images/WhitePawn.png").toURI().toString())); */
        
        
        

        

        }

    

}