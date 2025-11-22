package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SysData {

    private static final SysData INSTANCE = new SysData();

    private final List<String> playerScores;
    private final List<String> historyEntries;
    private final List<String> questions;
    private String player1Name;
    private String player2Name;
    private int currentDifficulty;

    private SysData() {
        this.playerScores = new ArrayList<>();
        this.historyEntries = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.player1Name = null;
        this.player2Name = null;
        this.currentDifficulty = 1; // Default difficulty
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

    public List<String> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public void addScore(String score) {
        playerScores.add(score);
    }

    public void addHistoryEntry(String entry) {
        historyEntries.add(entry);
    }

    public void addQuestion(String question) {
        questions.add(question);
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public int getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(int currentDifficulty) {
        if (currentDifficulty >= 1 && currentDifficulty <= 3) {
            this.currentDifficulty = currentDifficulty;
        }
    }
}

