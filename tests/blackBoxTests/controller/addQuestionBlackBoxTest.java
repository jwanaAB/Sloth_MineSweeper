package controller;

import model.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Black Box tests for the QuestionLogic.addQuestion() method
 * Tests functionality based on inputs/outputs without knowledge of internal structure
 * Focuses on equivalence classes and boundary conditions
 */
public class addQuestionBlackBoxTest {
    
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
     * EC6: Adding to empty list
     * EC10: List contains added question
     * Test adding question to empty list and verifying it appears
     */
    @Test
    void testAddQuestion_ToEmptyList() {
        // Arrange
        Question question = createQuestion(1, "What is 2+2?", 1, "3", "4", "5", "6", "B");
        
        // Act
        questionLogic.addQuestion(question);
        List<Question> questions = questionLogic.getQuestions();
        
        // Assert
        assertEquals(1, questions.size(), "List should contain exactly one question");
        assertTrue(questions.contains(question), "List should contain the added question");
        assertEquals(question, questions.get(0), "First question should be the added question");
    }
    
    /**
     * EC8: Adding to list with multiple questions
     * EC9: Adding multiple questions sequentially
     * EC11: List maintains order of additions
     * Test adding multiple questions and verifying all appear in order
     */
    @Test
    void testAddQuestion_MultipleQuestions() {
        // Arrange
        Question q1 = createQuestion(1, "Question 1?", 1, "A1", "B1", "C1", "D1", "A");
        Question q2 = createQuestion(2, "Question 2?", 2, "A2", "B2", "C2", "D2", "B");
        Question q3 = createQuestion(3, "Question 3?", 3, "A3", "B3", "C3", "D3", "C");
        
        // Act
        questionLogic.addQuestion(q1);
        questionLogic.addQuestion(q2);
        questionLogic.addQuestion(q3);
        List<Question> questions = questionLogic.getQuestions();
        
        // Assert
        assertEquals(3, questions.size(), "List should contain exactly three questions");
        assertEquals(q1, questions.get(0), "First question should be q1");
        assertEquals(q2, questions.get(1), "Second question should be q2");
        assertEquals(q3, questions.get(2), "Third question should be q3");
    }
    
    /**
     * EC2: Question with null fields (edge case)
     * Test that addQuestion() rejects questions with null answer options
     */
    @Test
    void testAddQuestion_WithNullFields() {
        // Arrange - Create question with null answer option fields
        Question question = new Question(1, "Test Question?", 1, null, "B1", "C1", "D1", "A");
        
        // Act & Assert - Should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.addQuestion(question),
            "addQuestion() should reject questions with null answer options");
        
        // Verify the error message
        assertTrue(exception.getMessage().contains("cannot be empty"), 
            "Error message should indicate null/empty options are not allowed");
        
        // Verify the question was NOT added
        assertEquals(0, questionLogic.getQuestions().size(), 
            "List should remain empty when null options are rejected");
    }
    
    /**
     * EC3: Question with empty strings (edge case)
     * Test that addQuestion() rejects questions with empty string answer options
     * Empty strings should be rejected because no field should be left empty
     */
    @Test
    void testAddQuestion_WithEmptyStrings() {
        // Arrange - Create question with empty string answer options
        Question question = createQuestion(1, "Test Question?", 1, "", "B1", "C1", "D1", "A");
        
        // Act & Assert - Should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.addQuestion(question),
            "addQuestion() should reject questions with empty answer options");
        
        // Verify the error message
        assertTrue(exception.getMessage().contains("cannot be empty"), 
            "Error message should indicate empty options are not allowed");
        
        // Verify the question was NOT added
        assertEquals(0, questionLogic.getQuestions().size(), 
            "List should remain empty when empty options are rejected");
    }
    
    /**
     * EC4: Question with special characters in text fields
     * Test adding question with special characters
     */
    @Test
    void testAddQuestion_WithSpecialCharacters() {
        // Arrange
        Question question = createQuestion(1, "What is 2+2? (Math)", 1, 
            "Option A: 3", "Option B: 4", "Option C: 5", "Option D: 6", "B");
        
        // Act
        questionLogic.addQuestion(question);
        List<Question> questions = questionLogic.getQuestions();
        
        // Assert
        assertEquals(1, questions.size(), "List should contain the question with special characters");
        Question result = questions.get(0);
        assertEquals("What is 2+2? (Math)", result.getQuestionText(), 
            "Question text with special characters should be preserved");
        assertEquals("Option A: 3", result.getA(), "Option A with special characters should be preserved");
    }
    
    /**
     * EC12: List size increases by 1 after each addition
     * Test that list size increases correctly after each addition
     */
    @Test
    void testAddQuestion_ListSizeIncreases() {
        // Arrange
        Question q1 = createQuestion(1, "Question 1?", 1, "A1", "B1", "C1", "D1", "A");
        Question q2 = createQuestion(2, "Question 2?", 2, "A2", "B2", "C2", "D2", "B");
        Question q3 = createQuestion(3, "Question 3?", 3, "A3", "B3", "C3", "D3", "C");
        
        // Act & Assert - Verify size increases by 1 each time
        assertEquals(0, questionLogic.getQuestions().size(), "Initial list should be empty");
        
        questionLogic.addQuestion(q1);
        assertEquals(1, questionLogic.getQuestions().size(), "List size should be 1 after first addition");
        
        questionLogic.addQuestion(q2);
        assertEquals(2, questionLogic.getQuestions().size(), "List size should be 2 after second addition");
        
        questionLogic.addQuestion(q3);
        assertEquals(3, questionLogic.getQuestions().size(), "List size should be 3 after third addition");
    }
    
    /**
     * EC5: Question with boundary difficulty values (1, 2, 3, 4)
     * Test adding questions with all valid difficulty levels
     */
    @Test
    void testAddQuestion_AllDifficultyLevels() {
        // Arrange
        Question q1 = createQuestion(1, "Easy Question?", 1, "A1", "B1", "C1", "D1", "A");
        Question q2 = createQuestion(2, "Medium Question?", 2, "A2", "B2", "C2", "D2", "B");
        Question q3 = createQuestion(3, "Hard Question?", 3, "A3", "B3", "C3", "D3", "C");
        Question q4 = createQuestion(4, "Very Hard Question?", 4, "A4", "B4", "C4", "D4", "D");
        
        // Act
        questionLogic.addQuestion(q1);
        questionLogic.addQuestion(q2);
        questionLogic.addQuestion(q3);
        questionLogic.addQuestion(q4);
        List<Question> questions = questionLogic.getQuestions();
        
        // Assert
        assertEquals(4, questions.size(), "List should contain all four questions");
        assertEquals(1, questions.get(0).getDifficulty(), "First question should have difficulty 1");
        assertEquals(2, questions.get(1).getDifficulty(), "Second question should have difficulty 2");
        assertEquals(3, questions.get(2).getDifficulty(), "Third question should have difficulty 3");
        assertEquals(4, questions.get(3).getDifficulty(), "Fourth question should have difficulty 4");
    }
    
    /**
     * EC1: Valid question with all fields populated
     * EC7: Adding to list with 1 question
     * Test adding a complete valid question and verifying all fields are preserved
     */
    @Test
    void testAddQuestion_ValidQuestionAllFields() {
        // Arrange
        Question question = createQuestion(42, "What is the capital of France?", 2, 
            "London", "Berlin", "Paris", "Madrid", "C");
        
        // Act
        questionLogic.addQuestion(question);
        List<Question> questions = questionLogic.getQuestions();
        
        // Assert
        assertEquals(1, questions.size(), "List should contain one question");
        Question result = questions.get(0);
        assertEquals(42, result.getId(), "ID should be preserved");
        assertEquals("What is the capital of France?", result.getQuestionText(), 
            "Question text should be preserved");
        assertEquals(2, result.getDifficulty(), "Difficulty should be preserved");
        assertEquals("London", result.getA(), "Option A should be preserved");
        assertEquals("Berlin", result.getB(), "Option B should be preserved");
        assertEquals("Paris", result.getC(), "Option C should be preserved");
        assertEquals("Madrid", result.getD(), "Option D should be preserved");
        assertEquals("C", result.getCorrectAnswer(), "Correct answer should be preserved");
    }
    
    /**
     * Edge case: Question with duplicate answer options (at least two identical)
     * Tests that addQuestion() rejects questions with identical answer options
     * If any two options are identical, the question is invalid - all other combinations are from the same equivalence class
     */
    @Test
    void testAddQuestion_WithDuplicateAnswerOptions() {
        // Arrange - Create question where options A and B are identical
        Question question = createQuestion(1, "What is 2+2?", 1, 
            "Four", "Four", "Five", "Six", "A");
        
        // Act & Assert - Should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.addQuestion(question),
            "addQuestion() should reject questions with duplicate answer options");
        
        // Verify the error message
        assertTrue(exception.getMessage().contains("cannot be identical"), 
            "Error message should indicate duplicate options are not allowed");
        
        // Verify the question was NOT added
        assertEquals(0, questionLogic.getQuestions().size(), 
            "List should remain empty when duplicate options are rejected");
    }
    
    /**
     * Edge case: Question with invalid difficulty
     * Tests that addQuestion() rejects questions with difficulty outside 1-4 range
     */
    @Test
    void testAddQuestion_WithInvalidDifficulty() {
        // Test difficulty less than 1
        Question questionLow = createQuestion(1, "Test Question?", 0, "A1", "B1", "C1", "D1", "A");
        IllegalArgumentException exceptionLow = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.addQuestion(questionLow),
            "addQuestion() should reject questions with difficulty < 1");
        assertTrue(exceptionLow.getMessage().contains("Difficulty must be between 1 and 4"),
            "Error message should indicate invalid difficulty range");
        
        // Test difficulty greater than 4
        Question questionHigh = createQuestion(2, "Test Question?", 5, "A2", "B2", "C2", "D2", "B");
        IllegalArgumentException exceptionHigh = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.addQuestion(questionHigh),
            "addQuestion() should reject questions with difficulty > 4");
        assertTrue(exceptionHigh.getMessage().contains("Difficulty must be between 1 and 4"),
            "Error message should indicate invalid difficulty range");
        
        // Verify no questions were added
        assertEquals(0, questionLogic.getQuestions().size(),
            "List should remain empty when invalid difficulty is rejected");
    }
    
    /**
     * Edge case: Question with invalid correct answer
     * Tests that addQuestion() rejects questions with correct answer other than A, B, C, or D
     */
    @Test
    void testAddQuestion_WithInvalidCorrectAnswer() {
        // Test with invalid correct answer "E"
        Question question1 = createQuestion(1, "Test Question?", 1, "A1", "B1", "C1", "D1", "E");
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.addQuestion(question1),
            "addQuestion() should reject questions with invalid correct answer");
        assertTrue(exception1.getMessage().contains("Correct answer must be A, B, C, or D"),
            "Error message should indicate invalid correct answer");
        
        // Test with null correct answer
        Question question2 = new Question(2, "Test Question?", 1, "A2", "B2", "C2", "D2", null);
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.addQuestion(question2),
            "addQuestion() should reject questions with null correct answer");
        assertTrue(exception2.getMessage().contains("Correct answer must be A, B, C, or D"),
            "Error message should indicate invalid correct answer");
        
        // Test with empty string correct answer
        Question question3 = createQuestion(3, "Test Question?", 1, "A3", "B3", "C3", "D3", "");
        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, 
            () -> questionLogic.addQuestion(question3),
            "addQuestion() should reject questions with empty correct answer");
        assertTrue(exception3.getMessage().contains("Correct answer must be A, B, C, or D"),
            "Error message should indicate invalid correct answer");
        
        // Verify no questions were added
        assertEquals(0, questionLogic.getQuestions().size(),
            "List should remain empty when invalid correct answer is rejected");
    }
}

