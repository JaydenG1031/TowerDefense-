package com.example.btd.game;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager {
    private final Canvas gameCanvas;
    private final GraphicsContext gc;
    private final GameMap gameMap;
    private final List<PathPoint> path;
    private final List<Tower> towers;
    private final List<Enemy> enemies;
    private final List<Projectile> projectiles;
    private final LeaderboardManager leaderboardManager;
    private final String playerName;

    private AnimationTimer gameLoop;
    private long lastUpdateTime;
    private double deltaTime;
    private int money;
    private int lives;
    private int score;
    private int spawnCounter;
    private boolean gameOver;
    private double gameSpeed;
    private int currentWave;
    private int enemiesRemainingInWave;
    private int totalEnemiesSpawned;
    private boolean waveInProgress;
    private int enemiesPerWave;

    private static final int STARTING_MONEY = 200; // Enough for 1 advanced tower or multiple basic towers
    private static final int STARTING_LIVES = 50; // More forgiving number of lives
    private static final int ENEMY_SPAWN_DELAY = 60;
    private static final int INITIAL_ENEMIES_PER_WAVE = 5; // Start with 5 enemies in wave 1
    private static final int ENEMIES_INCREASE_PER_WAVE = 3; // Add 3 more enemies each wave
    private static final int WAVE_BREAK_TIME = 300; // Time between waves (in frames)

    public GameManager(Canvas gameCanvas, String playerName) {
        this.gameCanvas = gameCanvas;
        this.gc = gameCanvas.getGraphicsContext2D();
        this.playerName = playerName;
        this.gameMap = new GameMap(gameCanvas.getWidth(), gameCanvas.getHeight());
        this.path = new ArrayList<>();
        this.towers = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.leaderboardManager = new LeaderboardManager();
        
        initializeGame();
        initializePath();
        startGameLoop();
    }

    private void initializeGame() {
        money = STARTING_MONEY;
        lives = STARTING_LIVES;
        score = 0;
        spawnCounter = 0;
        gameOver = false;
        lastUpdateTime = 0;
        gameSpeed = 1.0; // Default game speed
        currentWave = 1;
        startNewWave();
    }

    private void startNewWave() {
        enemiesPerWave = INITIAL_ENEMIES_PER_WAVE + (currentWave - 1) * ENEMIES_INCREASE_PER_WAVE;
        enemiesRemainingInWave = enemiesPerWave;
        totalEnemiesSpawned = 0;
        waveInProgress = true;
        spawnCounter = 0;
    }

    private void initializePath() {
        // Get the path from GameMap
        path.addAll(gameMap.getPath());
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateTime == 0) {
                    lastUpdateTime = now;
                    return;
                }
                deltaTime = (now - lastUpdateTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
                deltaTime *= gameSpeed; // Apply game speed multiplier
                lastUpdateTime = now;
                update(deltaTime);
                render();
            }
        };
        gameLoop.start();
    }

    private void update(double deltaTime) {
        if (gameOver) return;

        // Handle wave progression
        if (waveInProgress) {
            spawnCounter++;
            if (spawnCounter >= ENEMY_SPAWN_DELAY && totalEnemiesSpawned < enemiesPerWave) {
                spawnEnemy();
                spawnCounter = 0;
                totalEnemiesSpawned++;
            }
        } else {
            spawnCounter++;
            if (spawnCounter >= WAVE_BREAK_TIME && enemies.isEmpty()) {
                currentWave++;
                startNewWave();
            }
        }

        // Check if wave is complete
        if (waveInProgress && totalEnemiesSpawned >= enemiesPerWave && enemies.isEmpty()) {
            waveInProgress = false;
            spawnCounter = 0;
        }

        // Update enemies
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update(deltaTime);
            if (enemy.hasReachedEnd()) {
                lives--;
                enemyIterator.remove();
                if (lives <= 0) {
                    endGame();
                }
            } else if (enemy.isDead()) {
                money += enemy.getReward();
                score += enemy.getReward();
                enemyIterator.remove();
            }
        }

        // Update towers and handle shooting
        for (Tower tower : towers) {
            tower.update(deltaTime, enemies);
            Enemy target = tower.getTarget(enemies);
            if (target != null && tower.isReadyToShoot()) {
                Projectile projectile = tower.createProjectile(target);
                if (projectile != null) {
                    projectiles.add(projectile);
                }
            }
        }

        // Update projectiles and check for collisions
        Iterator<Projectile> projectileIterator = projectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            projectile.update(deltaTime);

            // Check if the projectile hits any enemy
            for (Enemy enemy : enemies) {
                if (isCollision(projectile, enemy)) {
                    enemy.takeDamage(projectile.damage);
                    projectileIterator.remove();
                    break;
                }
            }

            // Remove projectiles that are out of bounds
            if (!isProjectileInBounds(projectile)) {
                projectileIterator.remove();
            }
        }
    }

    private boolean isCollision(Projectile projectile, Enemy enemy) {
        double dx = projectile.x - enemy.getX();
        double dy = projectile.y - enemy.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (projectile.size + enemy.getSize()) / 2;
    }

    private boolean isProjectileInBounds(Projectile projectile) {
        return projectile.x >= 0 && projectile.x <= gameCanvas.getWidth() &&
               projectile.y >= 0 && projectile.y <= gameCanvas.getHeight();
    }

    private void render() {
        // Clear the canvas
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw the map
        gameMap.render(gc);

        // Draw path points for debugging
        for (PathPoint point : path) {
            gc.setFill(Color.RED);
            gc.fillOval(point.x - 2, point.y - 2, 4, 4);
        }

        // Draw towers
        for (Tower tower : towers) {
            tower.render(gc);
        }

        // Draw enemies
        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }

        // Draw projectiles
        for (Projectile projectile : projectiles) {
            projectile.render(gc);
        }

        // Draw game stats
        gc.setFill(Color.BLACK);
        gc.fillText("Money: $" + money, 10, 20);
        gc.fillText("Lives: " + lives, 10, 40);
        gc.fillText("Score: " + score, 10, 60);
        gc.fillText("Wave: " + currentWave, 10, 80);
        
        if (!waveInProgress && enemies.isEmpty()) {
            gc.setFill(Color.GREEN);
            gc.fillText("Wave " + (currentWave + 1) + " starts in " + 
                       ((WAVE_BREAK_TIME - spawnCounter) / 60 + 1) + " seconds", 10, 100);
        } else {
            gc.fillText("Enemies: " + enemies.size() + "/" + enemiesPerWave, 10, 100);
        }
    }

    public void placeTower(double x, double y, String type) {
        int cost = getTowerCost(type);
        if (money >= cost) {
            Tower tower = createTower(x, y, type);
            if (isValidTowerPlacement(tower)) {
                towers.add(tower);
                money -= cost;
            }
        }
    }

    public boolean deleteTower(double x, double y) {
        Tower towerToRemove = null;
        for (Tower tower : towers) {
            double dx = tower.getX() - x;
            double dy = tower.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < tower.getSize()) {
                towerToRemove = tower;
                break;
            }
        }
        
        if (towerToRemove != null) {
            towers.remove(towerToRemove);
            // Refund 50% of the tower's cost
            money += (int)(towerToRemove.getCost() * 0.5);
            return true;
        }
        return false;
    }

    private Tower createTower(double x, double y, String type) {
        return new Tower(x, y, type, gameCanvas.getWidth(), gameCanvas.getHeight());
    }

    private int getTowerCost(String type) {
        switch(type.toLowerCase()) {
            case "basic": return 50;
            case "sniper": return 100;
            case "machine": return 150;
            default: return 999999; // Very high cost for invalid types
        }
    }

    public void selectTowerAt(double x, double y) {
        // Deselect all towers first
        for (Tower tower : towers) {
            tower.setSelected(false);
        }
        
        // Find and select the clicked tower
        for (Tower tower : towers) {
            double dx = tower.getX() - x;
            double dy = tower.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < tower.getSize()) {
                tower.setSelected(true);
                return;
            }
        }
    }

    private boolean isValidTowerPlacement(Tower tower) {
        // Check if the tower is on the path
        for (PathPoint point : path) {
            double dx = tower.getX() - point.x;
            double dy = tower.getY() - point.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < tower.getSize()) {
                return false;
            }
        }
        
        // Check if the tower overlaps with other towers
        for (Tower existingTower : towers) {
            double dx = tower.getX() - existingTower.getX();
            double dy = tower.getY() - existingTower.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < (tower.getSize() + existingTower.getSize())) {
                return false;
            }
        }
        
        return true;
    }

    private void spawnEnemy() {
        if (!path.isEmpty()) {
            // Scale speed based on map size - this makes speed consistent across different screen sizes
            double mapSizeScale = Math.min(gameCanvas.getWidth(), gameCanvas.getHeight()) / 1000.0; // baseline for 1000px
            double baseSpeed = 100.0; // base speed in pixels per second
            double scaledSpeed = baseSpeed * mapSizeScale;
            
            // Scale enemy health and reward based on wave number
            double healthScaling = 100 * Math.pow(1.2, currentWave - 1); // Health increases by 20% each wave
            int rewardScaling = (int)(10 * Math.pow(1.1, currentWave - 1)); // Reward increases by 10% each wave
            
            Enemy enemy = new Enemy(path, healthScaling, scaledSpeed, rewardScaling, gameCanvas.getWidth(), gameCanvas.getHeight());
            
            // Randomly assign special properties based on wave number, with increasing probability
            double camoChance = Math.min(0.4, (currentWave - 5) * 0.05); // Max 40% chance
            double armorChance = Math.min(0.5, (currentWave - 10) * 0.07); // Max 50% chance
            double regenChance = Math.min(0.3, (currentWave - 15) * 0.04); // Max 30% chance
            
            // Track if enemy has any special property
            boolean hasSpecialProperty = false;
            
            // Only apply special properties after certain waves
            if (currentWave >= 5 && Math.random() < camoChance) {
                enemy.setCamo(true);
                healthScaling *= 0.8; // Camo enemies have less health
                rewardScaling = (int)(rewardScaling * 1.5); // but give more reward
                hasSpecialProperty = true;
            }
            
            if (currentWave >= 10 && Math.random() < armorChance && !hasSpecialProperty) {
                enemy.setArmored(true);
                healthScaling *= 1.5; // Armored enemies have more health
                rewardScaling = (int)(rewardScaling * 1.2); // and give slightly more reward
                hasSpecialProperty = true;
            }
            
            if (currentWave >= 15 && Math.random() < regenChance && !hasSpecialProperty) {
                enemy.setRegenerating(true);
                healthScaling *= 1.2; // Regenerating enemies have slightly more health
                rewardScaling = (int)(rewardScaling * 1.3); // and give more reward
            }
            
            // Create boss enemies at milestone waves
            if (currentWave % 10 == 0) { // Every 10th wave is a boss wave
                enemy.setArmored(true);
                enemy.setRegenerating(true);
                if (currentWave >= 20) { // Super boss at wave 20+
                    enemy.setCamo(true);
                }
                healthScaling *= 3.0; // Boss enemies have much more health
                rewardScaling *= 3; // and give much more reward
            }
            
            // Update enemy properties with the modified values
            enemy.setHealth(healthScaling);
            enemy.setReward(rewardScaling);
            
            enemies.add(enemy);
        }
    }

    private void endGame() {
        gameOver = true;
        gameLoop.stop();
        leaderboardManager.addScore(playerName, score);
        leaderboardManager.saveScores();
    }

    public void showLeaderboard() {
        leaderboardManager.loadScores();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public int getScore() {
        return score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setGameSpeed(double speed) {
        this.gameSpeed = Math.max(0.1, Math.min(3.0, speed)); // Clamp between 0.1x and 3.0x
    }

    public double getGameSpeed() {
        return gameSpeed;
    }

    public int getMoney() {
        return money;
    }

    public int getPlayerHealth() {
        return lives;
    }

    public int getWaveNumber() {
        return currentWave;
    }
}
