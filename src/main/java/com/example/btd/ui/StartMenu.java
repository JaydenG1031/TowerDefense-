package com.example.btd.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.example.btd.game.LeaderboardManager;

/**
 * Simple start menu for the game.
 */
public class StartMenu extends StackPane {

    public StartMenu(Stage primaryStage) {
        setPrefSize(800, 600);

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);

        Text title = new Text("Balloon Tower Defense");
        title.setStyle("-fx-font-size: 48; -fx-fill: white; -fx-font-weight: bold;");

        Button playButton = new Button("Play");
        Button leaderboardButton = new Button("Leaderboard");
        Button settingsButton = new Button("Settings");
        Button exitButton = new Button("Exit");

        playButton.setStyle("-fx-font-size: 20; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        leaderboardButton.setStyle("-fx-font-size: 20; -fx-background-color: #2196F3; -fx-text-fill: white;");
        settingsButton.setStyle("-fx-font-size: 20; -fx-background-color: #9E9E9E; -fx-text-fill: white;");
        exitButton.setStyle("-fx-font-size: 20; -fx-background-color: #f44336; -fx-text-fill: white;");

        playButton.setOnAction(e -> {
            TextInputDialog nameDialog = new TextInputDialog("Player");
            nameDialog.setTitle("Welcome to Balloon Tower Defense");
            nameDialog.setHeaderText("Enter Your Name");
            nameDialog.setContentText("Please enter your name:");
            nameDialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());

            nameDialog.showAndWait().ifPresent(playerName -> {
                // Calculate sizes based on screen
                javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                double gameAreaWidth = screenBounds.getWidth() * 0.8;
                double gameAreaHeight = screenBounds.getHeight();
                double panelWidth = screenBounds.getWidth() * 0.2;

                GameScene gameScene = new GameScene(gameAreaWidth, gameAreaHeight, panelWidth, playerName);
                // Replace root of current scene with the game scene
                primaryStage.getScene().setRoot(gameScene);
            });
        });

        leaderboardButton.setOnAction(e -> {
            LeaderboardManager lm = new LeaderboardManager();
            lm.loadScores();
        });

        settingsButton.setOnAction(e -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Settings");
            alert.setHeaderText(null);
            alert.setContentText("Settings are not implemented yet.");
            alert.showAndWait();
        });

        exitButton.setOnAction(e -> primaryStage.close());

        container.getChildren().addAll(title, playButton, leaderboardButton, settingsButton, exitButton);
        getChildren().add(container);
    }
}
