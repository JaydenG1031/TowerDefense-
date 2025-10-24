package com.example.btd.game;

public class LeaderboardEntry {
    private String playerName;
    private int score;
    private java.sql.Timestamp date;

    public LeaderboardEntry(String playerName, int score, java.sql.Timestamp date) {
        this.playerName = playerName;
        this.score = score;
        this.date = date;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public java.sql.Timestamp getDate() { return date; }
}