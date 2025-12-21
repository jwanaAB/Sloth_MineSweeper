package junit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.QuestionLogic;
import model.Game;

public class DecreaseSharedLivesTest {

    // Fixture: Shared test setup using fields
    private Game game;
    private QuestionLogic questionLogic;

    /**
     * Setup method that runs before each test (fixture pattern)
     * Initializes a fresh Game instance for each test to ensure independence
     */
    @BeforeEach
    public void setUp() {
        // Create QuestionLogic instance (Game needs it to initialize boards)
        questionLogic = new QuestionLogic();

        // Try loading questions (tests for decreaseSharedLives don't depend on questions,
        // but Game constructor calls initializeBoards which accesses the question list)
        try {
            questionLogic.loadQuestionsFromCSV("resources/Questions.csv");
        } catch (Exception e) {
            // If loading fails, we still continue (assuming QuestionLogic returns an empty list)
        }

        // Create a new Game instance for each test
        game = new Game("Player1", "Player2", Game.Difficulty.EASY, questionLogic);
    }

    /**
     * Cleanup method that runs after each test
     */
    @AfterEach
    public void tearDown() {
        game = null;
        questionLogic = null;
    }

    /**
     * Boundary Test: sharedLives = 1
     * After decreaseSharedLives():
     * - should return true (game over)
     * - sharedLives becomes 0
     * - gameOver becomes true
     * - winner becomes 0
     */
    @Test
    public void testDecreaseSharedLives_LastLife_ReturnsTrueAndGameOver() {
        // Arrange
        game.setSharedLives(1);
        game.setGameOver(false); // ensure clean precondition

        // Act
        boolean result = game.decreaseSharedLives();

        // Assert
        assertTrue(result, "Expected true when shared lives run out (game over).");
        assertEquals(0, game.getSharedLives(), "Expected sharedLives to decrease from 1 to 0.");
        assertTrue(game.isGameOver(), "Expected gameOver to be true when sharedLives <= 0.");
        assertEquals(0, game.getWinner(), "Expected winner to be 0 when lives run out (draw/loss).");
    }

    /**
     * Normal Test: sharedLives > 1
     * After decreaseSharedLives():
     * - should return false
     * - sharedLives decreases by 1
     * - gameOver remains false
     */
    @Test
    public void testDecreaseSharedLives_MoreThanOneLife_ReturnsFalseAndGameContinues() {
        // Arrange
        game.setSharedLives(3);
        game.setGameOver(false);

        // Act
        boolean result = game.decreaseSharedLives();

        // Assert
        assertFalse(result, "Expected false when shared lives still remain.");
        assertEquals(2, game.getSharedLives(), "Expected sharedLives to decrease from 3 to 2.");
        assertFalse(game.isGameOver(), "Expected gameOver to remain false when sharedLives > 0.");
    }
}
