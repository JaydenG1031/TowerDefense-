package com.example.btd.ui;

import com.example.btd.game.GameManager;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class GameScene extends Pane {
    private Canvas gameCanvas;
    private GameManager gameManager;
    private double gameAreaWidth;
    private javafx.animation.AnimationTimer gameOverChecker;

    public GameScene(double gameAreaWidth, double gameAreaHeight, double panelWidth, String playerName) {
        this.gameAreaWidth = gameAreaWidth;
        gameCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        getChildren().add(gameCanvas);
        gameManager = new GameManager(gameCanvas, playerName);

        // Add mouse event handler for tower selection and deletion
        gameCanvas.setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                // Right click - attempt to delete tower
                if (gameManager.deleteTower(event.getX(), event.getY())) {
                    // Show refund message
                    showRefundMessage(event.getX(), event.getY());
                }
            } else if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                // Left click - handle tower selection
                gameManager.selectTowerAt(event.getX(), event.getY());
            }
        });

        setupGameUI(panelWidth);
        setupGameOverChecker();
    }

    // ... other methods unchanged ...

    private void showLeaderboard() {
        // Create a new stage for the leaderboard
        javafx.stage.Stage leaderboardStage = new javafx.stage.Stage();
        leaderboardStage.setTitle("Leaderboard");
        
        // Create leaderboard content
        javafx.scene.layout.VBox leaderboardContent = new javafx.scene.layout.VBox(10);
        leaderboardContent.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");
        leaderboardContent.setPrefSize(400, 500);
        
        javafx.scene.text.Text title = new javafx.scene.text.Text("Top Scores");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        // Get scores from LeaderboardManager
        javafx.scene.layout.VBox scoresList = new javafx.scene.layout.VBox(5);
        scoresList.setStyle("-fx-padding: 10;");
        
        // Load and display actual scores
        gameManager.showLeaderboard();
        java.util.List<com.example.btd.game.LeaderboardEntry> scores = gameManager.getLeaderboardManager().getTopScores(10);
        
        for (com.example.btd.game.LeaderboardEntry score : scores) {
            javafx.scene.text.Text scoreText = new javafx.scene.text.Text(
                String.format("%s - %d points", score.getPlayerName(), score.getScore())
            );
            scoreText.setStyle("-fx-font-size: 16;");
            scoresList.getChildren().add(scoreText);
        }
        
        // Add close button
        javafx.scene.control.Button closeButton = new javafx.scene.control.Button("Close");
        closeButton.setOnAction(e -> leaderboardStage.close());

        // Add a Back to Main Menu button so players can return to the start menu
        javafx.scene.control.Button backToMenuButton = new javafx.scene.control.Button("Back to Main Menu");
        backToMenuButton.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white;");
        backToMenuButton.setOnAction(e -> {
            // Replace the current scene root with the StartMenu
            javafx.stage.Stage mainStage = (javafx.stage.Stage) getScene().getWindow();
            com.example.btd.ui.StartMenu startMenu = new com.example.btd.ui.StartMenu(mainStage);
            mainStage.getScene().setRoot(startMenu);
            leaderboardStage.close();
        });
        
    leaderboardContent.getChildren().addAll(title, scoresList, new javafx.scene.layout.HBox(10, closeButton, backToMenuButton));
        leaderboardContent.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Set up the scene
        javafx.scene.Scene leaderboardScene = new javafx.scene.Scene(leaderboardContent);
        leaderboardStage.setScene(leaderboardScene);
        leaderboardStage.show();
    }
}
