package com.example.btd.game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class LeaderboardManager {
    private static final String DB_URL = "jdbc:sqlite:leaderboard.db";
    private List<LeaderboardEntry> cachedScores;

    public LeaderboardManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            // Create table if it doesn't exist
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS leaderboard (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_name TEXT," +
                "score INTEGER," +
                "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addScore(String playerName, int score) {
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Unknown Player";
        }
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO leaderboard (player_name, score) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playerName.trim());
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LeaderboardEntry> getTopScores(int limit) {
        List<LeaderboardEntry> scores = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT player_name, score, date FROM leaderboard ORDER BY score DESC LIMIT ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                scores.add(new LeaderboardEntry(
                    rs.getString("player_name"),
                    rs.getInt("score"),
                    rs.getTimestamp("date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scores;
    }

    public void saveScores() {
        // Already saved in database automatically
    }

    public void loadScores() {
        cachedScores = getTopScores(10);
        displayLeaderboard();
    }

    private void displayLeaderboard() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Leaderboard");
        alert.setHeaderText("Top 10 Scores");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefRowCount(12);

        StringBuilder content = new StringBuilder();
        content.append(String.format("%-20s %-10s %-20s\n", "Player", "Score", "Date"));
        content.append("-".repeat(50)).append("\n");

        for (LeaderboardEntry entry : cachedScores) {
            content.append(String.format("%-20s %-10d %-20s\n",
                entry.getPlayerName(),
                entry.getScore(),
                entry.getDate().toString()
            ));
        }

        textArea.setText(content.toString());
        
        VBox dialogContent = new VBox(10);
        dialogContent.getChildren().add(textArea);
        alert.getDialogPane().setContent(dialogContent);
        
        alert.showAndWait();
    }
}