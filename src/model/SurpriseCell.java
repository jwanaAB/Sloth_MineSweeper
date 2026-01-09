package model;

/**
 * Represents a surprise cell with special effects.
 * Inherits from EmptyCell because it cascades reveals like empty cells.
 * 
 * @author Team Sloth
 */
public class SurpriseCell extends EmptyCell {
    
    private boolean surpriseActivated;
    
    /**
     * Constructs a new SurpriseCell.
     */
    public SurpriseCell() {
        super();
        this.surpriseActivated = false;
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
    
    @Override
    public CellType getType() {
        return CellType.SURPRISE;
    }
    
    @Override
    protected String getRevealedValue() {
        return "S";
    }
}

