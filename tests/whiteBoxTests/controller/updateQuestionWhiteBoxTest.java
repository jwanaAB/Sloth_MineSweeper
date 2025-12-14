package controller;

import model.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White Box tests for the QuestionLogic.updateQuestion() method
 * Tests all code paths and branches in the updateQuestion method
 */
public class updateQuestionWhiteBoxTest {
    
    private QuestionLogic questionLogic;
    
    @BeforeEach
    void setUp() {
        questionLogic = new QuestionLogic();
    }
    
    /**
     * Helper method to create a test question
     */
    private Question createQuestion(int id, String questionText, int difficulty, 
                                   String a, String b, String c, String d, String correctAnswer) {
        return new Question(id, questionText, difficulty, a, b, c, d, correctAnswer);
    }
    
    /**
     * Path 1: Empty list → loop doesn't execute → no update
     * Test updating when questions list is empty
     */
    @Test
    void testUpdateQuestion_EmptyList() {
        // Arrange
        Question updatedQuestion = createQuestion(1, "New Question?", 1, "A1", "B1", "C1", "D1", "A");
        
        // Act
        questionLogic.updateQuestion(updatedQuestion);
        
        // Assert
        assertEquals(0, questionLogic.getQuestions().size(), 
            "List should remain empty when updating in empty list");
    }
    
    /**
     * Path 2: Question found at first position → condition true → update → break
     * Test updating question at index 0
     */
    @Test
    void testUpdateQuestion_FoundAtFirstPosition() {
        // Arrange
        Question original = createQuestion(1, "Original Question?", 1, "A1", "B1", "C1", "D1", "A");
        questionLogic.addQuestion(original);
        questionLogic.addQuestion(createQuestion(2, "Second Question?", 2, "A2", "B2", "C2", "D2", "B"));
        
        Question updated = createQuestion(1, "Updated Question?", 3, "A3", "B3", "C3", "D3", "C");
        
        // Act
        questionLogic.updateQuestion(updated);
        
        // Assert
        Question result = questionLogic.getQuestions().get(0);
        assertEquals(1, result.getId(), "ID should remain 1");
        assertEquals("Updated Question?", result.getQuestionText(), "Question text should be updated");
        assertEquals(3, result.getDifficulty(), "Difficulty should be updated");
        assertEquals("C", result.getCorrectAnswer(), "Correct answer should be updated");
        assertEquals(2, questionLogic.getQuestions().size(), "List size should remain 2");
    }
    
    /**
     * Path 3: Question found at middle position → loop continues → condition true → update → break
     * Test updating question at middle index
     */
    @Test
    void testUpdateQuestion_FoundAtMiddlePosition() {
        // Arrange
        questionLogic.addQuestion(createQuestion(1, "First Question?", 1, "A1", "B1", "C1", "D1", "A"));
        Question original = createQuestion(2, "Middle Question?", 2, "A2", "B2", "C2", "D2", "B");
        questionLogic.addQuestion(original);
        questionLogic.addQuestion(createQuestion(3, "Last Question?", 3, "A3", "B3", "C3", "D3", "C"));
        
        Question updated = createQuestion(2, "Updated Middle Question?", 4, "A4", "B4", "C4", "D4", "D");
        
        // Act
        questionLogic.updateQuestion(updated);
        
        // Assert
        Question result = questionLogic.getQuestions().get(1);
        assertEquals(2, result.getId(), "ID should remain 2");
        assertEquals("Updated Middle Question?", result.getQuestionText(), "Question text should be updated");
        assertEquals(4, result.getDifficulty(), "Difficulty should be updated");
        assertEquals("D", result.getCorrectAnswer(), "Correct answer should be updated");
        assertEquals(3, questionLogic.getQuestions().size(), "List size should remain 3");
        
        // Verify other questions are unchanged
        assertEquals(1, questionLogic.getQuestions().get(0).getId(), "First question should be unchanged");
        assertEquals(3, questionLogic.getQuestions().get(2).getId(), "Last question should be unchanged");
    }
    
    /**
     * Path 4: Question found at last position → loop continues → condition true → update → break
     * Test updating question at last index
     */
    @Test
    void testUpdateQuestion_FoundAtLastPosition() {
        // Arrange
        questionLogic.addQuestion(createQuestion(1, "First Question?", 1, "A1", "B1", "C1", "D1", "A"));
        questionLogic.addQuestion(createQuestion(2, "Second Question?", 2, "A2", "B2", "C2", "D2", "B"));
        Question original = createQuestion(3, "Last Question?", 3, "A3", "B3", "C3", "D3", "C");
        questionLogic.addQuestion(original);
        
        Question updated = createQuestion(3, "Updated Last Question?", 4, "A4", "B4", "C4", "D4", "D");
        
        // Act
        questionLogic.updateQuestion(updated);
        
        // Assert
        Question result = questionLogic.getQuestions().get(2);
        assertEquals(3, result.getId(), "ID should remain 3");
        assertEquals("Updated Last Question?", result.getQuestionText(), "Question text should be updated");
        assertEquals(4, result.getDifficulty(), "Difficulty should be updated");
        assertEquals("D", result.getCorrectAnswer(), "Correct answer should be updated");
        assertEquals(3, questionLogic.getQuestions().size(), "List size should remain 3");
        
        // Verify other questions are unchanged
        assertEquals(1, questionLogic.getQuestions().get(0).getId(), "First question should be unchanged");
        assertEquals(2, questionLogic.getQuestions().get(1).getId(), "Second question should be unchanged");
    }
    
    /**
     * Path 5: Question not found → loop completes → condition always false → no update
     * Test updating non-existing question ID
     */
    @Test
    void testUpdateQuestion_NotFound() {
        // Arrange
        questionLogic.addQuestion(createQuestion(1, "Question 1?", 1, "A1", "B1", "C1", "D1", "A"));
        questionLogic.addQuestion(createQuestion(2, "Question 2?", 2, "A2", "B2", "C2", "D2", "B"));
        questionLogic.addQuestion(createQuestion(3, "Question 3?", 3, "A3", "B3", "C3", "D3", "C"));
        
        Question updated = createQuestion(99, "Non-existing Question?", 4, "A4", "B4", "C4", "D4", "D");
        
        // Act
        questionLogic.updateQuestion(updated);
        
        // Assert
        assertEquals(3, questionLogic.getQuestions().size(), "List size should remain 3");
        assertEquals("Question 1?", questionLogic.getQuestions().get(0).getQuestionText(), 
            "First question should be unchanged");
        assertEquals("Question 2?", questionLogic.getQuestions().get(1).getQuestionText(), 
            "Second question should be unchanged");
        assertEquals("Question 3?", questionLogic.getQuestions().get(2).getQuestionText(), 
            "Third question should be unchanged");
    }
    
    /**
     * Edge case: Multiple questions with same ID (should update first match)
     * Tests that break statement stops after first match
     */
    @Test
    void testUpdateQuestion_MultipleQuestionsWithSameId() {
        // Arrange - Create questions with same ID (edge case scenario)
        Question first = createQuestion(1, "First with ID 1?", 1, "A1", "B1", "C1", "D1", "A");
        Question second = createQuestion(1, "Second with ID 1?", 2, "A2", "B2", "C2", "D2", "B");
        
        // Manually add to list to simulate edge case
        questionLogic.addQuestion(first);
        questionLogic.addQuestion(second);
        
        Question updated = createQuestion(1, "Updated First Match?", 3, "A3", "B3", "C3", "D3", "C");
        
        // Act
        questionLogic.updateQuestion(updated);
        
        // Assert - Only first match should be updated due to break statement
        Question result = questionLogic.getQuestions().get(0);
        assertEquals(1, result.getId(), "ID should remain 1");
        assertEquals("Updated First Match?", result.getQuestionText(), 
            "First question with ID 1 should be updated");
        
        // Second question with same ID should remain unchanged
        Question secondResult = questionLogic.getQuestions().get(1);
        assertEquals(1, secondResult.getId(), "ID should remain 1");
        assertEquals("Second with ID 1?", secondResult.getQuestionText(), 
            "Second question with ID 1 should NOT be updated (break stops loop)");
    }
    
    /**
     * Edge case: Question with duplicate answer options
     * Tests that updateQuestion() rejects questions with identical answer options
     * Validation happens before the loop, so this tests the validation path
     */
    @Test
    void testUpdateQuestion_WithDuplicateAnswerOptions() {
        // Arrange
        Question original = createQuestion(1, "Original Question?", 1, "Option1", "Option2", "Option3", "Option4", "A");
        questionLogic.addQuestion(original);
        
        // Create updated question with duplicate options (A and B are identical)
        Question updated = createQuestion(1, "Updated Question?", 2, "Same", "Same", "Different", "Another", "A");
        
        // Act & Assert - Should throw IllegalArgumentException before updating
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.updateQuestion(updated),
            "updateQuestion() should reject questions with duplicate answer options");
        
        // Verify the error message
        assertTrue(exception.getMessage().contains("cannot be identical"), 
            "Error message should indicate duplicate options are not allowed");
        
        // Verify the original question was NOT updated
        Question result = questionLogic.getQuestions().get(0);
        assertEquals("Original Question?", result.getQuestionText(), 
            "Original question should remain unchanged when update is rejected");
    }
    
    /**
     * Edge case: Question with invalid difficulty
     * Tests that updateQuestion() rejects questions with difficulty outside 1-4 range
     * Validation happens before the loop, so this tests the validation path
     */
    @Test
    void testUpdateQuestion_WithInvalidDifficulty() {
        // Arrange
        Question original = createQuestion(1, "Original Question?", 1, "Option1", "Option2", "Option3", "Option4", "A");
        questionLogic.addQuestion(original);
        
        // Test difficulty less than 1
        Question updatedLow = createQuestion(1, "Updated Question?", 0, "A1", "B1", "C1", "D1", "A");
        IllegalArgumentException exceptionLow = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.updateQuestion(updatedLow),
            "updateQuestion() should reject questions with difficulty < 1");
        assertTrue(exceptionLow.getMessage().contains("Difficulty must be between 1 and 4"),
            "Error message should indicate invalid difficulty range");
        
        // Test difficulty greater than 4
        Question updatedHigh = createQuestion(1, "Updated Question?", 5, "A2", "B2", "C2", "D2", "B");
        IllegalArgumentException exceptionHigh = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.updateQuestion(updatedHigh),
            "updateQuestion() should reject questions with difficulty > 4");
        assertTrue(exceptionHigh.getMessage().contains("Difficulty must be between 1 and 4"),
            "Error message should indicate invalid difficulty range");
        
        // Verify the original question was NOT updated
        Question result = questionLogic.getQuestions().get(0);
        assertEquals("Original Question?", result.getQuestionText(), 
            "Original question should remain unchanged when update is rejected");
        assertEquals(1, result.getDifficulty(), 
            "Original difficulty should remain unchanged when update is rejected");
    }
    
    /**
     * Edge case: Question with invalid correct answer
     * Tests that updateQuestion() rejects questions with correct answer other than A, B, C, or D
     * Validation happens before the loop, so this tests the validation path
     */
    @Test
    void testUpdateQuestion_WithInvalidCorrectAnswer() {
        // Arrange
        Question original = createQuestion(1, "Original Question?", 1, "Option1", "Option2", "Option3", "Option4", "A");
        questionLogic.addQuestion(original);
        
        // Test with invalid correct answer "E"
        Question updated1 = createQuestion(1, "Updated Question?", 2, "A1", "B1", "C1", "D1", "E");
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.updateQuestion(updated1),
            "updateQuestion() should reject questions with invalid correct answer");
        assertTrue(exception1.getMessage().contains("Correct answer must be A, B, C, or D"),
            "Error message should indicate invalid correct answer");
        
        // Test with null correct answer
        Question updated2 = new Question(1, "Updated Question?", 2, "A2", "B2", "C2", "D2", null);
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.updateQuestion(updated2),
            "updateQuestion() should reject questions with null correct answer");
        assertTrue(exception2.getMessage().contains("Correct answer must be A, B, C, or D"),
            "Error message should indicate invalid correct answer");
        
        // Test with empty string correct answer
        Question updated3 = createQuestion(1, "Updated Question?", 2, "A3", "B3", "C3", "D3", "");
        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.updateQuestion(updated3),
            "updateQuestion() should reject questions with empty correct answer");
        assertTrue(exception3.getMessage().contains("Correct answer must be A, B, C, or D"),
            "Error message should indicate invalid correct answer");
        
        // Verify the original question was NOT updated
        Question result = questionLogic.getQuestions().get(0);
        assertEquals("Original Question?", result.getQuestionText(), 
            "Original question should remain unchanged when update is rejected");
        assertEquals("A", result.getCorrectAnswer(), 
            "Original correct answer should remain unchanged when update is rejected");
    }
}

