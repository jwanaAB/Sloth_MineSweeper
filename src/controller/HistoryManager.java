package controller;

import model.Game;
import model.GameHistory;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages game history persistence to and from CSV files.
 * Similar to QuestionLogic, handles file location detection for both IDE and JAR execution.
 */
public class HistoryManager {
    private static final String HISTORY_CSV_FILE = "GameHistory.csv";
    private File historyCsvFile;
    
    public HistoryManager() {
        this.historyCsvFile = getHistoryCsvFile();
    }
    
    /**
     * Gets the history CSV file path. When running from JAR, uses a file in the same directory as the JAR.
     * When running from IDE, uses GameHistory.csv in the project root.
     */
    private File getHistoryCsvFile() {
        try {
            // Try to get the JAR file location
            URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
            URI uri = location.toURI();
            File jarFile = new File(uri);
            
            // Check if running from JAR (path ends with .jar)
            String jarPath = jarFile.getAbsolutePath();
            if (jarPath != null && jarPath.toLowerCase().endsWith(".jar")) {
                File jarDir = jarFile.getParentFile();
                if (jarDir != null) {
                    // Use GameHistory.csv in the same directory as the JAR
                    return new File(jarDir, HISTORY_CSV_FILE);
                }
            }
        } catch (Exception e) {
            // Not running from JAR or couldn't determine JAR location
        }
        
        // Fallback: use GameHistory.csv in current directory (project root)
        return new File(HISTORY_CSV_FILE);
    }
    
    /**
     * Loads game history from CSV file.
     * 
     * @return List of GameHistory objects loaded from CSV
     */
    public List<GameHistory> loadGameHistoryFromCSV() {
        List<GameHistory> history = new ArrayList<>();
        
        if (!historyCsvFile.exists()) {
            return history; // No history file yet
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(historyCsvFile))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }
                
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                // Parse CSV line: Difficulty,Date,DurationSeconds,Player1Name,Player2Name,CombinedScore,RemainingHearts
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    try {
                        Game.Difficulty difficulty = parseDifficulty(parts[0].trim());
                        LocalDate date = LocalDate.parse(parts[1].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        long durationSeconds = Long.parseLong(parts[2].trim());
                        String player1Name = unescapeCsv(parts[3].trim());
                        String player2Name = unescapeCsv(parts[4].trim());
                        int combinedScore = Integer.parseInt(parts[5].trim());
                        int remainingHearts = Integer.parseInt(parts[6].trim());
                        
                        GameHistory gameHistory = new GameHistory(difficulty, date, durationSeconds, 
                                                                 player1Name, player2Name, combinedScore, remainingHearts);
                        history.add(gameHistory);
                    } catch (Exception e) {
                        System.err.println("Error parsing history line: " + line);
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading game history from CSV: " + e.getMessage());
            e.printStackTrace();
        }
        
        return history;
    }
    
    /**
     * Saves game history to CSV file.
     * 
     * @param history List of GameHistory objects to save
     */
    public void saveGameHistoryToCSV(List<GameHistory> history) {
        try {
            // Ensure parent directory exists
            File parentDir = historyCsvFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(historyCsvFile))) {
                // Write header
                writer.println("Difficulty,Date,DurationSeconds,Player1Name,Player2Name,CombinedScore,RemainingHearts");
                
                // Write each game history entry
                for (GameHistory gameHistory : history) {
                    writer.printf("%s,%s,%d,%s,%s,%d,%d%n",
                        gameHistory.getDifficulty().name(),
                        gameHistory.getFormattedDate(),
                        gameHistory.getDurationSeconds(),
                        escapeCsv(gameHistory.getPlayer1Name()),
                        escapeCsv(gameHistory.getPlayer2Name()),
                        gameHistory.getCombinedScore(),
                        gameHistory.getRemainingHearts()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving game history to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Escapes CSV special characters in a string.
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // If value contains comma, quote, or newline, wrap in quotes and escape quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Unescapes CSV special characters in a string.
     */
    private String unescapeCsv(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        // If value is quoted, remove quotes and unescape
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }
    
    /**
     * Parses difficulty string to Game.Difficulty enum.
     */
    private Game.Difficulty parseDifficulty(String difficultyStr) {
        try {
            return Game.Difficulty.valueOf(difficultyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Game.Difficulty.EASY; // Default
        }
    }
    
    /**
     * Creates demo games for testing/display purposes.
     * 
     * @return List of demo GameHistory objects
     */
    public List<GameHistory> createDemoGames() {
        List<GameHistory> demoGames = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // Demo game 1: Hard difficulty, recent date
        demoGames.add(new GameHistory(
            Game.Difficulty.HARD,
            today.minusDays(1),
            522, // 8:42 in seconds
            "Sarah",
            "Mike",
            143,
            1
        ));
        
        // Demo game 2: Medium difficulty, 2 days ago
        demoGames.add(new GameHistory(
            Game.Difficulty.MEDIUM,
            today.minusDays(2),
            375, // 6:15 in seconds
            "Emma",
            "Lucas",
            165,
            0
        ));
        
        // Demo game 3: Easy difficulty, 3 days ago
        demoGames.add(new GameHistory(
            Game.Difficulty.EASY,
            today.minusDays(3),
            240, // 4:00 in seconds
            "Alex",
            "Jordan",
            98,
            3
        ));
        
        // Demo game 4: Hard difficulty, 4 days ago
        demoGames.add(new GameHistory(
            Game.Difficulty.HARD,
            today.minusDays(4),
            680, // 11:20 in seconds
            "Chris",
            "Taylor",
            187,
            0
        ));
        
        // Demo game 5: Medium difficulty, 5 days ago
        demoGames.add(new GameHistory(
            Game.Difficulty.MEDIUM,
            today.minusDays(5),
            420, // 7:00 in seconds
            "Sam",
            "Riley",
            132,
            2
        ));
        
        // Demo game 6: Easy difficulty, 6 days ago
        demoGames.add(new GameHistory(
            Game.Difficulty.EASY,
            today.minusDays(6),
            180, // 3:00 in seconds
            "Morgan",
            "Casey",
            75,
            5
        ));
        
        // Demo game 7: Hard difficulty, 7 days ago
        demoGames.add(new GameHistory(
            Game.Difficulty.HARD,
            today.minusDays(7),
            600, // 10:00 in seconds
            "Drew",
            "Blake",
            201,
            0
        ));
        
        // Demo game 8: Medium difficulty, 8 days ago
        demoGames.add(new GameHistory(
            Game.Difficulty.MEDIUM,
            today.minusDays(8),
            300, // 5:00 in seconds
            "Quinn",
            "Avery",
            118,
            1
        ));
        
        return demoGames;
    }
}

