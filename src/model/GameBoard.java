package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a gameboard for a single player in the minesweeper game.
 * Manages a 2D grid of cells and handles cell allocation, revealing, and flagging.
 * 
 * @author Team Sloth
 */
public class GameBoard {
    
    private Cell[][] cells;
    private int rows;
    private int cols;
    private int totalMines;
    private int totalQuestionCells;
    private int totalSurpriseCells;
    private boolean firstClick = true;
    
    /**
     * Constructs a new GameBoard with the specified dimensions.
     * 
     * @param rows Number of rows in the board
     * @param cols Number of columns in the board
     */
    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        initializeBoard();
    }
    
    /**
     * Initializes the board with all empty cells in hidden state.
     */
    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell(Cell.CellType.EMPTY);
            }
        }
    }
    
    /**
     * Allocates cells across the board based on exact counts.
     * Distributes mines, question cells, surprise cells, and calculates number cells.
     * 
     * Allocation for Easy (9x9 = 81 cells):
     * - 10 mine cells
     * - 6 question cells
     * - 2 surprise cells
     * - Remaining 63 cells are regular cells (empty or number cells)
     * 
     * @param mineCount Exact number of mine cells (e.g., 10 for Easy)
     * @param questionCount Exact number of question cells (e.g., 6 for Easy)
     * @param surpriseCount Exact number of surprise cells (e.g., 2 for Easy)
     * @param questions List of questions to assign to question cells
     */
    public void allocateCells(int mineCount, int questionCount, 
                             int surpriseCount, List<Question> questions) {
        int totalCells = rows * cols;
        
        // Use exact counts as specified
        totalMines = mineCount;
        // Always use the requested number of question cells
        // If fewer questions are available, they will be reused (cycled)
        totalQuestionCells = questionCount;
        totalSurpriseCells = surpriseCount;


        // Create list of all positions
        List<int[]> positions = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                positions.add(new int[]{i, j});
            }
        }
        
        // Shuffle positions for random distribution
        Collections.shuffle(positions, new Random());
        
        int posIndex = 0;
        
        // Allocate mines
        for (int i = 0; i < totalMines && posIndex < positions.size(); i++) {
            int[] pos = positions.get(posIndex++);
            cells[pos[0]][pos[1]] = new Cell(Cell.CellType.MINE);
        }
        
        // Allocate question cells
        // Ensure we have enough questions - if not, we'll reuse questions
        List<Question> shuffledQuestions = new ArrayList<>(questions);
        Collections.shuffle(shuffledQuestions);
        int questionIndex = 0;
        for (int i = 0; i < totalQuestionCells && posIndex < positions.size(); i++) {
            int[] pos = positions.get(posIndex++);
            Cell questionCell = new Cell(Cell.CellType.QUESTION);
            // Reuse questions if we need more than available (cycle through the list)
            if (questionIndex >= shuffledQuestions.size()) {
                questionIndex = 0; // Reset to start if we've used all questions
            }
            questionCell.setQuestion(shuffledQuestions.get(questionIndex++));
            cells[pos[0]][pos[1]] = questionCell;
        }
        
        // Allocate surprise cells
        for (int i = 0; i < totalSurpriseCells && posIndex < positions.size(); i++) {
            int[] pos = positions.get(posIndex++);
            cells[pos[0]][pos[1]] = new Cell(Cell.CellType.SURPRISE);
        }
        
        // Remaining cells are empty (already initialized as EMPTY type)
        // These will become regular cells - either EMPTY (if no adjacent mines) 
        // or NUMBER (if adjacent to mines) after calculateAdjacentMines()
        
        // Calculate adjacent mine counts for all cells
        // This converts EMPTY cells adjacent to mines into NUMBER cells
        calculateAdjacentMines();
        
        // Verify and fix allocation to ensure exact counts
        verifyAndFixAllocation(mineCount, questionCount, surpriseCount, questions);
    }
    
    /**
     * Gets a shuffled list of all regular cell positions (EMPTY or NUMBER cells).
     * 
     * @return List of regular cell positions [row, col]
     */
    private List<int[]> getRegularCellPositions() {
        List<int[]> regularPositions = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                if (cell.getType() == Cell.CellType.EMPTY || 
                    cell.getType() == Cell.CellType.NUMBER) {
                    regularPositions.add(new int[]{i, j});
                }
            }
        }
        Collections.shuffle(regularPositions, new Random());
        return regularPositions;
    }
    
    /**
     * Verifies that the board has the correct number of each cell type,
     * and fixes it by converting regular cells if needed.
     * 
     * @param requiredMines Required number of mine cells
     * @param requiredQuestions Required number of question cells
     * @param requiredSurprises Required number of surprise cells
     * @param questions List of questions to assign to question cells
     */
    private void verifyAndFixAllocation(int requiredMines, int requiredQuestions, 
                                       int requiredSurprises, List<Question> questions) {
        // Count current allocation
        int currentMines = 0;
        int currentQuestions = 0;
        int currentSurprises = 0;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                switch (cell.getType()) {
                    case MINE:
                        currentMines++;
                        break;
                    case QUESTION:
                        currentQuestions++;
                        break;
                    case SURPRISE:
                        currentSurprises++;
                        break;
                    case EMPTY:
                    case NUMBER:
                        // Regular cells - counted separately when needed
                        break;
                }
            }
        }
        
        // Fix mines if needed
        while (currentMines < requiredMines) {
            List<int[]> regularPositions = getRegularCellPositions();
            if (regularPositions.isEmpty()) break;
            int[] pos = regularPositions.get(0);
            cells[pos[0]][pos[1]] = new Cell(Cell.CellType.MINE);
            currentMines++;
        }
        while (currentMines > requiredMines) {
            // Find a mine and convert to empty
            for (int i = 0; i < rows && currentMines > requiredMines; i++) {
                for (int j = 0; j < cols && currentMines > requiredMines; j++) {
                    if (cells[i][j].getType() == Cell.CellType.MINE) {
                        cells[i][j] = new Cell(Cell.CellType.EMPTY);
                        currentMines--;
                        break;
                    }
                }
            }
        }
        
        // Fix question cells if needed
        List<Question> shuffledQuestions = new ArrayList<>(questions);
        if (!shuffledQuestions.isEmpty()) {
            Collections.shuffle(shuffledQuestions);
        }
        int questionIndex = 0;
        
        while (currentQuestions < requiredQuestions) {
            List<int[]> regularPositions = getRegularCellPositions();
            if (regularPositions.isEmpty()) break;
            int[] pos = regularPositions.get(0);
            Cell questionCell = new Cell(Cell.CellType.QUESTION);
            // Only assign question if questions are available
            if (!shuffledQuestions.isEmpty()) {
                if (questionIndex >= shuffledQuestions.size()) {
                    questionIndex = 0; // Cycle through questions
                }
                questionCell.setQuestion(shuffledQuestions.get(questionIndex++));
            }
            cells[pos[0]][pos[1]] = questionCell;
            currentQuestions++;
        }
        while (currentQuestions > requiredQuestions) {
            // Find a question cell and convert to empty
            for (int i = 0; i < rows && currentQuestions > requiredQuestions; i++) {
                for (int j = 0; j < cols && currentQuestions > requiredQuestions; j++) {
                    if (cells[i][j].getType() == Cell.CellType.QUESTION) {
                        cells[i][j] = new Cell(Cell.CellType.EMPTY);
                        currentQuestions--;
                        break;
                    }
                }
            }
        }
        
        // Fix surprise cells if needed
        while (currentSurprises < requiredSurprises) {
            List<int[]> regularPositions = getRegularCellPositions();
            if (regularPositions.isEmpty()) break;
            int[] pos = regularPositions.get(0);
            cells[pos[0]][pos[1]] = new Cell(Cell.CellType.SURPRISE);
            currentSurprises++;
        }
        while (currentSurprises > requiredSurprises) {
            // Find a surprise cell and convert to empty
            for (int i = 0; i < rows && currentSurprises > requiredSurprises; i++) {
                for (int j = 0; j < cols && currentSurprises > requiredSurprises; j++) {
                    if (cells[i][j].getType() == Cell.CellType.SURPRISE) {
                        cells[i][j] = new Cell(Cell.CellType.EMPTY);
                        currentSurprises--;
                        break;
                    }
                }
            }
        }
        
        // Recalculate adjacent mines after fixing allocation
        calculateAdjacentMines();
    }
    
    /**
     * Calculates and sets the adjacent mine count for all NUMBER cells.
     * Cells adjacent to mines become NUMBER cells with the appropriate count.
     */
    private void calculateAdjacentMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                
                // Skip mines - they don't need adjacent counts
                if (cell.getType() == Cell.CellType.MINE || cell.getType() == Cell.CellType.QUESTION || cell.getType() == Cell.CellType.SURPRISE) {
                    continue;
                }
                
                // Count adjacent mines
                int mineCount = countAdjacentMines(i, j);
                
                // If there are adjacent mines, convert to NUMBER cell
                if (mineCount > 0) {
                    cell.setType(Cell.CellType.NUMBER);
                    cell.setAdjacentMines(mineCount);
                }
                // Otherwise, it remains EMPTY, QUESTION, or SURPRISE
            }
        }
    }
    
    /**
     * Counts the number of mines adjacent to a given cell position.
     * 
     * @param row The row index of the cell
     * @param col The column index of the cell
     * @return The number of adjacent mines (0-8)
     */
    public int countAdjacentMines(int row, int col) {
        int count = 0;
        
        // Check all 8 adjacent cells
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // Skip the cell itself
                
                int newRow = row + i;
                int newCol = col + j;
                
                // Check bounds
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                    if (cells[newRow][newCol].getType() == Cell.CellType.MINE) {
                        count++;
                    }
                }
            }
        }
        
        return count;
    }
    
    /**
     * Reveals a cell at the specified position.
     * If it's the first click, ensures no mine is at that position.
     * If an empty cell is revealed, automatically reveals adjacent empty cells.
     * 
     * @param row The row index
     * @param col The column index
     * @return true if a mine was revealed (game over), false otherwise
     */
    public boolean revealCell(int row, int col) {
        // Validate bounds
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        
        Cell cell = cells[row][col];
        
        // If cell is flagged, unflag it first
        if (cell.isFlagged()) {
            cell.toggleFlag(); // Unflag the cell
        }
        
        // Can't reveal already revealed cells
        if (cell.isRevealed()) {
            return false;
        }
        
        // Handle first click - ensure no mine
        if (firstClick) {
            firstClick = false;
            
            // If first click is on a mine, move the mine
            if (cell.getType() == Cell.CellType.MINE) {
                moveMineAway(row, col);
            }
        }
        
        // Reveal the cell
        cell.reveal();
        
        // If it's a mine, game over
        if (cell.getType() == Cell.CellType.MINE) {
            return true;
        }
        
        // If it's an empty cell, reveal adjacent empty cells recursively
        // Also reveal adjacent number cells (but don't recurse from them)
        if (cell.getType() == Cell.CellType.EMPTY) {
            revealAdjacentEmptyCells(row, col);
        }
        
        return false;
    }
    
    /**
     * Recursively reveals adjacent empty cells when an empty cell is revealed.
     * Also reveals adjacent number cells but doesn't recurse from them.
     * 
     * @param row The row index
     * @param col The column index
     */
    private void revealAdjacentEmptyCells(int row, int col) {
        // Check all 8 adjacent cells (directly adjacent only)
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // Skip the cell itself
                
                int newRow = row + i;
                int newCol = col + j;
                
                // Check bounds - ensure we're only checking directly adjacent cells
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                    Cell adjacentCell = cells[newRow][newCol];
                    
                    // Only reveal hidden, non-flagged cells
                    if (adjacentCell.isHidden() && !adjacentCell.isFlagged()) {
                        // Reveal number cells (but don't recurse from them)
                        if (adjacentCell.getType() == Cell.CellType.NUMBER) {
                            adjacentCell.reveal();
                        }
                        // Reveal empty cells and recurse
                        else if (adjacentCell.getType() == Cell.CellType.EMPTY) {
                            adjacentCell.reveal();
                            revealAdjacentEmptyCells(newRow, newCol);
                        }
                        // Also reveal question and surprise cells (but don't recurse)
                        else if (adjacentCell.getType() == Cell.CellType.QUESTION || 
                                 adjacentCell.getType() == Cell.CellType.SURPRISE) {
                            adjacentCell.reveal();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Moves a mine away from the first click position to ensure first click safety.
     * 
     * @param row The row index of the first click
     * @param col The column index of the first click
     */
    private void moveMineAway(int row, int col) {
        // Find the first non-mine cell and swap
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Skip the first click position and adjacent cells
                if (Math.abs(i - row) <= 1 && Math.abs(j - col) <= 1) {
                    continue;
                }
                
                if (cells[i][j].getType() != Cell.CellType.MINE) {
                    // Swap: move mine here, make first click position empty
                    cells[i][j] = new Cell(Cell.CellType.MINE);
                    cells[row][col] = new Cell(Cell.CellType.EMPTY);
                    
                    // Recalculate adjacent mines
                    calculateAdjacentMines();
                    return;
                }
            }
        }
    }
    
    /**
     * Toggles the flag state of a cell at the specified position.
     * 
     * @param row The row index
     * @param col The column index
     */
    public void flagCell(int row, int col) {
        // Validate bounds
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }
        
        Cell cell = cells[row][col];
        
        // Can only flag/unflag hidden cells
        if (cell.isHidden() || cell.isFlagged()) {
            cell.toggleFlag();
        }
    }
    
    /**
     * Gets the cell at the specified position.
     * 
     * @param row The row index
     * @param col The column index
     * @return The Cell object at the specified position, or null if out of bounds
     */
    public Cell getCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }
        return cells[row][col];
    }
    
    /**
     * Gets the number of rows in this board.
     * 
     * @return The number of rows
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * Gets the number of columns in this board.
     * 
     * @return The number of columns
     */
    public int getCols() {
        return cols;
    }
    
    /**
     * Gets the total number of mines on this board.
     * 
     * @return The number of mines
     */
    public int getTotalMines() {
        return totalMines;
    }
    
    /**
     * Checks if all non-mine cells have been revealed.
     * 
     * @return true if the game is won, false otherwise
     */
    public boolean isGameWon() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                // If it's not a mine and not revealed, game is not won
                if (cell.getType() != Cell.CellType.MINE && !cell.isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }
}

