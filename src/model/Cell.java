package model;

/**
 * Base class representing a single cell in the minesweeper gameboard.
 * Contains common state management (HIDDEN, REVEALED, FLAGGED).
 * 
 * @author Team Sloth
 */
public abstract class Cell {
    
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
    
    private CellState state;
    private boolean hasFlagScoreContributed;
    
    /**
     * Constructs a new Cell in HIDDEN state.
     */
    protected Cell() {
        this.state = CellState.HIDDEN;
        this.hasFlagScoreContributed = false;
    }
    
    /**
     * Gets the type of this cell.
     * 
     * @return The cell type
     */
    public abstract CellType getType();
    
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
     * Gets the display value for this cell.
     * This is a template method that defines the algorithm for displaying cell values.
     * The template method pattern is used here: the algorithm structure is defined in
     * the base class, while the specific step (getting the revealed value) is deferred
     * to subclasses via the getRevealedValue() method.
     * 
     * Template Method Pattern:
     * - Defines the skeleton of the algorithm (check if revealed, then get value)
     * - Allows subclasses to redefine specific steps (getRevealedValue)
     * 
     * @return A string representation of the cell content
     */
    public final String getDisplayValue() {
        // Template method: defines the algorithm structure
        // Step 1: Check if cell is revealed
        if (!isRevealed()) {
            // Common behavior: hidden cells show nothing
            return "";
        }
        // Step 2: Delegate to subclass-specific implementation
        return getRevealedValue();
    }
    
    /**
     * Gets the display value when this cell is revealed.
     * This method is part of the Template Method pattern - it represents the
     * step that varies between different cell types. Each subclass implements
     * this to provide its specific revealed value.
     * 
     * @return The string representation when the cell is revealed
     */
    protected abstract String getRevealedValue();
    
    /**
     * Checks if this cell has already contributed to the score via flagging.
     * 
     * @return true if the cell has contributed to score via flagging, false otherwise
     */
    public boolean hasFlagScoreContributed() {
        return hasFlagScoreContributed;
    }
    
    /**
     * Sets whether this cell has contributed to the score via flagging.
     * 
     * @param hasContributed true if the cell has contributed, false otherwise
     */
    public void setFlagScoreContributed(boolean hasContributed) {
        this.hasFlagScoreContributed = hasContributed;
    }
}
