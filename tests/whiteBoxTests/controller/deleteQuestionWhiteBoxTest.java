package whiteBoxTests.controller;

import model.Question;
import org.junit.jupiter.api.Test;

import controller.QuestionLogic;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White Box tests for the QuestionLogic.deleteQuestion() method
 * Test ID: WB_DELETE_001
 * Tests all code paths and branches in the deleteQuestion method
 * 
 * Code Segment Being Tested:
 * public void deleteQuestion(int questionId) {
 *     questions.removeIf(q -> q.getId() == questionId);
 * }
 * 
 * Coverage Type: Statement coverage (כיסוי פקודות)
 * 
 * Control Flow Graph:
 * [Start] → [questions.removeIf(lambda)] → [End]
 * 
 * The removeIf method internally iterates through the list:
 * - If list is empty: no iteration occurs
 * - If list has elements: iterates through each, removes matching IDs
 * 
 * Developer: [Your Name]
 */
public class deleteQuestionWhiteBoxTest {
    
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
     * Path 1: Empty list → removeIf doesn't iterate → no deletion
     * Test deleting from empty list
     * Graph Path: [Start] → [removeIf - no iteration] → [End]
     */
    @Test
    void testDeleteQuestion_EmptyList() {
        // Arrange
        int questionId = 1;
        
        // Act
        questionLogic.deleteQuestion(questionId);
        
        // Assert
        assertEquals(0, questionLogic.getQuestions().size(), 
            "List should remain empty when deleting from empty list");
    }
    
    /**
     * Path 2: List with questions, ID found at first position → removal occurs
     * Test deleting question at index 0
     * Graph Path: [Start] → [removeIf - iterate, find match at index 0] → [remove] → [End]
     */
    @Test
    void testDeleteQuestion_FoundAtFirstPosition() {
        // Arrange
        Question q1 = createQuestion(1, "First Question?", 1, "A1", "B1", "C1", "D1", "A");
        Question q2 = createQuestion(2, "Second Question?", 2, "A2", "B2", "C2", "D2", "B");
        questionLogic.addQuestion(q1);
        questionLogic.addQuestion(q2);
        
        // Act
        questionLogic.deleteQuestion(1);
        
        // Assert
        assertEquals(1, questionLogic.getQuestions().size(), 
            "List should contain one question after deletion");
        assertEquals(2, questionLogic.getQuestions().get(0).getId(), 
            "Remaining question should have ID 2");
        assertFalse(questionLogic.getQuestions().contains(q1), 
            "Question with ID 1 should be removed");
    }
    
    /**
     * Path 3: List with questions, ID found at middle position → removal occurs
     * Test deleting question at middle index
     * Graph Path: [Start] → [removeIf - iterate, find match at middle] → [remove] → [End]
     */
    @Test
    void testDeleteQuestion_FoundAtMiddlePosition() {
        // Arrange
        questionLogic.addQuestion(createQuestion(1, "First Question?", 1, "A1", "B1", "C1", "D1", "A"));
        Question q2 = createQuestion(2, "Middle Question?", 2, "A2", "B2", "C2", "D2", "B");
        questionLogic.addQuestion(q2);
        questionLogic.addQuestion(createQuestion(3, "Last Question?", 3, "A3", "B3", "C3", "D3", "C"));
        
        // Act
        questionLogic.deleteQuestion(2);
        
        // Assert
        assertEquals(2, questionLogic.getQuestions().size(), 
            "List should contain two questions after deletion");
        assertEquals(1, questionLogic.getQuestions().get(0).getId(), 
            "First question should remain");
        assertEquals(3, questionLogic.getQuestions().get(1).getId(), 
            "Last question should remain");
        assertFalse(questionLogic.getQuestions().contains(q2), 
            "Question with ID 2 should be removed");
    }
    
    /**
     * Path 4: List with questions, ID found at last position → removal occurs
     * Test deleting question at last index
     * Graph Path: [Start] → [removeIf - iterate, find match at end] → [remove] → [End]
     */
    @Test
    void testDeleteQuestion_FoundAtLastPosition() {
        // Arrange
        questionLogic.addQuestion(createQuestion(1, "First Question?", 1, "A1", "B1", "C1", "D1", "A"));
        questionLogic.addQuestion(createQuestion(2, "Second Question?", 2, "A2", "B2", "C2", "D2", "B"));
        Question q3 = createQuestion(3, "Last Question?", 3, "A3", "B3", "C3", "D3", "C");
        questionLogic.addQuestion(q3);
        
        // Act
        questionLogic.deleteQuestion(3);
        
        // Assert
        assertEquals(2, questionLogic.getQuestions().size(), 
            "List should contain two questions after deletion");
        assertEquals(1, questionLogic.getQuestions().get(0).getId(), 
            "First question should remain");
        assertEquals(2, questionLogic.getQuestions().get(1).getId(), 
            "Second question should remain");
        assertFalse(questionLogic.getQuestions().contains(q3), 
            "Question with ID 3 should be removed");
    }
    
    /**
     * Path 5: List with questions, ID not found → no removal
     * Test deleting non-existing question ID
     * Graph Path: [Start] → [removeIf - iterate, no match] → [End]
     */
    @Test
    void testDeleteQuestion_NotFound() {
        // Arrange
        questionLogic.addQuestion(createQuestion(1, "Question 1?", 1, "A1", "B1", "C1", "D1", "A"));
        questionLogic.addQuestion(createQuestion(2, "Question 2?", 2, "A2", "B2", "C2", "D2", "B"));
        questionLogic.addQuestion(createQuestion(3, "Question 3?", 3, "A3", "B3", "C3", "D3", "C"));
        int initialSize = questionLogic.getQuestions().size();
        
        // Act
        questionLogic.deleteQuestion(99);
        
        // Assert
        assertEquals(initialSize, questionLogic.getQuestions().size(), 
            "List size should remain unchanged when deleting non-existing ID");
        assertEquals(1, questionLogic.getQuestions().get(0).getId(), 
            "First question should remain");
        assertEquals(2, questionLogic.getQuestions().get(1).getId(), 
            "Second question should remain");
        assertEquals(3, questionLogic.getQuestions().get(2).getId(), 
            "Third question should remain");
    }
    
    /**
     * Path 6: List with multiple questions having same ID → all matching IDs removed
     * Test deleting when multiple questions have the same ID (edge case)
     * Graph Path: [Start] → [removeIf - iterate, find multiple matches] → [remove all] → [End]
     * Note: removeIf removes ALL elements matching the predicate
     */
    @Test
    void testDeleteQuestion_MultipleQuestionsWithSameId() {
        // Arrange - Create questions with same ID (edge case scenario)
        Question q1 = createQuestion(1, "First with ID 1?", 1, "A1", "B1", "C1", "D1", "A");
        Question q2 = createQuestion(1, "Second with ID 1?", 2, "A2", "B2", "C2", "D2", "B");
        Question q3 = createQuestion(2, "Question with ID 2?", 3, "A3", "B3", "C3", "D3", "C");
        
        // Manually add to list to simulate edge case
        questionLogic.addQuestion(q1);
        questionLogic.addQuestion(q2);
        questionLogic.addQuestion(q3);
        
        // Act
        questionLogic.deleteQuestion(1);
        
        // Assert - All questions with ID 1 should be removed
        assertEquals(1, questionLogic.getQuestions().size(), 
            "List should contain one question after deleting all with ID 1");
        assertEquals(2, questionLogic.getQuestions().get(0).getId(), 
            "Only question with ID 2 should remain");
        assertFalse(questionLogic.getQuestions().contains(q1), 
            "First question with ID 1 should be removed");
        assertFalse(questionLogic.getQuestions().contains(q2), 
            "Second question with ID 1 should be removed");
    }
    
    /**
     * Path 7: List with single question, ID matches → removal occurs, list becomes empty
     * Test deleting the only question in list
     * Graph Path: [Start] → [removeIf - iterate, find match] → [remove] → [End]
     */
    @Test
    void testDeleteQuestion_SingleQuestionList() {
        // Arrange
        Question q1 = createQuestion(1, "Only Question?", 1, "A1", "B1", "C1", "D1", "A");
        questionLogic.addQuestion(q1);
        
        // Act
        questionLogic.deleteQuestion(1);
        
        // Assert
        assertEquals(0, questionLogic.getQuestions().size(), 
            "List should be empty after deleting the only question");
        assertFalse(questionLogic.getQuestions().contains(q1), 
            "Question should be removed");
    }
}



