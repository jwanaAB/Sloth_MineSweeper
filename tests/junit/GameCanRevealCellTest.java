package junit;

import model.Game;
import model.EmptyCell;
import controller.QuestionLogic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class GameCanRevealCellTest {
    
    // Fixture: Shared test setup using fields
    private Game game;
    private QuestionLogic questionLogic;
    
    /**
     * Setup method that runs before each test (fixture pattern)
     * Initializes a fresh Game instance for each test to ensure independence
     */
    @BeforeEach
    public void setUp() {
        // Create QuestionLogic instance
        questionLogic = new QuestionLogic();
        
        // Create a minimal question list for game initialization
        // The game requires questions to initialize boards
        try {
            questionLogic.loadQuestionsFromCSV("resources/Questions.csv");
        } catch (Exception e) {
            // If CSV loading fails, create empty question list
            // Game will still initialize but may have issues with question cells
        }
        
        // Create a new Game instance for each test
        game = new Game("Player1", "Player2", Game.Difficulty.EASY, questionLogic);
    }
    
    /**
     * Cleanup method that runs after each test
     */
    @AfterEach
    public void tearDown() {
        // Cleanup: Set references to null to help garbage collection
        game = null;
        questionLogic = null;
    }
    
    /**
     * Unit Test: Tests the canRevealCell() method
     */
    @Test
    public void testCanRevealCell_ValidConditions_ReturnsTrue() {
        // Arrange: Game is already set up in @Before
        // currentPlayer is 1 by default, gameOver is false by default
        int row = 0;
        int col = 0;
        int player = 1; // Current player
        
        // Act: Call canRevealCell
        boolean result = game.canRevealCell(row, col, player);
        
        // Assert: Use proper assertion - assertEquals for boolean comparison
        assertEquals(true, result, 
                     "canRevealCell should return true for valid conditions");
    }
}

