package model;

/**
 * Represents a cell containing a mine.
 * 
 * @author Team Sloth
 */
public class MineCell extends Cell {
    
    /**
     * Constructs a new MineCell.
     */
    public MineCell() {
        super();
    }
    
    @Override
    public CellType getType() {
        return CellType.MINE;
    }
    
    @Override
    protected String getRevealedValue() {
        return "💣";
    }
}

