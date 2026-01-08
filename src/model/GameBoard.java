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
                cells[i][j] = CellFactory.createEmptyCell();
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
            cells[pos[0]][pos[1]] = CellFactory.createMineCell();
        }
        
        // Calculate adjacent mine counts after placing mines
        // This converts EMPTY cells adjacent to mines into NUMBER cells
        calculateAdjacentMines();
        
        // Get positions with zero adjacent mines for QuestionCell and SurpriseCell placement
        List<int[]> emptyCellPositions = getEmptyCellPositions();
        int emptyPosIndex = 0;
        
        // Allocate question cells
        // Ensure we have enough questions - if not, we'll reuse questions
        List<Question> shuffledQuestions = new ArrayList<>(questions);
        Collections.shuffle(shuffledQuestions);
        int questionIndex = 0;
        for (int i = 0; i < totalQuestionCells && emptyPosIndex < emptyCellPositions.size(); i++) {
            int[] pos = emptyCellPositions.get(emptyPosIndex++);
            Cell questionCell;
            // Reuse questions if we need more than available (cycle through the list)
            if (questionIndex >= shuffledQuestions.size()) {
                questionIndex = 0; // Reset to start if we've used all questions
            }
            if (!shuffledQuestions.isEmpty()) {
                questionCell = CellFactory.createQuestionCell(shuffledQuestions.get(questionIndex++));
            } else {
                questionCell = CellFactory.createQuestionCell();
            }
            cells[pos[0]][pos[1]] = questionCell;
        }
        
        // Refresh empty cell positions after placing question cells
        emptyCellPositions = getEmptyCellPositions();
        emptyPosIndex = 0;
        
        // Allocate surprise cells
        for (int i = 0; i < totalSurpriseCells && emptyPosIndex < emptyCellPositions.size(); i++) {
            int[] pos = emptyCellPositions.get(emptyPosIndex++);
            cells[pos[0]][pos[1]] = CellFactory.createSurpriseCell();
        }
        
        // Remaining cells are empty (already initialized as EMPTY type)
        // These will become regular cells - either EMPTY (if no adjacent mines) 
        // or NUMBER (if adjacent to mines) after calculateAdjacentMines()
        
        // Recalculate adjacent mine counts after placing all special cells
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
     * Gets a shuffled list of all positions with zero adjacent mines.
     * These positions are suitable for placing QuestionCell and SurpriseCell,
     * which should behave like EmptyCell (no adjacent mines).
     * 
     * @return List of empty cell positions [row, col] with zero adjacent mines
     */
    private List<int[]> getEmptyCellPositions() {
        List<int[]> emptyPositions = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                // Only include positions that are currently EMPTY cells (not NUMBER or MINE)
                // and have zero adjacent mines
                if (cell.getType() == Cell.CellType.EMPTY && 
                    countAdjacentMines(i, j) == 0) {
                    emptyPositions.add(new int[]{i, j});
                }
            }
        }
        Collections.shuffle(emptyPositions, new Random());
        return emptyPositions;
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
            cells[pos[0]][pos[1]] = CellFactory.createMineCell();
            currentMines++;
        }
        while (currentMines > requiredMines) {
            // Find a mine and convert to empty
            for (int i = 0; i < rows && currentMines > requiredMines; i++) {
                for (int j = 0; j < cols && currentMines > requiredMines; j++) {
                    if (cells[i][j].getType() == Cell.CellType.MINE) {
                        cells[i][j] = CellFactory.createEmptyCell();
                        currentMines--;
                        break;
                    }
                }
            }
        }
        
        // Validate and fix question cells - ensure they're not near mines
        List<Question> shuffledQuestions = new ArrayList<>(questions);
        if (!shuffledQuestions.isEmpty()) {
            Collections.shuffle(shuffledQuestions);
        }
        int questionIndex = 0;
        
        // First, check if any existing question cells are near mines and move them
        List<int[]> questionCellsToMove = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cells[i][j].getType() == Cell.CellType.QUESTION) {
                    if (countAdjacentMines(i, j) > 0) {
                        questionCellsToMove.add(new int[]{i, j});
                    }
                }
            }
        }
        
        // Move question cells that are near mines to empty positions
        for (int[] pos : questionCellsToMove) {
            QuestionCell questionCell = (QuestionCell) cells[pos[0]][pos[1]];
            List<int[]> emptyPositions = getEmptyCellPositions();
            if (!emptyPositions.isEmpty()) {
                int[] newPos = emptyPositions.get(0);
                cells[newPos[0]][newPos[1]] = questionCell;
                cells[pos[0]][pos[1]] = CellFactory.createEmptyCell();
            } else {
                // If no empty positions available, convert to empty cell
                cells[pos[0]][pos[1]] = CellFactory.createEmptyCell();
                currentQuestions--;
            }
        }
        
        // Fix question cell count if needed
        while (currentQuestions < requiredQuestions) {
            List<int[]> emptyPositions = getEmptyCellPositions();
            if (emptyPositions.isEmpty()) break;
            int[] pos = emptyPositions.get(0);
            Cell questionCell;
            // Only assign question if questions are available
            if (!shuffledQuestions.isEmpty()) {
                if (questionIndex >= shuffledQuestions.size()) {
                    questionIndex = 0; // Cycle through questions
                }
                questionCell = CellFactory.createQuestionCell(shuffledQuestions.get(questionIndex++));
            } else {
                questionCell = CellFactory.createQuestionCell();
            }
            cells[pos[0]][pos[1]] = questionCell;
            currentQuestions++;
        }
        while (currentQuestions > requiredQuestions) {
            // Find a question cell and convert to empty
            for (int i = 0; i < rows && currentQuestions > requiredQuestions; i++) {
                for (int j = 0; j < cols && currentQuestions > requiredQuestions; j++) {
                    if (cells[i][j].getType() == Cell.CellType.QUESTION) {
                        cells[i][j] = CellFactory.createEmptyCell();
                        currentQuestions--;
                        break;
                    }
                }
            }
        }
        
        // Validate and fix surprise cells - ensure they're not near mines
        // First, check if any existing surprise cells are near mines and move them
        List<int[]> surpriseCellsToMove = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cells[i][j].getType() == Cell.CellType.SURPRISE) {
                    if (countAdjacentMines(i, j) > 0) {
                        surpriseCellsToMove.add(new int[]{i, j});
                    }
                }
            }
        }
        
        // Move surprise cells that are near mines to empty positions
        for (int[] pos : surpriseCellsToMove) {
            SurpriseCell surpriseCell = (SurpriseCell) cells[pos[0]][pos[1]];
            List<int[]> emptyPositions = getEmptyCellPositions();
            if (!emptyPositions.isEmpty()) {
                int[] newPos = emptyPositions.get(0);
                cells[newPos[0]][newPos[1]] = surpriseCell;
                cells[pos[0]][pos[1]] = CellFactory.createEmptyCell();
            } else {
                // If no empty positions available, convert to empty cell
                cells[pos[0]][pos[1]] = CellFactory.createEmptyCell();
                currentSurprises--;
            }
        }
        
        // Fix surprise cell count if needed
        while (currentSurprises < requiredSurprises) {
            List<int[]> emptyPositions = getEmptyCellPositions();
            if (emptyPositions.isEmpty()) break;
            int[] pos = emptyPositions.get(0);
            cells[pos[0]][pos[1]] = CellFactory.createSurpriseCell();
            currentSurprises++;
        }
        while (currentSurprises > requiredSurprises) {
            // Find a surprise cell and convert to empty
            for (int i = 0; i < rows && currentSurprises > requiredSurprises; i++) {
                for (int j = 0; j < cols && currentSurprises > requiredSurprises; j++) {
                    if (cells[i][j].getType() == Cell.CellType.SURPRISE) {
                        cells[i][j] = CellFactory.createEmptyCell();
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
     * Calculates and sets the adjacent mine count for all non-mine, non-special cells.
     * <p>
     * This method is called multiple times during board setup and when mines are moved
     * (e.g., first-click safety in {@link #moveMineAway(int, int)}). To keep the
     * numbers consistent, it must always recompute the adjacent mine count and update
     * existing {@link NumberCell} instances instead of only converting from EMPTY
     * once.
     * <p>
     * Behaviour:
     * <ul>
     *   <li>If a position has one or more adjacent mines, it will contain a
     *       {@link NumberCell} with the correct {@code adjacentMines} value.</li>
     *   <li>If a position has zero adjacent mines, it will contain an
     *       {@link EmptyCell}.</li>
     *   <li>{@link MineCell}, {@link QuestionCell}, and {@link SurpriseCell}
     *       are left unchanged.</li>
     * </ul>
     */
    private void calculateAdjacentMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];

                // Skip mines, questions, and surprises - they don't get number values
                if (cell.getType() == Cell.CellType.MINE
                        || cell.getType() == Cell.CellType.QUESTION
                        || cell.getType() == Cell.CellType.SURPRISE) {
                    continue;
                }

                // Recompute adjacent mine count for this position
                int mineCount = countAdjacentMines(i, j);

                // Preserve the current state and scoring flag when we potentially replace the cell
                Cell.CellState state = cell.getState();
                boolean flagScoreContributed = cell.hasFlagScoreContributed();

                if (mineCount > 0) {
                    // Ensure this is a NumberCell with the correct count
                    NumberCell numberCell;
                    if (cell instanceof NumberCell) {
                        numberCell = (NumberCell) cell;
                        numberCell.setAdjacentMines(mineCount);
                    } else {
                        numberCell = (NumberCell) CellFactory.createNumberCell(mineCount);
                        cells[i][j] = numberCell;
                    }

                    // Restore state if we created a new cell
                    if (cells[i][j] == numberCell) {
                        if (state == Cell.CellState.REVEALED) {
                            numberCell.reveal();
                        } else if (state == Cell.CellState.FLAGGED) {
                            numberCell.toggleFlag();
                        }
                        if (flagScoreContributed) {
                            numberCell.setFlagScoreContributed(true);
                        }
                    }
                } else {
                    // No adjacent mines – ensure the cell is an EmptyCell
                    if (!(cell instanceof EmptyCell)) {
                        EmptyCell emptyCell = (EmptyCell) CellFactory.createEmptyCell();
                        cells[i][j] = emptyCell;

                        // Restore state on the new empty cell
                        if (state == Cell.CellState.REVEALED) {
                            emptyCell.reveal();
                        } else if (state == Cell.CellState.FLAGGED) {
                            emptyCell.toggleFlag();
                        }
                        if (flagScoreContributed) {
                            emptyCell.setFlagScoreContributed(true);
                        }
                    }
                    // If it was already an EmptyCell and mineCount == 0, nothing to change
                }
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
                // Update cell reference after mine was moved
                cell = cells[row][col];
            }
        }
        






        // Reveal the cell
        cell.reveal();
        
        if (cell.getType() == Cell.CellType.MINE) {
            return true;
        }
        
        // If it's an empty cell (or any subclass like QuestionCell or SurpriseCell), 
        // reveal adjacent cells recursively
        if (cell instanceof EmptyCell) {
            revealAdjacentEmptyCells(row, col);
        }
        
        return false;
    }
    
    /**
     * Recursively reveals adjacent cells when an empty cell (or its subclasses) is revealed.
     * Reveals adjacent number cells (but doesn't recurse from them).
     * Recursively reveals adjacent empty cells and their subclasses (QuestionCell, SurpriseCell).
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
                        if (adjacentCell instanceof NumberCell) {
                            adjacentCell.reveal();
                        }
                        // Recursively reveal empty cells and their subclasses (QuestionCell, SurpriseCell)
                        else if (adjacentCell instanceof EmptyCell) {
                            adjacentCell.reveal();
                            revealAdjacentEmptyCells(newRow, newCol);
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
                    cells[i][j] = CellFactory.createMineCell();
                    cells[row][col] = CellFactory.createEmptyCell();
                    
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
    
    /**
     * Reveals all cells on the board (used when game is over).
     * This will reveal all hidden and flagged cells.
     */
    public void revealAllCells() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                // Reveal all cells regardless of their current state
                // The reveal() method will only change HIDDEN to REVEALED,
                // so we need to handle FLAGGED cells too
                if (cell.isFlagged()) {
                    cell.toggleFlag(); // Unflag first
                }
                cell.reveal(); // Then reveal
            }
        }
    }
}

