package com.example.btd.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.List;
import java.util.ArrayList;

public class Tower {
    private double x, y;
    private int cost;
    private double range;
    private double damage;
    private double attackSpeed;
    private double lastAttackTime;
    private Image sprite;
    private String towerType;
    private double towerSize; // Dynamic size for towers
    private static final double TOWER_SIZE_RATIO = 0.05; // Tower size as percentage of map height
    private static final double RANGE_RATIO = 0.2; // Range as percentage of map size
    private List<Projectile> projectiles;
    private javafx.scene.paint.Color projectileColor;
    private Enemy currentTarget;
    private boolean canSeeCamo; // Whether the tower can detect camo enemies
    private boolean isSelected; // Whether the tower is currently selected

    public Tower(double x, double y, String type, double mapWidth, double mapHeight) {
        this.x = x;
        this.y = y;
        this.towerType = type;
        this.towerSize = mapHeight * TOWER_SIZE_RATIO; // Tower size scales with map height
        
        // Set tower stats based on type
        switch(type.toLowerCase()) {
            case "sniper":
                this.cost = 100;
                this.range = 2.0 * Math.min(mapWidth, mapHeight) * RANGE_RATIO;
                this.damage = 50;
                this.attackSpeed = 0.5;
                this.projectileColor = javafx.scene.paint.Color.RED;
                String imagePath = this.getClass().getClassLoader().getResource("images/sniper_tower.png").toString();
                this.sprite = new Image(imagePath);
                this.canSeeCamo = true; // Sniper towers can detect camo
                break;
            case "machine":
                this.cost = 150;
                this.range = 1.0 * Math.min(mapWidth, mapHeight) * RANGE_RATIO;
                this.damage = 10;
                this.attackSpeed = 3.0;
                this.projectileColor = javafx.scene.paint.Color.LIGHTGREEN;
                imagePath = this.getClass().getClassLoader().getResource("images/rapid_tower.png").toString();
                this.sprite = new Image(imagePath);
                this.canSeeCamo = false; // Machine gun towers can't detect camo
                break;
            default: // Basic tower
                this.cost = 50;
                this.range = 1.5 * Math.min(mapWidth, mapHeight) * RANGE_RATIO;
                this.damage = 20;
                this.attackSpeed = 1.0;
                imagePath = this.getClass().getClassLoader().getResource("images/basic_tower.png").toString();
                this.sprite = new Image(imagePath);
                this.projectileColor = javafx.scene.paint.Color.CYAN;
                this.canSeeCamo = false; // Basic towers can't detect camo
                break;
        }
        
        this.lastAttackTime = 0;
        this.projectiles = new ArrayList<>();
        
        // Set projectile color based on tower type
        if (attackSpeed >= 2.0) {
            projectileColor = javafx.scene.paint.Color.LIGHTGREEN; // Rapid fire tower
        } else if (damage >= 30) {
            projectileColor = javafx.scene.paint.Color.RED; // High damage tower
        } else {
            projectileColor = javafx.scene.paint.Color.CYAN; // Basic tower
        }
        
        try {
            String spritePath = "/images/towers/" + towerType.toLowerCase() + ".png";
            this.sprite = new Image(getClass().getResourceAsStream(spritePath));
            if (this.sprite == null || this.sprite.isError()) {
                throw new Exception("Failed to load tower sprite");
            }
        } catch (Exception e) {
            System.err.println("Error loading tower sprite: " + e.getMessage());
            // Create a default colored rectangle as sprite based on tower type
            javafx.scene.canvas.Canvas tempCanvas = new javafx.scene.canvas.Canvas(towerSize, towerSize);
            javafx.scene.canvas.GraphicsContext gc = tempCanvas.getGraphicsContext2D();
            
            // Set color based on tower type (determined by stats)
            if (attackSpeed >= 2.0) {
                // Rapid Tower - Green
                gc.setFill(javafx.scene.paint.Color.GREEN);
            } else if (damage >= 30) {
                // Sniper Tower - Red
                gc.setFill(javafx.scene.paint.Color.RED);
            } else {
                // Basic Tower - Blue
                gc.setFill(javafx.scene.paint.Color.BLUE);
            }
            
            gc.fillRect(0, 0, towerSize, towerSize);
            gc.setStroke(javafx.scene.paint.Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRect(0, 0, towerSize, towerSize);
            this.sprite = tempCanvas.snapshot(null, null);
        }
    }

    public Enemy getTarget(List<Enemy> enemies) {
        if (currentTarget != null && !currentTarget.isDead() && !currentTarget.hasReachedEnd() &&
            calculateDistance(currentTarget) <= range && (canSeeCamo || !currentTarget.isCamo())) {
            return currentTarget;
        }
        
        Enemy closest = null;
        double closestDistance = range;

        for (Enemy enemy : enemies) {
            if (!enemy.isDead() && (canSeeCamo || !enemy.isCamo())) {
                double distance = calculateDistance(enemy);
                if (distance <= range && (closest == null || distance < closestDistance)) {
                    closest = enemy;
                    closestDistance = distance;
                }
            }
        }

        currentTarget = closest;
        return closest;
    }

    private double calculateDistance(Enemy enemy) {
        double dx = x - enemy.getX();
        double dy = y - enemy.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean isReadyToShoot() {
        return lastAttackTime >= 1.0 / attackSpeed;
    }

    public Projectile createProjectile(Enemy target) {
        lastAttackTime = 0;
        return new Projectile(
            x, y,  // start position (tower center)
            target.getX(), target.getY(),  // target position
            damage,  // projectile damage
            projectileColor,  // color based on tower type
            towerSize * 0.2  // projectile size
        );
    }
    
    public void update(double deltaTime, List<Enemy> enemies) {
        lastAttackTime += deltaTime;
    }

    public void render(GraphicsContext gc) {
        // Draw range circle only when selected
        if (isSelected) {
            gc.setLineWidth(2);
            gc.setStroke(javafx.scene.paint.Color.GREY); // Grey outline
            gc.strokeOval(x - range, y - range, range * 2, range * 2);
        }
        
        // Draw all active projectiles
        projectiles.forEach(projectile -> projectile.render(gc));
        
        // Draw tower sprite
        gc.drawImage(sprite, x - towerSize / 2, y - towerSize / 2, towerSize, towerSize);
    }
    
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    
    public boolean isSelected() {
        return isSelected;
    }

    // Getters
    public int getCost() { return cost; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return towerSize; }
    public double getRange() { return range; }

    public void checkCollisions(List<Enemy> enemies) {
        // Implement projectile collision if needed
    }
}