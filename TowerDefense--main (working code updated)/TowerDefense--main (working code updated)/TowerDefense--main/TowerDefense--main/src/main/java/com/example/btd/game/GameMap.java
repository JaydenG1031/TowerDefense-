package com.example.btd.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private Image backgroundImage;
    private List<PathPoint> path;
    private boolean[][] towerPlacementGrid;
    private static final int GRID_SIZE = 20; // Size of each grid cell
    private double mapWidth;
    private double mapHeight;

    public GameMap(double mapWidth, double mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        backgroundImage = new Image(getClass().getResourceAsStream("/images/map_background.png"));
        initializePath();
        initializeTowerGrid();
    }

    private void initializePath() {
        path = new ArrayList<>();
        // Define a more interesting path with curves
        path.add(new PathPoint(0, mapHeight * 0.2)); // Start at left side, upper portion
        path.add(new PathPoint(mapWidth * 0.2, mapHeight * 0.2)); // Right
        path.add(new PathPoint(mapWidth * 0.2, mapHeight * 0.8)); // Down
        path.add(new PathPoint(mapWidth * 0.4, mapHeight * 0.8)); // Right
        path.add(new PathPoint(mapWidth * 0.4, mapHeight * 0.4)); // Up
        path.add(new PathPoint(mapWidth * 0.6, mapHeight * 0.4)); // Right
        path.add(new PathPoint(mapWidth * 0.6, mapHeight * 0.6)); // Down
        path.add(new PathPoint(mapWidth * 0.8, mapHeight * 0.6)); // Right
        path.add(new PathPoint(mapWidth * 0.8, mapHeight * 0.2)); // Up
        path.add(new PathPoint(mapWidth, mapHeight * 0.2)); // Exit at right side
    }

    private void initializeTowerGrid() {
    int gridWidth = (int) (mapWidth / GRID_SIZE);
    int gridHeight = (int) (mapHeight / GRID_SIZE);
    towerPlacementGrid = new boolean[gridWidth][gridHeight];

        // Mark path cells and their immediate neighbors as invalid for tower placement
        for (PathPoint point : path) {
            int gridX = (int) (point.x / GRID_SIZE);
            int gridY = (int) (point.y / GRID_SIZE);
            markAreaInvalid(gridX, gridY, 1);
        }
    }

    private void markAreaInvalid(int centerX, int centerY, int radius) {
        if (centerX >= 0 && centerX < towerPlacementGrid.length && 
            centerY >= 0 && centerY < towerPlacementGrid[0].length) {
            towerPlacementGrid[centerX][centerY] = true;
        }
    }

    public void render(GraphicsContext gc) {
    // Draw background
    gc.drawImage(backgroundImage, 0, 0, mapWidth, mapHeight);
        
        // Draw path background
        gc.setStroke(javafx.scene.paint.Color.BROWN);
        double pathWidth = Math.min(mapWidth, mapHeight) * 0.05; // Scale path width with map size
        gc.setLineWidth(pathWidth);
        for (int i = 0; i < path.size() - 1; i++) {
            PathPoint current = path.get(i);
            PathPoint next = path.get(i + 1);
            gc.strokeLine(current.x, current.y, next.x, next.y);
        }
        
        // Draw path border
        gc.setStroke(javafx.scene.paint.Color.YELLOW);
        gc.setLineWidth(pathWidth * 0.1); // Border width is 10% of path width
        for (int i = 0; i < path.size() - 1; i++) {
            PathPoint current = path.get(i);
            PathPoint next = path.get(i + 1);
            gc.strokeLine(current.x, current.y, next.x, next.y);
        }
    }

    public boolean canPlaceTower(double x, double y) {
        // Check if too close to any path segment
        for (int i = 0; i < path.size() - 1; i++) {
            PathPoint start = path.get(i);
            PathPoint end = path.get(i + 1);
            // Calculate distance from point to line segment
            double distance = distanceToLineSegment(x, y, start.x, start.y, end.x, end.y);
            if (distance < Math.min(mapWidth, mapHeight) * 0.05) { // Minimum distance scales with map size
                return false;
            }
        }
        // Check if within map bounds
        return x >= 0 && x <= mapWidth && y >= 0 && y <= mapHeight;
    }

    private double distanceToLineSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;

        if (len_sq != 0) {
            param = dot / len_sq;
        }

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public List<PathPoint> getPath() {
        return path;
    }

    public double getWidth() {
        return mapWidth;
    }

    public double getHeight() {
        return mapHeight;
    }
}