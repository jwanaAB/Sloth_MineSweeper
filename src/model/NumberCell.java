package model;

/**
 * Represents a cell showing the number of adjacent mines (1-8).
 * 
 * @author Team Sloth
 */
public class NumberCell extends Cell {
    
    private int adjacentMines;
    
    /**
     * Constructs a new NumberCell with the specified adjacent mine count.
     * 
     * @param adjacentMines The number of adjacent mines (1-8)
     */
    public NumberCell(int adjacentMines) {
        super();
        this.adjacentMines = adjacentMines;
    }
    
    /**
     * Gets the number of adjacent mines.
     * 
     * @return The number of adjacent mines (1-8)
     */
    public int getAdjacentMines() {
        return adjacentMines;
    }
    
    /**
     * Sets the number of adjacent mines.
     * 
     * @param adjacentMines The number of adjacent mines (1-8)
     */
    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }
    
    @Override
    public CellType getType() {
        return CellType.NUMBER;
    }
    
    @Override
    protected String getRevealedValue() {
        return String.valueOf(adjacentMines);
    }
}

