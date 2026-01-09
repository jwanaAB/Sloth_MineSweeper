package model;

/**
 * Observer interface for observing game state changes.
 * Components that need to be notified of game state changes should implement this interface.
 * 
 * @author Team Sloth
 */
public interface GameObserver {
    /**
     * Called when the game score changes.
     * 
     * @param newScore The new combined score
     */
    void onScoreChanged(int newScore);
    
    /**
     * Called when the shared lives change.
     * 
     * @param newLives The new number of shared lives
     * @param totalLives The total number of lives
     */
    void onLivesChanged(int newLives, int totalLives);
    
    /**
     * Called when the turn changes to a different player.
     * 
     * @param currentPlayer The current player number (1 or 2)
     * @param playerName The name of the current player
     */
    void onTurnChanged(int currentPlayer, String playerName);
    
    /**
     * Called when the game ends (either won or lost).
     * 
     * @param won true if the game was won, false if lost
     * @param winner The winner player number (1 or 2), or 0 if no winner (loss)
     */
    void onGameOver(boolean won, int winner);
    
    /**
     * Called when a cell is revealed.
     * 
     * @param row The row index of the revealed cell
     * @param col The column index of the revealed cell
     * @param player The player number (1 or 2) who revealed the cell
     */
    void onCellRevealed(int row, int col, int player);
}

