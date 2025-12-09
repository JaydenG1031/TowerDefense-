package com.example.btd;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.btd.ui.GameScene;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create name input dialog
        javafx.scene.control.TextInputDialog nameDialog = new javafx.scene.control.TextInputDialog("Player");
        nameDialog.setTitle("Welcome to Balloon Tower Defense");
        nameDialog.setHeaderText("Enter Your Name");
        nameDialog.setContentText("Please enter your name:");

        // Style the dialog
        nameDialog.getDialogPane().setStyle("-fx-background-color: #2c3e50;");
        nameDialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());

        // Wait for the player's name and start the game
        java.util.Optional<String> result = nameDialog.showAndWait();
        if (result.isPresent()) {
            String playerName = result.get();
            
            // Use screen size for fullscreen
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            double gameAreaWidth = screenBounds.getWidth() * 0.8; // Game area takes 80% of screen width
            double gameAreaHeight = screenBounds.getHeight();
            double panelWidth = screenBounds.getWidth() * 0.2; // Panel takes 20% of screen width
            GameScene gameScene = new GameScene(gameAreaWidth, gameAreaHeight, panelWidth, playerName);
            Scene scene = new Scene(gameScene, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());

            primaryStage.setTitle("Balloon Tower Defense");
            primaryStage.setScene(scene);
            // Enable fullscreen
            primaryStage.setFullScreen(true);
            // Optionally allow resizing
            // primaryStage.setResizable(true);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}