package model;

/**
 * Represents the state of a player during a game, including score and lives.
 */
public class PlayerState {
    private String playerName;
    private int score;
    private int lives;
    private int hintsRemaining;
    private int solutionsRemaining;

    public PlayerState(String playerName, int initialLives, int initialHints, int initialSolutions) {
        this.playerName = playerName;
        this.score = 0;
        this.lives = initialLives;
        this.hintsRemaining = initialHints;
        this.solutionsRemaining = initialSolutions;
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

    public int getHintsRemaining() {
        return hintsRemaining;
    }

    public void setHintsRemaining(int hintsRemaining) {
        this.hintsRemaining = Math.max(0, hintsRemaining);
    }

    public void useHint() {
        if (this.hintsRemaining > 0) {
            this.hintsRemaining--;
        }
    }

    public int getSolutionsRemaining() {
        return solutionsRemaining;
    }

    public void setSolutionsRemaining(int solutionsRemaining) {
        this.solutionsRemaining = Math.max(0, solutionsRemaining);
    }

    public void useSolution() {
        if (this.solutionsRemaining > 0) {
            this.solutionsRemaining--;
        }
    }

    public boolean isAlive() {
        return lives > 0;
    }

    @Override
    public String toString() {
        return "PlayerState{" +
                "playerName='" + playerName + '\'' +
                ", score=" + score +
                ", lives=" + lives +
                ", hintsRemaining=" + hintsRemaining +
                ", solutionsRemaining=" + solutionsRemaining +
                '}';
    }
}

