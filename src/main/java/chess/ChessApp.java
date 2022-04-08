package chess;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ChessApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Chess");
        primaryStage.getIcons().add(new Image(new File("src/main/resources/images/WhiteKing.png").toURI().toString()));
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("App.fxml")));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}