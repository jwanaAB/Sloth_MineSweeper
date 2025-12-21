# Black Box Test Documentation

## 1. Test ID: BB_IMPORT_001

## 2. Short Description:
This test verifies the CSV import functionality, which allows the system to import questions from a CSV file. The test focuses on the functional requirement without knowledge of internal implementation details. The method validates CSV structure, checks question data validity, skips invalid rows, and reports errors.

## 3. Selected Requirement:
"The system should allow importing questions from a CSV file. The CSV file must have a correct header format and each row must contain valid question data. Invalid rows should be skipped and errors should be reported. The method should return an ImportResult containing the number of imported questions, skipped questions, and error messages."

**Full Requirement:**
The game system supports importing questions from external CSV files to populate the question database. The `importQuestionsFromCSV` method must enforce the following rules:

1. The CSV file must exist and be readable
2. The CSV file must have a header row with the exact format: "ID,Question,Difficulty,A,B,C,D,Correct Answer"
3. Each data row must contain exactly 8 columns (ID, Question text, Difficulty, Option A, Option B, Option C, Option D, Correct Answer)
4. The ID field must be numeric (will be regenerated to avoid conflicts)
5. The Question text field must not be empty
6. The Difficulty field must be an integer between 1 and 4 (inclusive)
7. The Correct Answer field must be exactly "A", "B", "C", or "D"
8. Answer options (A, B, C, D) must be non-empty strings
9. Valid questions should be imported and added to the questions list with new generated IDs
10. Invalid rows should be skipped and appropriate error messages should be recorded
11. Empty lines in the CSV should be ignored (skipped)
12. The method should return an ImportResult object containing:
    - `importedCount`: Number of successfully imported questions
    - `skippedCount`: Number of skipped/invalid rows
    - `errors`: List of error messages describing validation failures
13. If the file does not exist, the method should throw an Exception

## 4. Equivalent Classes:

**Input: csvFile (File object)**
- EC1: File exists and is readable - Valid input
- EC2: File does not exist - Invalid input

**Input: CSV Header**
- EC3: Correct header format ("ID,Question,Difficulty,A,B,C,D,Correct Answer") - Valid input
- EC4: Incorrect header format (any other format) - Invalid input

**Input: Number of Columns per Row**
- EC5: Exactly 8 columns - Valid input
- EC6: Less than 8 columns - Invalid input
- EC7: More than 8 columns - Valid input (extra columns ignored)

**Input: Question Text**
- EC8: Non-empty question text - Valid input
- EC9: Empty question text - Invalid input

**Input: Difficulty Value**
- EC10: Valid difficulty (1 <= difficulty <= 4) - Valid input
- EC11: Invalid difficulty (difficulty < 1 OR difficulty > 4) - Invalid input

**Input: Correct Answer**
- EC12: Valid answer ("A" OR "B" OR "C" OR "D") - Valid input
- EC13: Invalid answer (not in {A, B, C, D}) - Invalid input

**Input: ID and Difficulty Format**
- EC14: Numeric format (can be parsed as integer) - Valid input
- EC15: Non-numeric format (cannot be parsed as integer) - Invalid input

**Input: CSV Row Type**
- EC16: Data row with valid question - Valid input
- EC17: Empty line - Valid input (should be skipped)
- EC18: Data row with invalid question - Invalid input (should be skipped)

**Expected Output:**
- Valid import: ImportResult with importedCount > 0, appropriate skippedCount and errors
- Invalid file: Exception thrown
- Mixed valid/invalid: ImportResult with some importedCount, some skippedCount, and error messages

## 5. Test Case:

**Input Values:**
- csvFile: A CSV file with the following content:
```
ID,Question,Difficulty,A,B,C,D,Correct Answer
1,What is 2+2?,1,3,4,5,6,B
2,Invalid Difficulty?,5,Opt1,Opt2,Opt3,Opt4,B
3,Valid Question 2?,2,Opt1,Opt2,Opt3,Opt4,C
4,Invalid Answer?,1,Opt1,Opt2,Opt3,Opt4,E
```

- File state: File exists and is readable
- Preconditions: QuestionLogic object is initialized with empty questions list

**Mapping of Each Input Value to Its Equivalence Class:**
- csvFile exists: Maps to EC1 (File exists and is readable)
- Header "ID,Question,Difficulty,A,B,C,D,Correct Answer": Maps to EC3 (Correct header format)
- Row 1 (ID=1): 
  - 8 columns: Maps to EC5 (Exactly 8 columns)
  - Question text "What is 2+2?": Maps to EC8 (Non-empty question text)
  - Difficulty 1: Maps to EC10 (Valid difficulty, 1 <= 1 <= 4)
  - Correct answer "B": Maps to EC12 (Valid answer, "B" is in {A,B,C,D})
  - ID format "1": Maps to EC14 (Numeric format)
  - Overall: Maps to EC16 (Data row with valid question)
- Row 2 (ID=2):
  - 8 columns: Maps to EC5 (Exactly 8 columns)
  - Question text "Invalid Difficulty?": Maps to EC8 (Non-empty question text)
  - Difficulty 5: Maps to EC11 (Invalid difficulty, 5 > 4)
  - Correct answer "B": Maps to EC12 (Valid answer)
  - Overall: Maps to EC18 (Data row with invalid question)
- Row 3 (ID=3):
  - 8 columns: Maps to EC5 (Exactly 8 columns)
  - Question text "Valid Question 2?": Maps to EC8 (Non-empty question text)
  - Difficulty 2: Maps to EC10 (Valid difficulty, 1 <= 2 <= 4)
  - Correct answer "C": Maps to EC12 (Valid answer, "C" is in {A,B,C,D})
  - Overall: Maps to EC16 (Data row with valid question)
- Row 4 (ID=4):
  - 8 columns: Maps to EC5 (Exactly 8 columns)
  - Question text "Invalid Answer?": Maps to EC8 (Non-empty question text)
  - Difficulty 1: Maps to EC10 (Valid difficulty)
  - Correct answer "E": Maps to EC13 (Invalid answer, "E" is not in {A,B,C,D})
  - Overall: Maps to EC18 (Data row with invalid question)

**Expected Output:**
- Return value: `ImportResult` object with:
  - `importedCount` = 2 (rows 1 and 3 are valid and imported)
  - `skippedCount` = 2 (rows 2 and 4 are invalid and skipped)
  - `errors` list contains 2 error messages:
    - "Line 3: Invalid difficulty (must be 1-4)" (for row 2 with difficulty=5)
    - "Line 5: Invalid correct answer (must be A, B, C, or D)" (for row 4 with answer="E")
- The questions list should contain 2 questions (the valid ones from rows 1 and 3)
- The imported questions should have new generated IDs (sequential IDs starting from 1, not the original IDs 1 and 3)
- No exception should be thrown

**Test Scenario Description:**
This test case represents a typical mixed scenario where:
- A valid CSV file exists with correct header format
- The CSV contains a mix of valid and invalid question rows
- Valid questions (rows 1 and 3) should be successfully imported
- Invalid questions (rows 2 and 4) should be skipped with appropriate error messages
- The method should handle both valid and invalid data gracefully without throwing exceptions
- Error reporting should be accurate and descriptive

This is a positive test case that demonstrates the method's ability to handle mixed valid/invalid data and properly report results through the ImportResult object.

## 6. Tester Name: [Your Name]
