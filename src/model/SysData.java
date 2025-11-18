package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SysData {

    private static final SysData INSTANCE = new SysData();

    private final List<String> playerScores;
    private final List<String> historyEntries;
    private final List<String> questions;

    private SysData() {
        this.playerScores = new ArrayList<>();
        this.historyEntries = new ArrayList<>();
        this.questions = new ArrayList<>();
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
}

