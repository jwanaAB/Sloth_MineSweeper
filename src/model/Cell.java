package model;

/**
 * Represents a single cell in the minesweeper gameboard.
 * Each cell can be one of several types (MINE, NUMBER, EMPTY, SURPRISE, QUESTION)
 * and can be in different states (HIDDEN, REVEALED, FLAGGED).
 * 
 * @author Team Sloth
 */
public class Cell {
    
    /**
     * Enum representing the different types of cells in the gameboard.
     */
    public enum CellType {
        /** Cell containing a mine */
        MINE,
        /** Cell showing number of adjacent mines (1-8) */
        NUMBER,
        /** Empty cell with no mines adjacent */
        EMPTY,
        /** Surprise cell with special effects */
        SURPRISE,
        /** Cell containing a question that can be opened */
        QUESTION
    }
    
    /**
     * Enum representing the current state of the cell.
     */
    public enum CellState {
        /** Cell is hidden and not yet revealed */
        HIDDEN,
        /** Cell has been revealed and shows its content */
        REVEALED,
        /** Cell has been flagged by the player */
        FLAGGED
    }
    
    private CellType type;
    private CellState state;
    private int adjacentMines; // For NUMBER cells
    private Question question; // For QUESTION cells
    private boolean questionOpened; // Track if question has been opened
    private boolean surpriseActivated; // Track if surprise has been activated
    
    /**
     * Constructs a new Cell with the specified type.
     * 
     * @param type The type of cell (MINE, NUMBER, EMPTY, SURPRISE, or QUESTION)
     */
    public Cell(CellType type) {
        this.type = type;
        this.state = CellState.HIDDEN;
        this.adjacentMines = 0;
        this.question = null;
        this.questionOpened = false;
        this.surpriseActivated = false;
    }
    
    /**
     * Gets the type of this cell.
     * 
     * @return The cell type
     */
    public CellType getType() {
        return type;
    }
    
    /**
     * Sets the type of this cell.
     * 
     * @param type The cell type to set
     */
    public void setType(CellType type) {
        this.type = type;
    }
    
    /**
     * Gets the current state of this cell.
     * 
     * @return The cell state (HIDDEN, REVEALED, or FLAGGED)
     */
    public CellState getState() {
        return state;
    }
    
    /**
     * Reveals this cell, changing its state from HIDDEN to REVEALED.
     * If the cell is already revealed or flagged, this method does nothing.
     */
    public void reveal() {
        if (state == CellState.HIDDEN) {
            state = CellState.REVEALED;
        }
    }
    
    /**
     * Toggles the flag state of this cell.
     * If hidden, it becomes flagged. If flagged, it becomes hidden.
     * Revealed cells cannot be flagged.
     */
    public void toggleFlag() {
        if (state == CellState.HIDDEN) {
            state = CellState.FLAGGED;
        } else if (state == CellState.FLAGGED) {
            state = CellState.HIDDEN;
        }
    }
    
    /**
     * Gets the number of adjacent mines (for NUMBER cells).
     * 
     * @return The number of adjacent mines (0-8)
     */
    public int getAdjacentMines() {
        return adjacentMines;
    }
    
    /**
     * Sets the number of adjacent mines (for NUMBER cells).
     * 
     * @param adjacentMines The number of adjacent mines (0-8)
     */
    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }
    
    /**
     * Gets the question associated with this cell (for QUESTION cells).
     * 
     * @return The Question object, or null if not a question cell
     */
    public Question getQuestion() {
        return question;
    }
    
    /**
     * Sets the question for this cell (for QUESTION cells).
     * 
     * @param question The Question object to associate with this cell
     */
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    /**
     * Checks if the question in this cell has been opened.
     * 
     * @return true if the question has been opened, false otherwise
     */
    public boolean isQuestionOpened() {
        return questionOpened;
    }
    
    /**
     * Marks the question in this cell as opened.
     * Once opened, a question cannot be opened again.
     */
    public void markQuestionOpened() {
        this.questionOpened = true;
    }
    
    /**
     * Checks if the surprise in this cell has been activated.
     * 
     * @return true if the surprise has been activated, false otherwise
     */
    public boolean isSurpriseActivated() {
        return surpriseActivated;
    }
    
    /**
     * Marks the surprise in this cell as activated.
     * Once activated, a surprise cannot be activated again.
     */
    public void markSurpriseActivated() {
        this.surpriseActivated = true;
    }
    
    /**
     * Checks if this cell is currently hidden.
     * 
     * @return true if the cell is hidden, false otherwise
     */
    public boolean isHidden() {
        return state == CellState.HIDDEN;
    }
    
    /**
     * Checks if this cell is currently revealed.
     * 
     * @return true if the cell is revealed, false otherwise
     */
    public boolean isRevealed() {
        return state == CellState.REVEALED;
    }
    
    /**
     * Checks if this cell is currently flagged.
     * 
     * @return true if the cell is flagged, false otherwise
     */
    public boolean isFlagged() {
        return state == CellState.FLAGGED;
    }
    
    /**
     * Gets the display value for this cell when revealed.
     * 
     * @return A string representation of the cell content:
     *         "M" for mine, number (1-8) for number cells,
     *         "?" for question cells, "S" for surprise cells,
     *         or empty string for empty cells
     */
    public String getDisplayValue() {
        if (!isRevealed()) {
            return "";
        }
        
        switch (type) {
            case MINE:
                return "M";
            case NUMBER:
                return String.valueOf(adjacentMines);
            case QUESTION:
                return "?";
            case SURPRISE:
                return "S";
            case EMPTY:
            default:
                return "";
        }
    }
}

