package junit;

import controller.QuestionLogic;
import model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for QuestionLogic.updateQuestion() method
 * Tests the scenario of updating an existing question with valid data
 */
@DisplayName("QuestionLogic UpdateQuestion Test")
public class UpdateQuestionTest {
    
    private QuestionLogic questionLogic;

    @BeforeEach
    public void setUp() {
        questionLogic = new QuestionLogic();
        
        // Add an initial question to the list for testing
        Question initialQuestion = new Question(
            1, 
            "What is 2+2?", 
            1, 
            "3", 
            "4", 
            "5", 
            "6", 
            "B"
        );
        questionLogic.addQuestion(initialQuestion);
    }
    
    @Test
    @DisplayName("Junit 2: Update existing question with valid data")
    public void testUpdateQuestion_ValidQuestionWithExistingId_QuestionUpdated() {
        // Arrange: Create an updated question with the same ID but different content
        Question updatedQuestion = new Question(
            1,                          // Same ID as initial question
            "What is 3+3?",            // Updated question text
            2,                          // Updated difficulty
            "5",                        // Updated option A
            "6",                        // Updated option B
            "7",                        // Updated option C
            "8",                        // Updated option D
            "B"                         // Updated correct answer
        );
        
        // Verify initial state: question exists with original data
        Question originalQuestion = questionLogic.getQuestions().get(0);
        assertEquals(1, originalQuestion.getId(), "Initial question should have ID=1");
        assertEquals("What is 2+2?", originalQuestion.getQuestionText(), "Initial question text should match");
        assertEquals(1, originalQuestion.getDifficulty(), "Initial difficulty should be 1");
        assertEquals("4", originalQuestion.getB(), "Initial option B should be '4'");
        
        // Act: Update the question
        questionLogic.updateQuestion(updatedQuestion);
        
        // Assert: Verify the question was updated correctly
        Question resultQuestion = questionLogic.getQuestions().get(0);
        
        // Verify ID remains the same
        assertEquals(1, resultQuestion.getId(), "Question ID should remain 1");
        
        // Verify all fields were updated
        assertEquals("What is 3+3?", resultQuestion.getQuestionText(), "Question text should be updated to 'What is 3+3?'");
        assertEquals(2, resultQuestion.getDifficulty(), "Difficulty should be updated to 2");
        assertEquals("5", resultQuestion.getA(), "Option A should be updated to '5'");
        assertEquals("6", resultQuestion.getB(), "Option B should be updated to '6'");
        assertEquals("7", resultQuestion.getC(), "Option C should be updated to '7'");
        assertEquals("8", resultQuestion.getD(), "Option D should be updated to '8'");
        assertEquals("B", resultQuestion.getCorrectAnswer(), "Correct answer should be updated to 'B'");
        
        // Verify only one question exists in the list (no duplicates)
        assertEquals(1, questionLogic.getQuestions().size(), "List should contain exactly one question after update");
        
        // Verify the updated question is the same object reference (replaced, not added)
        assertSame(updatedQuestion, resultQuestion, 
            "The question object should be replaced, not added");
    }
}