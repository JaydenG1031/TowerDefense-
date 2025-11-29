package com.example.btd;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Show the main start menu first
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth();
        double height = screenBounds.getHeight();

        com.example.btd.ui.StartMenu startMenu = new com.example.btd.ui.StartMenu(primaryStage);
        Scene scene = new Scene(startMenu, width, height);
        scene.getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());

        primaryStage.setTitle("Balloon Tower Defense");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
