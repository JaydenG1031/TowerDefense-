# Balloon Tower Defense Game

A JavaFX-based tower defense game where you defend against waves of balloons using different types of towers.

## Features

- Multiple tower types with different abilities
- Wave-based enemy system
- Interactive tower placement
- Score tracking with leaderboard
- Local SQLite database for high scores

## Prerequisites

- Java 17 or higher
- Maven

## How to Run

1. Clone this repository
2. Navigate to the project directory
3. Run the following commands:

```bash
mvn clean install
mvn javafx:run
```

## Game Controls

- Left-click on a tower button to select it
- Left-click on the game area to place the selected tower
- Monitor your health, money, and score in the right panel

## Tower Types

1. Basic Tower
   - Cost: 100
   - Medium range and damage
   - Balanced attack speed

2. Sniper Tower
   - Cost: 200
   - Long range and high damage
   - Slow attack speed

3. Rapid Tower
   - Cost: 300
   - Short range and low damage
   - Very fast attack speed

## Game Rules

- Defend against waves of enemies
- Don't let enemies reach the end of the path
- Earn money by defeating enemies
- Place towers strategically to maximize effectiveness
- Try to achieve the highest score possible!