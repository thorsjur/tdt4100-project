package chess;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * @author Thor Sjursen
 */
public class ChessApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Chess");
        primaryStage.getIcons().add(new Image(new File("src/images/WhiteKing.png").toURI().toString()));
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("App.fxml")));
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        /* primaryStage.setMaximized(true); */
        /* primaryStage.setFullScreen(true); */
        primaryStage.show();
    }

}