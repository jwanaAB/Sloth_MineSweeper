package controller;

import model.Question;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the QuestionLogic.getNextId() method
 * Test ID: UT_GETNEXTID_001
 * 
 * Description: Unit test for getNextId() method which returns the next available ID
 * by finding the maximum ID in the questions list and adding 1.
 * 
 * Developer: [Your Name]
 */
public class getNextIdTest {
    
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
     * Test Case 1: Empty list → should return 1
     * Test ID: UT_GETNEXTID_001_TC1
     * Description: When the questions list is empty, getNextId() should return 1
     * Expected Value: 1
     * Actual Value: [will be determined at runtime]
     * Result: [pass/fail]
     */
    @Test
    void testGetNextId_EmptyList() {
        // Arrange - Empty list (default state)
        
        // Act
        int nextId = questionLogic.getNextId();
        
        // Assert
        assertEquals(1, nextId, 
            "getNextId() should return 1 when list is empty");
    }
    
    /**
     * Test Case 2: List with sequential IDs [1, 2, 3] → should return 4
     * Test ID: UT_GETNEXTID_001_TC2
     * Description: When list contains sequential IDs starting from 1, getNextId() should return max+1
     * Expected Value: 4
     * Actual Value: [will be determined at runtime]
     * Result: [pass/fail]
     */
    @Test
    void testGetNextId_SequentialIds() {
        // Arrange
        questionLogic.addQuestion(createQuestion(1, "Question 1?", 1, "A1", "B1", "C1", "D1", "A"));
        questionLogic.addQuestion(createQuestion(2, "Question 2?", 2, "A2", "B2", "C2", "D2", "B"));
        questionLogic.addQuestion(createQuestion(3, "Question 3?", 3, "A3", "B3", "C3", "D3", "C"));
        
        // Act
        int nextId = questionLogic.getNextId();
        
        // Assert
        assertEquals(4, nextId, 
            "getNextId() should return 4 when max ID is 3");
    }
    
    /**
     * Test Case 3: List with non-sequential IDs [1, 5, 10] → should return 11
     * Test ID: UT_GETNEXTID_001_TC3
     * Description: When list contains non-sequential IDs, getNextId() should return max+1
     * Expected Value: 11
     * Actual Value: [will be determined at runtime]
     * Result: [pass/fail]
     */
    @Test
    void testGetNextId_NonSequentialIds() {
        // Arrange
        questionLogic.addQuestion(createQuestion(1, "Question 1?", 1, "A1", "B1", "C1", "D1", "A"));
        questionLogic.addQuestion(createQuestion(5, "Question 5?", 2, "A2", "B2", "C2", "D2", "B"));
        questionLogic.addQuestion(createQuestion(10, "Question 10?", 3, "A3", "B3", "C3", "D3", "C"));
        
        // Act
        int nextId = questionLogic.getNextId();
        
        // Assert
        assertEquals(11, nextId, 
            "getNextId() should return 11 when max ID is 10");
    }
    
    /**
     * Test Case 4: List with single question ID=42 → should return 43
     * Test ID: UT_GETNEXTID_001_TC4
     * Description: When list contains a single question with ID 42, getNextId() should return 43
     * Expected Value: 43
     * Actual Value: [will be determined at runtime]
     * Result: [pass/fail]
     */
    @Test
    void testGetNextId_SingleQuestion() {
        // Arrange
        questionLogic.addQuestion(createQuestion(42, "Question 42?", 1, "A1", "B1", "C1", "D1", "A"));
        
        // Act
        int nextId = questionLogic.getNextId();
        
        // Assert
        assertEquals(43, nextId, 
            "getNextId() should return 43 when max ID is 42");
    }
    
    /**
     * Test Case 5: List with ID=1 only → should return 2
     * Test ID: UT_GETNEXTID_001_TC5
     * Description: When list contains only one question with ID 1, getNextId() should return 2
     * Expected Value: 2
     * Actual Value: [will be determined at runtime]
     * Result: [pass/fail]
     */
    @Test
    void testGetNextId_SingleQuestionWithIdOne() {
        // Arrange
        questionLogic.addQuestion(createQuestion(1, "Question 1?", 1, "A1", "B1", "C1", "D1", "A"));
        
        // Act
        int nextId = questionLogic.getNextId();
        
        // Assert
        assertEquals(2, nextId, 
            "getNextId() should return 2 when max ID is 1");
    }
    
    /**
     * Test Case 6: List with large ID values → should return max+1
     * Test ID: UT_GETNEXTID_001_TC6
     * Description: When list contains questions with large IDs, getNextId() should return max+1
     * Expected Value: 1001
     * Actual Value: [will be determined at runtime]
     * Result: [pass/fail]
     */
    @Test
    void testGetNextId_LargeIds() {
        // Arrange
        questionLogic.addQuestion(createQuestion(100, "Question 100?", 1, "A1", "B1", "C1", "D1", "A"));
        questionLogic.addQuestion(createQuestion(500, "Question 500?", 2, "A2", "B2", "C2", "D2", "B"));
        questionLogic.addQuestion(createQuestion(1000, "Question 1000?", 3, "A3", "B3", "C3", "D3", "C"));
        
        // Act
        int nextId = questionLogic.getNextId();
        
        // Assert
        assertEquals(1001, nextId, 
            "getNextId() should return 1001 when max ID is 1000");
    }
    
    /**
     * Test Case 7: List with unordered IDs → should return max+1
     * Test ID: UT_GETNEXTID_001_TC7
     * Description: When list contains questions with IDs in random order, getNextId() should find max and return max+1
     * Expected Value: 15
     * Actual Value: [will be determined at runtime]
     * Result: [pass/fail]
     */
    @Test
    void testGetNextId_UnorderedIds() {
        // Arrange - Add questions in non-sequential order
        questionLogic.addQuestion(createQuestion(10, "Question 10?", 1, "A1", "B1", "C1", "D1", "A"));
        questionLogic.addQuestion(createQuestion(3, "Question 3?", 2, "A2", "B2", "C2", "D2", "B"));
        questionLogic.addQuestion(createQuestion(15, "Question 15?", 3, "A3", "B3", "C3", "D3", "C"));
        questionLogic.addQuestion(createQuestion(7, "Question 7?", 1, "A4", "B4", "C4", "D4", "D"));
        
        // Act
        int nextId = questionLogic.getNextId();
        
        // Assert
        assertEquals(16, nextId, 
            "getNextId() should return 16 when max ID is 15 (regardless of order)");
    }
}


