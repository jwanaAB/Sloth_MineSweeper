package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a game history entry containing all information about a completed game.
 */
public class GameHistory {
    private final Game.Difficulty difficulty;
    private final LocalDate date;
    private final long durationSeconds; // Game duration in seconds
    private final String player1Name;
    private final String player2Name;
    private final int combinedScore;
    private final int remainingHearts; // Remaining shared lives
    
    public GameHistory(Game.Difficulty difficulty, LocalDate date, long durationSeconds,
                       String player1Name, String player2Name, int combinedScore, int remainingHearts) {
        this.difficulty = difficulty;
        this.date = date;
        this.durationSeconds = durationSeconds;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.combinedScore = combinedScore;
        this.remainingHearts = remainingHearts;
    }
    
    public Game.Difficulty getDifficulty() {
        return difficulty;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    public long getDurationSeconds() {
        return durationSeconds;
    }
    
    public String getFormattedDuration() {
        long minutes = durationSeconds / 60;
        long seconds = durationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    public String getPlayer1Name() {
        return player1Name;
    }
    
    public String getPlayer2Name() {
        return player2Name;
    }
    
    public int getCombinedScore() {
        return combinedScore;
    }
    
    public int getRemainingHearts() {
        return remainingHearts;
    }
}

