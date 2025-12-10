package model;


import model.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 *  Khalil's  Unit tests for the Question class
 */
public class QuestionTest {
    
    private Question question;
    
    @BeforeEach
    void setUp() {
        question = new Question(
            1, 
            "What is 2+2?", 
            1, 
            "3", 
            "4", 
            "5", 
            "6", 
            "B"
        );
    }
    
    @Test
    void testGetId() {
        assertEquals(1, question.getId());
    }
    
    @Test
    void testGetQuestionText() {
        assertEquals("What is 2+2?", question.getQuestionText());
    }
    
    @Test
    void testGetDifficulty() {
        assertEquals(1, question.getDifficulty());
    }
    
    @Test
    void testGetOptions() {
        assertEquals("3", question.getA());
        assertEquals("4", question.getB());
        assertEquals("5", question.getC());
        assertEquals("6", question.getD());
    }
    
    @Test
    void testGetCorrectAnswer() {
        assertEquals("B", question.getCorrectAnswer());
    }
    
    @Test
    void testSetters() {
        question.setId(2);
        question.setQuestionText("New question?");
        question.setDifficulty(2);
        question.setCorrectAnswer("A");
        
        assertEquals(2, question.getId());
        assertEquals("New question?", question.getQuestionText());
        assertEquals(2, question.getDifficulty());
        assertEquals("A", question.getCorrectAnswer());
    }
    
    @Test
    void testToString() {
        String result = question.toString();
        assertNotNull(result);
        assertTrue(result.contains("Question"));
        assertTrue(result.contains("id=1"));
    }
}

