package model;

/**
 * Represents the state of a player during a game, including score and lives.
 */
public class PlayerState {
    private String playerName;
    private int score;
    private int lives;
    private int hints;
    private int solutions;

    public PlayerState(String playerName, int initialLives, int initialHints, int initialSolutions) {
        this.playerName = playerName;
        this.score = 0;
        this.lives = initialLives;
        this.hints = initialHints;
        this.solutions = initialSolutions;
    }

    // Constructor for backward compatibility if needed, or just remove if not used
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

    public int getHintsRemaining() {
        return hints;
    }

    public void useHint() {
        if (hints > 0)
            hints--;
    }

    public int getSolutionsRemaining() {
        return solutions;
    }

    public void useSolution() {
        if (solutions > 0)
            solutions--;
    }

    @Override
    public String toString() {
        return "PlayerState{" +
                "playerName='" + playerName + '\'' +
                ", score=" + score +
                ", lives=" + lives +
                ", hints=" + hints +
                ", solutions=" + solutions +
                '}';
    }
}