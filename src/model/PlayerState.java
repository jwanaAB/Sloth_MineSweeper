package model;

/**
 * Represents the state of a player during a game, including score and lives.
 */
public class PlayerState {
    private String playerName;
    private int score;
    private int lives;
    private int questions;
    private int surprises;

    public PlayerState(String playerName, int initialLives, int initialQuestions, int initialSurprises) {
        this.playerName = playerName;
        this.score = 0;
        this.lives = initialLives;
        this.questions = initialQuestions;
        this.surprises = initialSurprises;
    }

    // Constructor for backward compatibility
    public PlayerState(String playerName, int initialLives) {
        this(playerName, initialLives, 0, 0);
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives); // Lives cannot go below 0
    }

    public void loseLife() {
        this.lives = Math.max(0, this.lives - 1);
    }

    public void addLife() {
        this.lives++;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public int getQuestionsRemaining() {
        return questions;
    }

    public void useQuestion() {
        if (questions > 0)
            questions--;
    }

    public int getSurprisesRemaining() {
        return surprises;
    }

    public void useSurprise() {
        if (surprises > 0)
            surprises--;
    }

    @Override
    public String toString() {
        return "PlayerState{" +
                "playerName='" + playerName + '\'' +
                ", score=" + score +
                ", lives=" + lives +
                ", questions=" + questions +
                ", surprises=" + surprises +
                '}';
    }
}