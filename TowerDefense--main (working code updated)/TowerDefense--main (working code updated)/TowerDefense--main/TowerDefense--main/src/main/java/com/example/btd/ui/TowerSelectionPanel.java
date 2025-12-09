package com.example.btd.ui;

import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import com.example.btd.game.GameManager;

public class TowerSelectionPanel extends VBox {
    private GameManager gameManager;
    private static final int PANEL_WIDTH = 200;

    public TowerSelectionPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        
     setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); background-color: rgba(0, 0, 0, 0.7); " +
         "-fx-padding: 10; padding: 10px; " + 
         "-fx-spacing: 10; margin: 10px;");
    setPrefWidth(PANEL_WIDTH);
    setMaxHeight(700);
        
        createTowerButtons();
        createGameInfo();
    }

    private void createTowerButtons() {
        addTowerButton("Basic Tower", 50, "basic");
        addTowerButton("Sniper Tower", 100, "sniper");
        addTowerButton("Machine Gun Tower", 150, "machine");
    }

    private void addTowerButton(String name, int cost, String towerType) {
        Button button = new Button(name + "\nCost: $" + cost);
        button.setPrefWidth(PANEL_WIDTH - 20);
        button.setWrapText(true);
        button.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        
        // Set initial state based on money
        button.setDisable(gameManager.getMoney() < cost);
        
        // Update button state when money changes
        javafx.animation.AnimationTimer moneyCheck = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                button.setDisable(gameManager.getMoney() < cost);
            }
        };
        moneyCheck.start();
        
        button.setOnAction(e -> {
            if (gameManager.getMoney() >= cost) {
                button.setDisable(true);
                
                // Start tower preview mode
                gameManager.startTowerPreview(towerType);
                button.setText(name + "\nCost: $" + cost + "\n(Press ESC to cancel)");
                
                // Wait for tower placement or cancellation
                javafx.animation.AnimationTimer previewWaiter = new javafx.animation.AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        if (!gameManager.isPreviewActive()) {
                            // Preview was cancelled or tower was placed
                            button.setText(name + "\nCost: $" + cost);
                            button.setDisable(gameManager.getMoney() < cost);
                            stop();
                        }
                    }
                };
                previewWaiter.start();
            }
        });
        
        getChildren().add(button);
    }

    private void createGameInfo() {
        // Add speed control
        VBox speedControls = new VBox(5);
        speedControls.setStyle("-fx-padding: 10; -fx-border-color: white; -fx-border-width: 1;");
        
        Text speedTitle = new Text("Game Speed");
        speedTitle.setStyle("-fx-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
        
        javafx.scene.control.Slider speedSlider = new javafx.scene.control.Slider(0.1, 3.0, 1.0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.setPrefWidth(PANEL_WIDTH - 20);
        
        HBox speedDisplay = new HBox(5);
        Text currentSpeedLabel = new Text("Current: ");
        currentSpeedLabel.setStyle("-fx-fill: white;");
        Text currentSpeedValue = new Text("1.0x");
        currentSpeedValue.setStyle("-fx-fill: white;");
        speedDisplay.getChildren().addAll(currentSpeedLabel, currentSpeedValue);
        speedDisplay.setAlignment(javafx.geometry.Pos.CENTER);
        
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double roundedSpeed = Math.round(newVal.doubleValue() * 10) / 10.0; // Round to nearest 0.1
            currentSpeedValue.setText(String.format("%.1fx", roundedSpeed));
            gameManager.setGameSpeed(roundedSpeed);
        });
        
        speedControls.getChildren().addAll(speedTitle, speedSlider, speedDisplay);
        speedControls.setAlignment(javafx.geometry.Pos.CENTER);

        // Add game information display
        HBox healthBox = new HBox(5);
        Text healthLabel = new Text("Health: ");
        Text healthValue = new Text(String.valueOf(gameManager.getPlayerHealth()));
        healthBox.getChildren().addAll(healthLabel, healthValue);

        HBox moneyBox = new HBox(5);
        Text moneyLabel = new Text("Money: ");
        Text moneyValue = new Text(String.valueOf(gameManager.getMoney()));
        moneyBox.getChildren().addAll(moneyLabel, moneyValue);

        // Update health and money display continuously
        javafx.animation.AnimationTimer uiUpdater = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                healthValue.setText(String.valueOf(gameManager.getPlayerHealth()));
                moneyValue.setText(String.valueOf(gameManager.getMoney()));
            }
        };
        uiUpdater.start();

        // Create wave display
        HBox waveBox = new HBox(5);
        Text waveLabel = new Text("Wave: ");
        Text waveValue = new Text(String.valueOf(gameManager.getWaveNumber()));
        waveBox.getChildren().addAll(waveLabel, waveValue);
        waveBox.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        // Score display
        HBox scoreBox = new HBox(5);
        Text scoreLabel = new Text("Score: ");
        Text scoreValue = new Text(String.valueOf(gameManager.getScore()));
        scoreBox.getChildren().addAll(scoreLabel, scoreValue);
        
        // Update wave and score display continuously
        javafx.animation.AnimationTimer statsUpdater = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                waveValue.setText(String.valueOf(gameManager.getWaveNumber()));
                scoreValue.setText(String.valueOf(gameManager.getScore()));
            }
        };
        statsUpdater.start();

        // Add speed indicator
        HBox speedBox = new HBox(5);
        Text speedLabel = new Text("Current Speed: ");
        Text speedValue = new Text("1.0x");
        speedBox.setAlignment(javafx.geometry.Pos.CENTER);
        speedBox.getChildren().addAll(speedLabel, speedValue);
        
        // Update speed display when slider changes
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double roundedSpeed = Math.round(newVal.doubleValue() * 2) / 2.0;
            speedValue.setText(String.format("%.1fx", roundedSpeed));
        });

        getChildren().addAll(
            createSeparator(),
            waveBox,
            speedControls,
            speedBox,
            createSeparator(),
            healthBox,
            moneyBox,
            scoreBox
        );
    }

    private javafx.scene.control.Separator createSeparator() {
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
        separator.setMaxWidth(Double.MAX_VALUE);
        return separator;
    }
}