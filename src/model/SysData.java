package model;

import controller.HistoryManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SysData {

    private static final SysData INSTANCE = new SysData();

    private final List<String> playerScores;
    private final List<String> historyEntries; // For in-game action history
    private final List<GameHistory> gameHistory; // For completed games
    private final List<String> questions;
    private final HistoryManager historyManager;

    private SysData() {
        this.playerScores = new ArrayList<>();
        this.historyEntries = new ArrayList<>();
        this.gameHistory = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.historyManager = new HistoryManager();
        
        // Load history from CSV
        List<GameHistory> loadedHistory = historyManager.loadGameHistoryFromCSV();
        gameHistory.addAll(loadedHistory);
    }

    public static SysData getInstance() {
        return INSTANCE;
    }

    public List<String> getPlayerScores() {
        return Collections.unmodifiableList(playerScores);
    }

    public List<String> getHistoryEntries() {
        return Collections.unmodifiableList(historyEntries);
    }
    
    public List<GameHistory> getGameHistory() {
        return Collections.unmodifiableList(gameHistory);
    }

    public List<String> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public void addScore(String score) {
        playerScores.add(score);
    }

    public void addHistoryEntry(String entry) {
        historyEntries.add(entry);
    }
    
    public void addGameHistory(GameHistory history) {
        gameHistory.add(history);
        // Save to CSV whenever a new game is added
        historyManager.saveGameHistoryToCSV(gameHistory);
    }
    
    public void removeGameHistory(GameHistory history) {
        gameHistory.remove(history);
        // Save to CSV after removal
        historyManager.saveGameHistoryToCSV(gameHistory);
    }

    public void addQuestion(String question) {
        questions.add(question);
    }
}

