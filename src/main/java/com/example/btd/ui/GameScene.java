package com.example.btd.ui;

import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import com.example.btd.game.GameManager;

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

    private void showRefundMessage(double x, double y) {
        // Create floating text message
        javafx.scene.text.Text refundText = new javafx.scene.text.Text("Tower Sold! (50% refunded)");
        refundText.setStyle("-fx-fill: green; -fx-font-size: 16; -fx-font-weight: bold;");
        refundText.setX(x);
        refundText.setY(y);

        // Add to scene
        getChildren().add(refundText);

        // Create fade out animation
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
            javafx.util.Duration.seconds(1.5), refundText
        );
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> getChildren().remove(refundText));
        
        // Create float up animation
        javafx.animation.TranslateTransition floatUp = new javafx.animation.TranslateTransition(
            javafx.util.Duration.seconds(1.5), refundText
        );
        floatUp.setByY(-30);

        // Play animations
        fadeOut.play();
        floatUp.play();
    }

    private void setupGameOverChecker() {
        gameOverChecker = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameManager.isGameOver()) {
                    showGameOverScreen();
                    gameOverChecker.stop();
                }
            }
        };
        gameOverChecker.start();
    }

    private void showGameOverScreen() {
        // Create a semi-transparent overlay
        javafx.scene.layout.VBox gameOverPane = new javafx.scene.layout.VBox(20);
        gameOverPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20;");
        gameOverPane.setAlignment(javafx.geometry.Pos.CENTER);
        gameOverPane.setPrefSize(getWidth(), getHeight());

        // Game Over text
        javafx.scene.text.Text gameOverText = new javafx.scene.text.Text("Game Over");
        gameOverText.setStyle("-fx-fill: white; -fx-font-size: 48; -fx-font-weight: bold;");

        // Score text
        javafx.scene.text.Text scoreText = new javafx.scene.text.Text(
            String.format("Score: %d", gameManager.getScore())
        );
        scoreText.setStyle("-fx-fill: white; -fx-font-size: 24;");

        // Create buttons
        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(20);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.control.Button restartButton = new javafx.scene.control.Button("Play Again");
        restartButton.setStyle("-fx-font-size: 18; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        restartButton.setOnAction(e -> restartGame());

        javafx.scene.control.Button leaderboardButton = new javafx.scene.control.Button("View Leaderboard");
        leaderboardButton.setStyle("-fx-font-size: 18; -fx-background-color: #2196F3; -fx-text-fill: white;");
        leaderboardButton.setOnAction(e -> showLeaderboard());

        buttons.getChildren().addAll(restartButton, leaderboardButton);

        gameOverPane.getChildren().addAll(gameOverText, scoreText, buttons);
        getChildren().add(gameOverPane);
    }

    private void restartGame() {
        // Get the current stage
        javafx.stage.Stage stage = (javafx.stage.Stage) getScene().getWindow();
        
        // Create name input dialog for new game
        javafx.scene.control.TextInputDialog nameDialog = new javafx.scene.control.TextInputDialog(gameManager.getPlayerName());
        nameDialog.setTitle("New Game");
        nameDialog.setHeaderText("Enter Your Name");
        nameDialog.setContentText("Please enter your name:");
        nameDialog.getDialogPane().setStyle("-fx-background-color: #2c3e50;");
        
        nameDialog.showAndWait().ifPresent(playerName -> {
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            double newGameAreaWidth = screenBounds.getWidth() * 0.8;
            double newGameAreaHeight = screenBounds.getHeight();
            double newPanelWidth = screenBounds.getWidth() * 0.2;
            
            GameScene newGameScene = new GameScene(newGameAreaWidth, newGameAreaHeight, newPanelWidth, playerName);
            stage.getScene().setRoot(newGameScene);
        });
    }

    private void setupGameUI(double panelWidth) {
        // Set up UI elements like score display, tower selection, etc.
        TowerSelectionPanel towerPanel = new TowerSelectionPanel(gameManager);
        // Place at right edge of game area
        towerPanel.setLayoutX(gameAreaWidth);
        towerPanel.setLayoutY(0);

        // Create leaderboard button
        javafx.scene.control.Button leaderboardButton = new javafx.scene.control.Button("Leaderboard");
        leaderboardButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        leaderboardButton.setLayoutX(10);
        leaderboardButton.setLayoutY(10);
        leaderboardButton.setOnAction(e -> showLeaderboard());

        getChildren().addAll(towerPanel, leaderboardButton);
        towerPanel.toFront();
        leaderboardButton.toFront();
    }
    
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
        
        leaderboardContent.getChildren().addAll(title, scoresList, closeButton);
        leaderboardContent.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Set up the scene
        javafx.scene.Scene leaderboardScene = new javafx.scene.Scene(leaderboardContent);
        leaderboardStage.setScene(leaderboardScene);
        leaderboardStage.show();
    }
}