package controller;

import model.*;
import java.util.*;

/**
 * AI bot for playing Minesweeper cooperatively with a human player.
 * 
 * The bot plays on its own board (Player 2's board) and makes intelligent moves
 * based on visible information only. It does not cheat by accessing hidden cell types.
 * 
 * Strategy:
 * 1. First, apply simple logic to find forced safe cells (cells that must be safe
 *    based on revealed number cells and flagged mines)
 * 2. If no forced safe cells exist, find forced mines (cells that must be mines)
 *    and flag them (though the bot doesn't flag, it avoids them)
 * 3. If no forced moves exist, make educated guesses:
 *    - Prefer cells adjacent to revealed number cells (more information)
 *    - Avoid cells far from any revealed cells (less information)
 *    - Not completely random, but not perfect either
 * 
 * When the bot reveals a question cell, the human player will be prompted to
 * answer the question (handled by GameController).
 * 
 * @author Team Sloth
 */
public class MinesweeperBot {
    
    private final Random random;
    
    /**
     * Constructs a new MinesweeperBot.
     */
    public MinesweeperBot() {
        this.random = new Random();
    }
    
    /**
     * Selects the next cell for the bot to reveal on its board.
     * Uses a multi-tier strategy: forced safe cells -> potential question cells -> educated guesses.
     * 
     * @param board The bot's game board (Player 2's board)
     * @return An array [row, col] representing the cell to reveal, or null if no valid move
     */
    public int[] selectCellToReveal(GameBoard board) {
        if (board == null) {
            return null;
        }
        
        // Strategy 1: Find forced safe cells (cells that must be safe based on logic)
        List<int[]> safeCells = findForcedSafeCells(board);
        if (!safeCells.isEmpty()) {
            // Pick a random safe cell
            return safeCells.get(random.nextInt(safeCells.size()));
        }
        
        // Strategy 2: Prioritize potential question cells (hidden cells with no adjacent revealed cells)
        // Question cells are typically placed in areas with zero adjacent mines, so cells
        // with no adjacent revealed cells are more likely to be question cells
        List<int[]> potentialQuestionCells = findPotentialQuestionCells(board);
        if (!potentialQuestionCells.isEmpty()) {
            // Pick a random potential question cell to reveal
            return potentialQuestionCells.get(random.nextInt(potentialQuestionCells.size()));
        }
        
        // Strategy 3: Make educated guesses (prefer cells near revealed numbers)
        List<int[]> educatedGuesses = findEducatedGuesses(board);
        if (!educatedGuesses.isEmpty()) {
            return educatedGuesses.get(random.nextInt(educatedGuesses.size()));
        }
        
        // Strategy 4: Fallback to any unrevealed cell (should rarely happen)
        List<int[]> allUnrevealed = getAllUnrevealedCells(board);
        if (!allUnrevealed.isEmpty()) {
            return allUnrevealed.get(random.nextInt(allUnrevealed.size()));
        }
        
        return null; // No valid move
    }
    
    /**
     * Finds cells that are forced to be safe based on revealed number cells.
     * 
     * A cell is forced safe if:
     * - It's adjacent to a revealed number cell
     * - All adjacent mines for that number cell have been accounted for
     *   (either flagged or revealed as mines)
     * - The remaining hidden adjacent cells must be safe
     * 
     * @param board The game board to analyze
     * @return List of forced safe cell positions [row, col]
     */
    private List<int[]> findForcedSafeCells(GameBoard board) {
        List<int[]> safeCells = new ArrayList<>();
        int rows = board.getRows();
        int cols = board.getCols();
        
        // For each revealed number cell, check if we can identify safe adjacent cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                
                // Only analyze revealed number cells
                if (cell instanceof NumberCell && cell.isRevealed()) {
                    NumberCell numberCell = (NumberCell) cell;
                    int requiredMines = numberCell.getAdjacentMines();
                    
                    // Count flagged and revealed mine cells adjacent to this number cell
                    int flaggedOrMineCount = 0;
                    List<int[]> hiddenAdjacent = new ArrayList<>();
                    
                    // Check all 8 adjacent cells
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            if (di == 0 && dj == 0) continue; // Skip the cell itself
                            
                            int ni = i + di;
                            int nj = j + dj;
                            
                            if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                                Cell adjCell = board.getCell(ni, nj);
                                if (adjCell != null) {
                                    if (adjCell.isFlagged()) {
                                        // Flagged cells are assumed to be mines
                                        flaggedOrMineCount++;
                                    } else if (adjCell.isRevealed() && 
                                               adjCell.getType() == Cell.CellType.MINE) {
                                        // Revealed mine cells
                                        flaggedOrMineCount++;
                                    } else if (adjCell.isHidden() && !adjCell.isFlagged()) {
                                        // Hidden, unflagged cells are candidates
                                        hiddenAdjacent.add(new int[]{ni, nj});
                                    }
                                }
                            }
                        }
                    }
                    
                    // If all required mines are accounted for, remaining hidden cells are safe
                    if (flaggedOrMineCount == requiredMines && !hiddenAdjacent.isEmpty()) {
                        safeCells.addAll(hiddenAdjacent);
                    }
                }
            }
        }
        
        // Remove duplicates
        return removeDuplicates(safeCells);
    }
    
    /**
     * Finds potential question cells - hidden cells with no adjacent revealed cells.
     * Question cells are typically placed in areas with zero adjacent mines, so cells
     * that are isolated (no revealed neighbors) are more likely to be question cells.
     * 
     * @param board The game board to analyze
     * @return List of potential question cell positions [row, col]
     */
    private List<int[]> findPotentialQuestionCells(GameBoard board) {
        List<int[]> candidates = new ArrayList<>();
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                
                // Only consider hidden, unflagged cells
                if (cell != null && cell.isHidden() && !cell.isFlagged()) {
                    // Check if this cell has no adjacent revealed cells
                    boolean hasRevealedNeighbor = false;
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            if (di == 0 && dj == 0) continue;
                            
                            int ni = i + di;
                            int nj = j + dj;
                            
                            if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                                Cell adjCell = board.getCell(ni, nj);
                                if (adjCell != null && adjCell.isRevealed()) {
                                    hasRevealedNeighbor = true;
                                    break;
                                }
                            }
                        }
                        if (hasRevealedNeighbor) break;
                    }
                    
                    // If no revealed neighbors, this is a potential question cell location
                    if (!hasRevealedNeighbor) {
                        candidates.add(new int[]{i, j});
                    }
                }
            }
        }
        
        return candidates;
    }
    
    /**
     * Finds educated guesses for cells to reveal.
     * Prefers cells adjacent to revealed number cells (more information available).
     * 
     * @param board The game board to analyze
     * @return List of educated guess positions [row, col]
     */
    private List<int[]> findEducatedGuesses(GameBoard board) {
        List<int[]> guesses = new ArrayList<>();
        int rows = board.getRows();
        int cols = board.getCols();
        
        // Find all hidden cells that are adjacent to at least one revealed number cell
        Set<String> candidateSet = new HashSet<>();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                
                // If this is a revealed number cell, mark its hidden neighbors as candidates
                if (cell instanceof NumberCell && cell.isRevealed()) {
                    // Check all 8 adjacent cells
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            if (di == 0 && dj == 0) continue;
                            
                            int ni = i + di;
                            int nj = j + dj;
                            
                            if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                                Cell adjCell = board.getCell(ni, nj);
                                if (adjCell != null && adjCell.isHidden() && !adjCell.isFlagged()) {
                                    String key = ni + "," + nj;
                                    candidateSet.add(key);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Convert set to list
        for (String key : candidateSet) {
            String[] parts = key.split(",");
            guesses.add(new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
        }
        
        // If we have candidates near revealed numbers, return them
        if (!guesses.isEmpty()) {
            return guesses;
        }
        
        // Fallback: prefer cells near any revealed cell (not just numbers)
        return findCellsNearRevealed(board);
    }
    
    /**
     * Finds hidden cells that are adjacent to any revealed cell (not just numbers).
     * This is a fallback when no cells are adjacent to number cells.
     * 
     * @param board The game board to analyze
     * @return List of cell positions [row, col]
     */
    private List<int[]> findCellsNearRevealed(GameBoard board) {
        List<int[]> guesses = new ArrayList<>();
        int rows = board.getRows();
        int cols = board.getCols();
        Set<String> candidateSet = new HashSet<>();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                
                // If this cell is revealed, mark its hidden neighbors as candidates
                if (cell != null && cell.isRevealed()) {
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            if (di == 0 && dj == 0) continue;
                            
                            int ni = i + di;
                            int nj = j + dj;
                            
                            if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                                Cell adjCell = board.getCell(ni, nj);
                                if (adjCell != null && adjCell.isHidden() && !adjCell.isFlagged()) {
                                    String key = ni + "," + nj;
                                    candidateSet.add(key);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Convert set to list
        for (String key : candidateSet) {
            String[] parts = key.split(",");
            guesses.add(new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
        }
        
        return guesses;
    }
    
    /**
     * Gets all unrevealed, unflagged cells on the board.
     * This is a fallback when no educated guesses can be made.
     * 
     * @param board The game board
     * @return List of unrevealed cell positions [row, col]
     */
    private List<int[]> getAllUnrevealedCells(GameBoard board) {
        List<int[]> unrevealed = new ArrayList<>();
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                if (cell != null && cell.isHidden() && !cell.isFlagged()) {
                    unrevealed.add(new int[]{i, j});
                }
            }
        }
        
        return unrevealed;
    }
    
    /**
     * Removes duplicate positions from a list of cell coordinates.
     * 
     * @param cells List of cell positions [row, col]
     * @return List without duplicates
     */
    private List<int[]> removeDuplicates(List<int[]> cells) {
        Set<String> seen = new HashSet<>();
        List<int[]> unique = new ArrayList<>();
        
        for (int[] cell : cells) {
            String key = cell[0] + "," + cell[1];
            if (!seen.contains(key)) {
                seen.add(key);
                unique.add(cell);
            }
        }
        
        return unique;
    }
    
    /**
     * Checks if there are any revealed question cells that haven't been opened yet.
     * The bot should prioritize opening question cells so the human can answer them.
     * 
     * @param board The game board
     * @return An array [row, col] of a question cell to open, or null if none found
     */
    public int[] findUnopenedQuestionCell(GameBoard board) {
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                if (cell instanceof QuestionCell && cell.isRevealed()) {
                    QuestionCell questionCell = (QuestionCell) cell;
                    if (!questionCell.isQuestionOpened()) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Checks if there are any revealed surprise cells that haven't been activated yet.
     * The bot should activate surprise cells when found.
     * 
     * @param board The game board
     * @return An array [row, col] of a surprise cell to activate, or null if none found
     */
    public int[] findUnactivatedSurpriseCell(GameBoard board) {
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                if (cell instanceof SurpriseCell && cell.isRevealed()) {
                    SurpriseCell surpriseCell = (SurpriseCell) cell;
                    if (!surpriseCell.isSurpriseActivated()) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        
        return null;
    }
}


