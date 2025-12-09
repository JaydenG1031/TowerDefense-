package com.example.btd.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Projectile {
    public double x, y; // Make these public for easy access from GameManager
    public double damage;
    public double size;
    private double targetX, targetY;
    private double speed;
    private boolean active;
    private Color color;
    private static final double PROJECTILE_SPEED = 300; // pixels per second
    
    public Projectile(double startX, double startY, double targetX, double targetY, 
                     double damage, Color color, double size) {
        this.x = startX;
        this.y = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.damage = damage;
        this.color = color;
        this.size = size;
        this.active = true;
        this.speed = PROJECTILE_SPEED;
    }
    
    public void update(double deltaTime) {
        if (!active) return;
        
        // Calculate direction
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Normalize direction and apply speed
        double dirX = dx / distance;
        double dirY = dy / distance;
        
        // Update position
        x += dirX * speed * deltaTime;
        y += dirY * speed * deltaTime;
        
        // Check if projectile reached target
        double currentDx = targetX - x;
        double currentDy = targetY - y;
        double currentDistance = Math.sqrt(currentDx * currentDx + currentDy * currentDy);
        
        if (currentDistance < 5) {
            active = false;
        }
    }
    
    public void render(GraphicsContext gc) {
        if (!active) return;
        
        gc.setFill(color);
        gc.fillOval(x - size/2, y - size/2, size, size);
        
        // Draw trail
        gc.setGlobalAlpha(0.3);
        gc.fillOval(x - size*1.5, y - size*1.5, size*3, size*3);
        gc.setGlobalAlpha(1.0);
    }
    
    public boolean isActive() {
        return active;
    }
}