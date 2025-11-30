package model;

/**
 * Represents a cell containing a question that can be opened.
 * Inherits from EmptyCell because it cascades reveals like empty cells.
 * 
 * @author Team Sloth
 */
public class QuestionCell extends EmptyCell {
    
    private Question question;
    private boolean questionOpened;
    
    /**
     * Constructs a new QuestionCell.
     */
    public QuestionCell() {
        super();
        this.question = null;
        this.questionOpened = false;
    }
    
    /**
     * Constructs a new QuestionCell with a question.
     * 
     * @param question The Question object to associate with this cell
     */
    public QuestionCell(Question question) {
        super();
        this.question = question;
        this.questionOpened = false;
    }
    
    /**
     * Gets the question associated with this cell.
     * 
     * @return The Question object, or null if not set
     */
    public Question getQuestion() {
        return question;
    }
    
    /**
     * Sets the question for this cell.
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
    
    @Override
    public CellType getType() {
        return CellType.QUESTION;
    }
    
    @Override
    public String getDisplayValue() {
        if (!isRevealed()) {
            return "";
        }
        return "?";
    }
}

