package model;
/**
 * Factory class for creating Cell objects.
 * Implements the Factory Method design pattern to centralize cell creation.
 * This factory provides a single point of creation for all cell types,
 * making it easier to extend with new cell types and maintain consistent
 * cell initialization across the application.
 */
public class CellFactory {
    /**
     * Creates a new MineCell.
     * @return A new MineCell instance
     */
    public static Cell createMineCell() {
        return new MineCell();
    }
    /**
     * Creates a new EmptyCell.
     * @return A new EmptyCell instance
     */
    public static Cell createEmptyCell() {
        return new EmptyCell();
    }
       /**
     * Creates a new NumberCell with the specified adjacent mine count.
     * 
     * @param adjacentMines The number of adjacent mines (1-8)
     * @return A new NumberCell instance with the specified adjacent mine count
     */
    public static Cell createNumberCell(int adjacentMines) {
        return new NumberCell(adjacentMines);
    }
        /**
     * Creates a new QuestionCell.
     * @return A new QuestionCell instance without a question
     */
    public static Cell createQuestionCell() {
        return new QuestionCell();
    }
        /**
     * Creates a new QuestionCell with the specified question.
     * @param question The Question object to associate with this cell
     * @return A new QuestionCell instance with the specified question
     */
    public static Cell createQuestionCell(Question question) {
        return new QuestionCell(question);
    }
        /**
     * Creates a new SurpriseCell.
     * @return A new SurpriseCell instance
     */
    public static Cell createSurpriseCell() {
        return new SurpriseCell();
    }
}

