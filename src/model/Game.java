package model;

import controller.QuestionLogic;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the overall game state for a two-player minesweeper game.
 * Manages two gameboards (one for each player), turn-based gameplay,
 * difficulty settings, and player information.
 * 
 * @author Team Sloth
 */
public class Game {
    
    /**
     * Enum representing the difficulty levels of the game.
     */
    public enum Difficulty {
        /** Easy difficulty: 9x9 board */
        EASY(9, 9),
        /** Medium difficulty: 13x13 board */
        MEDIUM(13, 13),
        /** Hard difficulty: 16x16 board */
        HARD(16, 16);
        
        private final int rows;
        private final int cols;
        
        Difficulty(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }
        
        /**
         * Gets the number of rows for this difficulty level.
         * 
         * @return The number of rows
         */
        public int getRows() {
            return rows;
        }
        
        /**
         * Gets the number of columns for this difficulty level.
         * 
         * @return The number of columns
         */
        public int getCols() {
            return cols;
        }
    }
    
    private GameBoard player1Board;
    private GameBoard player2Board;
    private int currentPlayer; // 1 or 2
    private Difficulty difficulty;
    private String player1Name;
    private String player2Name;
    private int combinedScore; // Placeholder for now
    private int sharedLives; // Shared lives pool for both players
    private int totalLives;
    private boolean gameOver;
    private boolean gameWon;
    private int winner; // 1 or 2, or 0 if no winner yet
    private List<GameObserver> observers; // List of observers for the Observer pattern
    
    /**
     * Constructs a new Game with the specified settings.
     * 
     * @param player1Name Name of player 1
     * @param player2Name Name of player 2
     * @param difficulty The difficulty level (EASY, MEDIUM, or HARD)
     * @param questionLogic The QuestionLogic instance to load questions from
     */
    public Game(String player1Name, String player2Name, Difficulty difficulty, QuestionLogic questionLogic) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.difficulty = difficulty;
        this.currentPlayer = 1;
        this.combinedScore = 0;
        this.gameOver = false;
        this.gameWon = false;
        this.winner = 0;
        this.observers = new ArrayList<>();
        
        // Set shared lives based on difficulty
        switch (difficulty) {
            case EASY:
                this.totalLives = 10;
                break;
            case MEDIUM:
                this.totalLives = 8;
                break;
            case HARD:
                this.totalLives = 6;
                break;
        }
        this.sharedLives = totalLives; // Both players share the same lives pool
        
        // Initialize boards
        initializeBoards(questionLogic);
    }
    
    /**
     * Initializes both gameboards with the appropriate dimensions and cell allocation.
     * 
     * @param questionLogic The QuestionLogic instance to get questions from
     */
    private void initializeBoards(QuestionLogic questionLogic) {
        int rows = difficulty.getRows();
        int cols = difficulty.getCols();
        
        // Create boards
        player1Board = new GameBoard(rows, cols);
        player2Board = new GameBoard(rows, cols);
        
        // Load questions
        List<Question> questions = questionLogic.getQuestions();
        
        // Allocate cells based on difficulty with exact counts
        int mineCount, questionCount, surpriseCount;
        switch (difficulty) {
            case EASY:
                mineCount = 10;
                questionCount = 6;
                surpriseCount = 2;
                break;
            case MEDIUM:
                mineCount = 26;
                questionCount = 7;
                surpriseCount = 3;
                break;
            case HARD:
                mineCount = 44;
                questionCount = 11;
                surpriseCount = 4;
                break;
            default:
                mineCount = 10;
                questionCount = 6;
                surpriseCount = 2;
                break;
        }
        
        // Allocate cells for both boards
        player1Board.allocateCells(mineCount, questionCount, surpriseCount, questions);
        player2Board.allocateCells(mineCount, questionCount, surpriseCount, questions);
    }
    
    /**
     * Gets the gameboard for player 1.
     * 
     * @return Player 1's gameboard
     */
    public GameBoard getPlayer1Board() {
        return player1Board;
    }
    
    /**
     * Gets the gameboard for player 2.
     * 
     * @return Player 2's gameboard
     */
    public GameBoard getPlayer2Board() {
        return player2Board;
    }
    
    /**
     * Gets the gameboard for the current player.
     * 
     * @return The current player's gameboard
     */
    public GameBoard getCurrentBoard() {
        return currentPlayer == 1 ? player1Board : player2Board;
    }
    
    /**
     * Gets the gameboard for the specified player.
     * 
     * @param player The player number (1 or 2)
     * @return The specified player's gameboard
     */
    public GameBoard getBoard(int player) {
        return player == 1 ? player1Board : player2Board;
    }
    
    /**
     * Gets the current player number.
     * 
     * @return 1 for player 1, 2 for player 2
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Gets the name of the current player.
     * 
     * @return The current player's name
     */
    public String getCurrentPlayerName() {
        return currentPlayer == 1 ? player1Name : player2Name;
    }
    
    /**
     * Gets the name of player 1.
     * 
     * @return Player 1's name
     */
    public String getPlayer1Name() {
        return player1Name;
    }
    
    /**
     * Gets the name of player 2.
     * 
     * @return Player 2's name
     */
    public String getPlayer2Name() {
        return player2Name;
    }
    
    /**
     * Switches to the next player's turn.
     * Always switches unless the game is over.
     */
    public void switchTurn() {
        if (!gameOver) {
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
            notifyTurnChanged();
        }
    }
    
    /**
     * Gets the current difficulty level.
     * 
     * @return The difficulty level
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }
    
    /**
     * Gets the combined score of both players.
     * 
     * @return The combined score (placeholder for now)
     */
    public int getCombinedScore() {
        return combinedScore;
    }
    
    /**
     * Sets the combined score.
     * 
     * @param score The new combined score
     */
    public void setCombinedScore(int score) {
        this.combinedScore = score;
        notifyScoreChanged();
    }
    
    /**
     * Gets the total number of lives (shared pool).
     * 
     * @return The total lives
     */
    public int getTotalLives() {
        return totalLives;
    }
    
    /**
     * Gets the number of shared lives remaining.
     * 
     * @return The remaining shared lives
     */
    public int getSharedLives() {
        return sharedLives;
    }
    
    /**
     * Decreases the shared lives by 1 when a mine is hit.
     * 
     * @return true if lives have run out (game over), false otherwise
     */
    public boolean decreaseSharedLives() {
        sharedLives--;
        notifyLivesChanged();
        if (sharedLives <= 0) {
            gameOver = true;
            // No winner if shared lives run out - it's a draw/loss
            winner = 0;
            notifyGameOver(false, 0);
            return true;
        }
        return false;
    }
    
    /**
     * Adds to the shared score.
     * 
     * @param points The points to add (can be negative)
     */
    public void addSharedScore(int points) {
        this.combinedScore += points;
        notifyScoreChanged();
    }
    
    /**
     * Adds a shared life (cannot exceed initial total lives).
     */
    public void addSharedLife() {
        if (sharedLives < totalLives) {
            sharedLives++;
            notifyLivesChanged();
        }
    }
    
    /**
     * Sets the shared lives directly.
     * 
     * @param lives The new number of shared lives
     */
    public void setSharedLives(int lives) {
        this.sharedLives = Math.max(0, Math.min(lives, totalLives));
        notifyLivesChanged();
    }
    
    /**
     * Checks if the game is over.
     * 
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }
    
    /**
     * Sets the game over state.
     * 
     * @param gameOver true to set game as over, false otherwise
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (gameOver) {
            notifyGameOver(gameWon, winner);
        }
    }
    
    /**
     * Checks if the game has been won.
     * 
     * @return true if the game is won, false otherwise
     */
    public boolean isGameWon() {
        return gameWon;
    }
    
    /**
     * Gets the winner of the game.
     * 
     * @return 1 for player 1, 2 for player 2, or 0 if no winner yet
     */
    public int getWinner() {
        return winner;
    }
    
    /**
     * Handles a cell reveal action for the current player.
     * 
     * @param row The row index of the cell to reveal
     * @param col The column index of the cell to reveal
     * @return true if a mine was hit, false otherwise
     */
    public boolean revealCell(int row, int col) {
        if (gameOver) {
            return false;
        }
        
        GameBoard currentBoard = getCurrentBoard();
        boolean mineHit = currentBoard.revealCell(row, col);
        
        if (mineHit) {
            // Mine hit - don't end game, just return true so controller can handle it
            // Game continues, turn will switch
        } else {
            // Check if current player won - only set gameWon if ALL non-mine cells are revealed
            // Don't set gameOver here, let the controller handle it after checking
            if (currentBoard.isGameWon()) {
                gameWon = true;
                winner = currentPlayer;
                // gameOver will be set by controller after showing message
            }
        }
        
        // Notify observers that a cell was revealed
        notifyCellRevealed(row, col, currentPlayer);
        
        return mineHit;
    }
    
    /**
     * Handles a cell flag action for the current player.
     * 
     * @param row The row index of the cell to flag
     * @param col The column index of the cell to flag
     */
    public void flagCell(int row, int col) {
        if (gameOver) {
            return;
        }
        
        GameBoard currentBoard = getCurrentBoard();
        currentBoard.flagCell(row, col);
    }
    
    /**
     * Checks if a cell can be revealed by the current player.
     * Players can only reveal cells on their own board.
     * 
     * @param row The row index
     * @param col The column index
     * @param player The player number (1 or 2)
     * @return true if the cell can be revealed, false otherwise
     */
    public boolean canRevealCell(int row, int col, int player) {
        if (gameOver || player != currentPlayer) {
            return false;
        }
        
        GameBoard board = getBoard(player);
        Cell cell = board.getCell(row, col);
        
        // Allow clicking on any cell if it's the player's turn
        // (already revealed cells will be handled separately and won't cause errors)
        return cell != null;
    }
    
    /**
     * Checks if a cell can be flagged by the current player.
     * Players can only flag cells on their own board.
     * 
     * @param row The row index
     * @param col The column index
     * @param player The player number (1 or 2)
     * @return true if the cell can be flagged, false otherwise
     */
    public boolean canFlagCell(int row, int col, int player) {
        if (gameOver || player != currentPlayer) {
            return false;
        }
        
        GameBoard board = getBoard(player);
        Cell cell = board.getCell(row, col);
        
        return cell != null && (cell.isHidden() || cell.isFlagged());
    }
    
    /**
     * Reveals all cells on both players' boards (used when game is over).
     */
    public void revealAllCells() {
        player1Board.revealAllCells();
        player2Board.revealAllCells();
    }
    
    /**
     * Adds an observer to be notified of game state changes.
     * 
     * @param observer The observer to add
     */
    public void addObserver(GameObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Removes an observer from the list of observers.
     * 
     * @param observer The observer to remove
     */
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notifies all observers that the score has changed.
     */
    private void notifyScoreChanged() {
        for (GameObserver observer : observers) {
            observer.onScoreChanged(combinedScore);
        }
    }
    
    /**
     * Notifies all observers that the shared lives have changed.
     */
    private void notifyLivesChanged() {
        for (GameObserver observer : observers) {
            observer.onLivesChanged(sharedLives, totalLives);
        }
    }
    
    /**
     * Notifies all observers that the turn has changed.
     */
    private void notifyTurnChanged() {
        String playerName = getCurrentPlayerName();
        for (GameObserver observer : observers) {
            observer.onTurnChanged(currentPlayer, playerName);
        }
    }
    
    /**
     * Notifies all observers that the game is over.
     */
    private void notifyGameOver(boolean won, int winner) {
        for (GameObserver observer : observers) {
            observer.onGameOver(won, winner);
        }
    }
    
    /**
     * Notifies all observers that a cell has been revealed.
     */
    private void notifyCellRevealed(int row, int col, int player) {
        for (GameObserver observer : observers) {
            observer.onCellRevealed(row, col, player);
        }
    }
}

