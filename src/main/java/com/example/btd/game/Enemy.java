package com.example.btd.game;

import javafx.scene.canvas.GraphicsContext;
import java.util.List;

public class Enemy {
    private double x, y;
    private double speed;
    private double health;
    private double maxHealth;
    private List<PathPoint> path;
    private int currentPathIndex;
    private boolean isDead;
    private int reward;
    private double enemySize;
    private javafx.scene.paint.Color color;
    private String type;
    private boolean isArmored; // Takes reduced damage
    private boolean isCamo; // Some towers might not see it
    private boolean isRegenerating; // Heals over time
    private boolean isPowerUp; // Gives buff to machine towers when defeated
    private static final double ENEMY_SIZE_RATIO = 0.03; // Enemy size as percentage of map height
    private static final double REGEN_RATE = 5.0; // Health regenerated per second
    private static final double ARMOR_DAMAGE_REDUCTION = 0.5; // Armored enemies take 50% less damage

    public Enemy(List<PathPoint> path, double health, double speed, int reward, double mapWidth, double mapHeight) {
        this.path = path;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.reward = reward;
        this.currentPathIndex = 0;
        this.isDead = false;
        this.enemySize = mapHeight * ENEMY_SIZE_RATIO;
        this.type = "NORMAL";
        this.color = javafx.scene.paint.Color.RED;
        this.isArmored = false;
        this.isCamo = false;
        this.isRegenerating = false;
        this.isPowerUp = false;
        
        if (!path.isEmpty()) {
            PathPoint start = path.get(0);
            this.x = start.x;
            this.y = start.y;
        }
    }
    
    public void setHealth(double health) {
        this.health = health;
        this.maxHealth = health;
    }
    
    public void setReward(int reward) {
        this.reward = reward;
    }
    
    public void setArmored(boolean armored) {
        this.isArmored = armored;
        if (armored) {
            this.color = javafx.scene.paint.Color.GRAY;
            this.type = this.type.equals("NORMAL") ? "ARMORED" : this.type;
        }
    }
    
    public void setCamo(boolean camo) {
        this.isCamo = camo;
        if (camo) {
            this.color = javafx.scene.paint.Color.LIGHTGREEN;
            this.type = this.type.equals("NORMAL") ? "CAMO" : this.type;
        }
    }
    
    public void setRegenerating(boolean regenerating) {
        this.isRegenerating = regenerating;
        if (regenerating) {
            this.color = javafx.scene.paint.Color.PINK;
            this.type = this.type.equals("NORMAL") ? "REGENERATING" : this.type;
        }
    }

    public void setPowerUp(boolean powerUp) {
        this.isPowerUp = powerUp;
        if (powerUp) {
            this.color = javafx.scene.paint.Color.GOLD;
            this.type = this.type.equals("NORMAL") ? "POWER-UP" : this.type;
        }
    }

    public boolean isPowerUp() {
        return isPowerUp;
    }

    public void update(double deltaTime) {
        if (isDead || currentPathIndex >= path.size() - 1) return;
        
        // Handle regeneration
        if (isRegenerating && health < maxHealth) {
            health = Math.min(maxHealth, health + REGEN_RATE * deltaTime);
        }
        
        PathPoint targetPoint = path.get(currentPathIndex + 1);
        double dx = targetPoint.x - x;
        double dy = targetPoint.y - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Move to next point if we're close enough
        double moveDistance = speed * deltaTime;
        if (distance <= moveDistance) {
            x = targetPoint.x;
            y = targetPoint.y;
            currentPathIndex++;
        } else {
            // Move towards the target point
            double directionX = dx / distance;
            double directionY = dy / distance;
            x += directionX * moveDistance;
            y += directionY * moveDistance;
        }
    }

    public void render(GraphicsContext gc) {
        if (isDead) return;
        
        // Draw the enemy as a colored circle
        gc.setFill(color);
        gc.fillOval(x - enemySize / 2, y - enemySize / 2, enemySize, enemySize);

        // Highlight power-up enemies with a gold star outline
        if (isPowerUp) {
            gc.setStroke(javafx.scene.paint.Color.GOLD);
            gc.setLineWidth(2);
            double s = enemySize * 0.6;
            // simple diamond/star-like marker
            gc.strokePolygon(
                new double[]{x, x + s/2, x, x - s/2},
                new double[]{y - s/2, y, y + s/2, y},
                4
            );
        }
        
        // Draw patterns for special types
        if (isArmored) {
            gc.setStroke(javafx.scene.paint.Color.SILVER);
            gc.setLineWidth(2);
            gc.strokeOval(x - enemySize / 2, y - enemySize / 2, enemySize, enemySize);
        }
        if (isCamo) {
            gc.setStroke(javafx.scene.paint.Color.DARKGREEN);
            gc.setLineWidth(1);
            gc.strokeLine(x - enemySize/2, y - enemySize/2, x + enemySize/2, y + enemySize/2);
            gc.strokeLine(x - enemySize/2, y + enemySize/2, x + enemySize/2, y - enemySize/2);
        }
        
        // Draw health bar
        double healthBarWidth = enemySize * 1.2; // Health bar slightly wider than enemy
        double healthPercent = health / maxHealth;
        
        // Draw health bar background (red)
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(x - healthBarWidth/2, y - enemySize/2 - 10,
                   healthBarWidth, 5);
        
        // Draw current health (color based on type)
        gc.setFill(isRegenerating ? javafx.scene.paint.Color.PINK : javafx.scene.paint.Color.GREEN);
        gc.fillRect(x - healthBarWidth/2, y - enemySize/2 - 10,
                   healthBarWidth * healthPercent, 5);
    }

    public void takeDamage(double damage) {
        // Apply armor damage reduction if applicable
        double actualDamage = isArmored ? damage * (1 - ARMOR_DAMAGE_REDUCTION) : damage;
        health -= actualDamage;
        if (health <= 0) {
            isDead = true;
        }
    }

    public boolean isCamo() {
        return isCamo;
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isDead() { return isDead; }
    public int getReward() { return reward; }
    public boolean hasReachedEnd() { return currentPathIndex >= path.size() - 1; }
    public double getSize() { return enemySize; }

}
