package chess;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Chess");
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("App.fxml"))));
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        /* primaryStage.setMaximized(true); */
        /* primaryStage.setFullScreen(true); */
        primaryStage.show();
    }

}