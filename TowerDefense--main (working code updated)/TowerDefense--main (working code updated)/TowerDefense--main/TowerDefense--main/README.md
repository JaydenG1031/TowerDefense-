# Tower Defense Game

A JavaFX-based tower defense game project created for CSC-2592-98533, where you defend against waves of enemies using different types of towers.

## Features

- Multiple tower types with different abilities
- Advanced enemy system with special properties (Camo, Armored, Regenerating)
- Wave-based progression with increasing difficulty
- Interactive tower placement and management
- Score tracking with leaderboard
- Visual effects and animations
- Local SQLite database for high scores

## Requirements

- Java 17 or higher
- JavaFX
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

- Left-click on towers to view their range
- Right-click on towers to sell them (50% refund)
- Place towers by selecting them from the tower panel
- Monitor your health, money, and score in the game UI

## Tower Types

1. Basic Tower
   - Cost: 50
   - Balanced range and damage
   - Medium attack speed

2. Sniper Tower
   - Cost: 100
   - Long range and high damage
   - Can detect camo enemies
   - Slow attack speed

3. Machine Gun Tower
   - Cost: 150
   - Short range and low damage
   - Very fast attack speed

## Enemy Types

- Normal: Basic enemies
- Camo: Can only be detected by certain towers
- Armored: Takes reduced damage
- Regenerating: Heals over time
- Boss: Appears every 10 waves with multiple special properties

## Game Rules

- Defend against increasingly difficult waves of enemies
- Don't let enemies reach the end of the path
- Earn money by defeating enemies
- Place towers strategically to handle different enemy types
- Try to achieve the highest score possible!
