package blackBoxTests.controller;

import model.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import controller.QuestionLogic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Black Box tests for the QuestionLogic.importQuestionsFromCSV() method
 * Test ID: BB_IMPORT_001
 * Tests functionality based on inputs/outputs without knowledge of internal structure
 * Focuses on equivalence classes and boundary conditions
 * 
 * Requirement: 
 * The importQuestionsFromCSV method should import questions from a CSV file.
 * It should validate the CSV structure (header and 8 columns per row),
 * validate question data (difficulty 1-4, correct answer A/B/C/D, non-empty question text),
 * skip invalid rows and report errors, and generate new IDs to avoid conflicts.
 * 
 * Equivalence Classes:
 * Valid ECs:
 * - EC1: Valid CSV with correct header and valid questions
 * - EC2: Valid CSV with some invalid rows (should skip invalid, import valid)
 * - EC3: CSV with empty lines (should skip)
 * 
 * Invalid ECs:
 * - EC_INV1: File doesn't exist
 * - EC_INV2: Invalid header format
 * - EC_INV3: CSV with insufficient columns (<8)
 * - EC_INV4: CSV with invalid difficulty values (<1 or >4)
 * - EC_INV5: CSV with invalid correct answer (not A/B/C/D)
 * - EC_INV6: CSV with empty question text
 * - EC_INV7: CSV with invalid number format (non-numeric ID or difficulty)
 * 
 * Developer: [Your Name]
 */
public class importQuestionsFromCSVBlackBoxTest {
    
    private QuestionLogic questionLogic;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        questionLogic = new QuestionLogic();
    }
    
    /**
     * Helper method to create a test CSV file
     */
    private File createTestCSV(String content) throws IOException {
        File csvFile = tempDir.resolve("test_questions.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(content);
        }
        return csvFile;
    }
    
    /**
     * EC1: Valid CSV with correct header and valid questions
     * Test importing valid CSV file with proper format
     * Input: Valid CSV with header and valid questions
     * Expected Output: All questions imported successfully, importedCount > 0, skippedCount = 0
     * Equivalence Class: EC1
     */
    @Test
    void testImportQuestionsFromCSV_ValidCSV() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "1,What is 2+2?,1,3,4,5,6,B\n" +
                           "2,What is the capital of France?,2,London,Berlin,Paris,Madrid,C\n" +
                           "3,What is 5*5?,1,20,25,30,35,B";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(3, result.getImportedCount(), 
            "All 3 valid questions should be imported");
        assertEquals(0, result.getSkippedCount(), 
            "No questions should be skipped for valid CSV");
        assertTrue(result.getErrors().isEmpty(), 
            "No errors should be reported for valid CSV");
        assertEquals(3, questionLogic.getQuestions().size(), 
            "Questions list should contain 3 questions");
    }
    
    /**
     * EC2: Valid CSV with some invalid rows (should skip invalid, import valid)
     * Test importing CSV with mixed valid and invalid rows
     * Input: CSV with valid header, some valid questions, some invalid
     * Expected Output: Valid questions imported, invalid skipped, errors reported
     * Equivalence Class: EC2
     */
    @Test
    void testImportQuestionsFromCSV_MixedValidAndInvalid() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "1,Valid Question 1?,1,Opt1,Opt2,Opt3,Opt4,A\n" +
                           "2,Invalid Difficulty?,5,Opt1,Opt2,Opt3,Opt4,B\n" +
                           "3,Valid Question 2?,2,Opt1,Opt2,Opt3,Opt4,C\n" +
                           "4,Invalid Answer?,1,Opt1,Opt2,Opt3,Opt4,E";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(2, result.getImportedCount(), 
            "2 valid questions should be imported");
        assertEquals(2, result.getSkippedCount(), 
            "2 invalid questions should be skipped");
        assertEquals(2, result.getErrors().size(), 
            "2 errors should be reported");
        assertEquals(2, questionLogic.getQuestions().size(), 
            "Questions list should contain 2 valid questions");
    }
    
    /**
     * EC3: CSV with empty lines (should skip)
     * Test importing CSV with empty lines between questions
     * Input: CSV with header, valid questions, and empty lines
     * Expected Output: Valid questions imported, empty lines skipped
     * Equivalence Class: EC3
     */
    @Test
    void testImportQuestionsFromCSV_WithEmptyLines() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "1,Question 1?,1,Opt1,Opt2,Opt3,Opt4,A\n" +
                           "\n" +
                           "2,Question 2?,2,Opt1,Opt2,Opt3,Opt4,B\n" +
                           "\n" +
                           "3,Question 3?,3,Opt1,Opt2,Opt3,Opt4,C";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(3, result.getImportedCount(), 
            "All 3 valid questions should be imported (empty lines skipped)");
        assertEquals(0, result.getSkippedCount(), 
            "No questions should be skipped");
        assertEquals(3, questionLogic.getQuestions().size(), 
            "Questions list should contain 3 questions");
    }
    
    /**
     * EC_INV1: File doesn't exist
     * Test importing from non-existent file
     * Input: File path to non-existent file
     * Expected Output: Exception thrown or error in ImportResult
     * Equivalence Class: EC_INV1
     */
    @Test
    void testImportQuestionsFromCSV_FileNotFound() {
        // Arrange
        File nonExistentFile = new File(tempDir.toFile(), "non_existent.csv");
        
        // Act & Assert
        assertThrows(Exception.class, 
            () -> questionLogic.importQuestionsFromCSV(nonExistentFile),
            "Should throw exception when file doesn't exist");
    }
    
    /**
     * EC_INV2: Invalid header format
     * Test importing CSV with incorrect header
     * Input: CSV with wrong header format
     * Expected Output: Error reported in ImportResult, but processing continues
     * Equivalence Class: EC_INV2
     */
    @Test
    void testImportQuestionsFromCSV_InvalidHeader() throws Exception {
        // Arrange
        String csvContent = "Wrong,Header,Format\n" +
                           "1,Question 1?,1,Opt1,Opt2,Opt3,Opt4,A";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertFalse(result.getErrors().isEmpty(), 
            "Error should be reported for invalid header");
        assertTrue(result.getErrors().get(0).contains("Header does not match expected format"), 
            "Error message should mention header format");
        // Note: The method continues processing even with invalid header
    }
    
    /**
     * EC_INV3: CSV with insufficient columns (<8)
     * Test importing CSV with rows having less than 8 columns
     * Input: CSV with row having only 5 columns
     * Expected Output: Row skipped, error reported
     * Equivalence Class: EC_INV3
     */
    @Test
    void testImportQuestionsFromCSV_InsufficientColumns() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "1,Question 1?,1,Opt1,Opt2,Opt3\n" +
                           "2,Question 2?,2,Opt1,Opt2,Opt3,Opt4,B";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(1, result.getImportedCount(), 
            "1 valid question should be imported");
        assertEquals(1, result.getSkippedCount(), 
            "1 question with insufficient columns should be skipped");
        assertTrue(result.getErrors().get(0).contains("Insufficient columns"), 
            "Error should mention insufficient columns");
    }
    
    /**
     * EC_INV4: CSV with invalid difficulty values (<1 or >4)
     * Test importing CSV with difficulty outside valid range
     * Input: CSV with difficulty = 0 and difficulty = 5
     * Expected Output: Rows skipped, errors reported
     * Equivalence Class: EC_INV4
     */
    @Test
    void testImportQuestionsFromCSV_InvalidDifficulty() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "1,Question with difficulty 0?,0,Opt1,Opt2,Opt3,Opt4,A\n" +
                           "2,Question with difficulty 5?,5,Opt1,Opt2,Opt3,Opt4,B\n" +
                           "3,Valid Question?,2,Opt1,Opt2,Opt3,Opt4,C";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(1, result.getImportedCount(), 
            "1 valid question should be imported");
        assertEquals(2, result.getSkippedCount(), 
            "2 questions with invalid difficulty should be skipped");
        assertEquals(2, result.getErrors().size(), 
            "2 errors should be reported");
        assertTrue(result.getErrors().get(0).contains("Invalid difficulty"), 
            "Error should mention invalid difficulty");
    }
    
    /**
     * EC_INV5: CSV with invalid correct answer (not A/B/C/D)
     * Test importing CSV with invalid correct answer values
     * Input: CSV with correct answer = "E" and correct answer = "X"
     * Expected Output: Rows skipped, errors reported
     * Equivalence Class: EC_INV5
     */
    @Test
    void testImportQuestionsFromCSV_InvalidCorrectAnswer() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "1,Question with answer E?,1,Opt1,Opt2,Opt3,Opt4,E\n" +
                           "2,Question with answer X?,2,Opt1,Opt2,Opt3,Opt4,X\n" +
                           "3,Valid Question?,1,Opt1,Opt2,Opt3,Opt4,A";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(1, result.getImportedCount(), 
            "1 valid question should be imported");
        assertEquals(2, result.getSkippedCount(), 
            "2 questions with invalid correct answer should be skipped");
        assertEquals(2, result.getErrors().size(), 
            "2 errors should be reported");
        assertTrue(result.getErrors().get(0).contains("Invalid correct answer"), 
            "Error should mention invalid correct answer");
    }
    
    /**
     * EC_INV6: CSV with empty question text
     * Test importing CSV with empty question text field
     * Input: CSV with question text = ""
     * Expected Output: Row skipped, error reported
     * Equivalence Class: EC_INV6
     */
    @Test
    void testImportQuestionsFromCSV_EmptyQuestionText() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "1,,1,Opt1,Opt2,Opt3,Opt4,A\n" +
                           "2,Valid Question?,2,Opt1,Opt2,Opt3,Opt4,B";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(1, result.getImportedCount(), 
            "1 valid question should be imported");
        assertEquals(1, result.getSkippedCount(), 
            "1 question with empty text should be skipped");
        assertTrue(result.getErrors().get(0).contains("Question text cannot be empty"), 
            "Error should mention empty question text");
    }
    
    /**
     * EC_INV7: CSV with invalid number format (non-numeric ID or difficulty)
     * Test importing CSV with non-numeric ID or difficulty
     * Input: CSV with ID = "abc" or difficulty = "xyz"
     * Expected Output: Rows skipped, errors reported
     * Equivalence Class: EC_INV7
     */
    @Test
    void testImportQuestionsFromCSV_InvalidNumberFormat() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "abc,Question with non-numeric ID?,1,Opt1,Opt2,Opt3,Opt4,A\n" +
                           "2,Question with non-numeric difficulty?,xyz,Opt1,Opt2,Opt3,Opt4,B\n" +
                           "3,Valid Question?,1,Opt1,Opt2,Opt3,Opt4,C";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(1, result.getImportedCount(), 
            "1 valid question should be imported");
        assertEquals(2, result.getSkippedCount(), 
            "2 questions with invalid number format should be skipped");
        assertEquals(2, result.getErrors().size(), 
            "2 errors should be reported");
        assertTrue(result.getErrors().get(0).contains("Invalid number format"), 
            "Error should mention invalid number format");
    }
    
    /**
     * Boundary test: CSV with all boundary difficulty values (1, 2, 3, 4)
     * Test importing CSV with all valid difficulty boundary values
     * Input: CSV with difficulties 1, 2, 3, 4
     * Expected Output: All questions imported successfully
     * Equivalence Class: EC1 (boundary values)
     */
    @Test
    void testImportQuestionsFromCSV_BoundaryDifficultyValues() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "1,Question difficulty 1?,1,Opt1,Opt2,Opt3,Opt4,A\n" +
                           "2,Question difficulty 2?,2,Opt1,Opt2,Opt3,Opt4,B\n" +
                           "3,Question difficulty 3?,3,Opt1,Opt2,Opt3,Opt4,C\n" +
                           "4,Question difficulty 4?,4,Opt1,Opt2,Opt3,Opt4,D";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(4, result.getImportedCount(), 
            "All 4 questions with boundary difficulty values should be imported");
        assertEquals(0, result.getSkippedCount(), 
            "No questions should be skipped");
        assertEquals(4, questionLogic.getQuestions().size(), 
            "Questions list should contain 4 questions");
    }
    
    /**
     * Boundary test: CSV with all valid correct answer values (A, B, C, D)
     * Test importing CSV with all valid correct answer boundary values
     * Input: CSV with correct answers A, B, C, D
     * Expected Output: All questions imported successfully
     * Equivalence Class: EC1 (boundary values)
     */
    @Test
    void testImportQuestionsFromCSV_BoundaryCorrectAnswerValues() throws Exception {
        // Arrange
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                           "1,Question with answer A?,1,Opt1,Opt2,Opt3,Opt4,A\n" +
                           "2,Question with answer B?,1,Opt1,Opt2,Opt3,Opt4,B\n" +
                           "3,Question with answer C?,1,Opt1,Opt2,Opt3,Opt4,C\n" +
                           "4,Question with answer D?,1,Opt1,Opt2,Opt3,Opt4,D";
        File csvFile = createTestCSV(csvContent);
        
        // Act
        QuestionLogic.ImportResult result = questionLogic.importQuestionsFromCSV(csvFile);
        
        // Assert
        assertEquals(4, result.getImportedCount(), 
            "All 4 questions with boundary correct answer values should be imported");
        assertEquals(0, result.getSkippedCount(), 
            "No questions should be skipped");
        assertEquals(4, questionLogic.getQuestions().size(), 
            "Questions list should contain 4 questions");
    }
}

