package model;

/**
 * Represents an empty cell with no mines adjacent.
 * 
 * @author Team Sloth
 */
public class EmptyCell extends Cell {
    
    /**
     * Constructs a new EmptyCell.
     */
    public EmptyCell() {
        super();
    }
    
    @Override
    public CellType getType() {
        return CellType.EMPTY;
    }
    
    @Override
    protected String getRevealedValue() {
        return "";
    }
}

