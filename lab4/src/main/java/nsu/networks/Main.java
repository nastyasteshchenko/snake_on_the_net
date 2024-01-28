package nsu.networks;

import javafx.application.Application;
import javafx.stage.Stage;
import nsu.networks.controllers.MainController;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setResizable(false);
        MainController.getInstance().setStage(stage);
        MainController.getInstance().startApp();
    }

    public static void main(String[] args) {
        launch();
    }
}