package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import model.Question;

public class QuestionLogic {
    private List<Question> questions;
    private String csvPath;
    private File externalCsvFile;

    public QuestionLogic() {
        this.questions = new ArrayList<>();
        this.externalCsvFile = getExternalCsvFile();
    }

    /**
     * Gets the external CSV file path. When running from JAR, uses a file in the same directory as the JAR.
     * When running from IDE, uses the src/resources/Questions.csv file.
     */
    private File getExternalCsvFile() {
        try {
            // Try to get the JAR file location
            URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
            URI uri = location.toURI();
            File jarFile = new File(uri);
            
            // Check if running from JAR (path ends with .jar)
            String jarPath = jarFile.getAbsolutePath();
            if (jarPath != null && jarPath.toLowerCase().endsWith(".jar")) {
                File jarDir = jarFile.getParentFile();
                if (jarDir != null) {
                    // Use Questions.csv in the same directory as the JAR
                    return new File(jarDir, "Questions.csv");
                }
            }
        } catch (Exception e) {
            // Not running from JAR or couldn't determine JAR location
        }
        
        // Fallback: use Questions.csv in current directory (project root)
        return new File("Questions.csv");
    }

    /**
     * Ensures the external CSV file exists. If it doesn't exist, creates an empty CSV file with just the header.
     */
    private void ensureExternalCsvExists() {
        if (externalCsvFile.exists()) {
            return; // External file already exists
        }

        // Try to extract from resources first (in case it's still in the JAR)
        String[] resourcePaths = {
            "resources/Questions.csv",
            "/resources/Questions.csv",
            "Questions.csv",
            "/Questions.csv"
        };

        InputStream resourceStream = null;
        OutputStream fileStream = null;

        try {
            for (String resourcePath : resourcePaths) {
                resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
                if (resourceStream == null) {
                    resourceStream = getClass().getClassLoader().getResourceAsStream("/" + resourcePath);
                }
                if (resourceStream != null) {
                    break;
                }
            }

            // Create parent directories if needed
            if (externalCsvFile.getParentFile() != null) {
                externalCsvFile.getParentFile().mkdirs();
            }

            if (resourceStream != null) {
                // Copy resource to external file
                fileStream = new FileOutputStream(externalCsvFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = resourceStream.read(buffer)) != -1) {
                    fileStream.write(buffer, 0, bytesRead);
                }
                System.out.println("Extracted Questions.csv to: " + externalCsvFile.getAbsolutePath());
            } else {
                // Resource doesn't exist, create empty CSV with header
                fileStream = new FileOutputStream(externalCsvFile);
                String header = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n";
                fileStream.write(header.getBytes());
                System.out.println("Created empty Questions.csv at: " + externalCsvFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not create Questions.csv: " + e.getMessage());
        } finally {
            try {
                if (resourceStream != null) resourceStream.close();
                if (fileStream != null) fileStream.close();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    public void loadQuestionsFromCSV(String csvPath) {
        // Always use the external CSV file when it exists (for JAR execution)
        ensureExternalCsvExists();
        
        if (externalCsvFile.exists()) {
            this.csvPath = externalCsvFile.getAbsolutePath();
        } else {
            this.csvPath = csvPath; // Fallback to provided path
        }
        
        questions.clear();

        BufferedReader reader = null;
        InputStream inputStream = null;

        try {
            // First, try to use the external CSV file
            if (externalCsvFile.exists()) {
                reader = new BufferedReader(new java.io.FileReader(externalCsvFile));
            } else {
                // Fallback: try as resource stream
                inputStream = getClass().getClassLoader().getResourceAsStream(csvPath);
                if (inputStream == null) {
                    // Try with leading slash
                    inputStream = getClass().getClassLoader().getResourceAsStream("/" + csvPath);
                }
                if (inputStream == null) {
                    // Try as file path (for development)
                    java.io.File file = new java.io.File("src/" + csvPath);
                    if (!file.exists()) {
                        file = new java.io.File(csvPath);
                    }
                    if (file.exists()) {
                        reader = new BufferedReader(new java.io.FileReader(file));
                    } else {
                        throw new RuntimeException("Could not find resource or file: " + csvPath);
                    }
                } else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                }
            }
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }

                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String questionText = parts[1].trim();
                        int difficulty = Integer.parseInt(parts[2].trim());
                        String a = parts[3].trim();
                        String b = parts[4].trim();
                        String c = parts[5].trim();
                        String d = parts[6].trim();
                        String correctAnswer = parts[7].trim();

                        Question question = new Question(id, questionText, difficulty, a, b, c, d, correctAnswer);
                        questions.add(question);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line);
                        e.printStackTrace();
                    }
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading questions from CSV: " + csvPath, e);
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void updateQuestion(Question updatedQuestion) {
        validateUniqueOptions(updatedQuestion);
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId() == updatedQuestion.getId()) {
                questions.set(i, updatedQuestion);
                break;
            }
        }
    }

    public void deleteQuestion(int questionId) {
        questions.removeIf(q -> q.getId() == questionId);
    }

    public void saveQuestionsToCSV(String csvPath) throws Exception {
        // Always save to the external CSV file when it exists
        java.io.File file;
        if (externalCsvFile != null && externalCsvFile.exists()) {
            file = externalCsvFile;
        } else {
            // Fallback to provided path
            file = new java.io.File("src/" + csvPath);
            if (!file.exists()) {
                file = new java.io.File(csvPath);
            }
        }

        // Ensure parent directory exists
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file));

        // Write header
        writer.write("ID,Question,Difficulty,A,B,C,D,Correct Answer");
        writer.newLine();

        // Write questions
        for (Question q : questions) {
            writer.write(String.format("%d,%s,%d,%s,%s,%s,%s,%s",
                    q.getId(),
                    q.getQuestionText(),
                    q.getDifficulty(),
                    q.getA(),
                    q.getB(),
                    q.getC(),
                    q.getD(),
                    q.getCorrectAnswer()));
            writer.newLine();
        }

        writer.close();
    }

    public String getCSVPath() {
        return csvPath;
    }

    public int getNextId() {
        int maxId = 0;
        for (Question q : questions) {
            if (q.getId() > maxId) {
                maxId = q.getId();
            }
        }
        return maxId + 1;
    }

    /**
     * Validates that all answer options (A, B, C, D) are non-empty and unique,
     * difficulty is between 1-4, and correct answer is A, B, C, or D
     * @param question The question to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUniqueOptions(Question question) {
        String a = question.getA();
        String b = question.getB();
        String c = question.getC();
        String d = question.getD();
        
        // Check for empty strings - no field should be left empty
        if (a == null || a.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer option A cannot be empty");
        }
        if (b == null || b.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer option B cannot be empty");
        }
        if (c == null || c.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer option C cannot be empty");
        }
        if (d == null || d.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer option D cannot be empty");
        }
        
        // Check all pairs for duplicates
        if (a.equals(b)) {
            throw new IllegalArgumentException("Answer options A and B cannot be identical");
        }
        if (a.equals(c)) {
            throw new IllegalArgumentException("Answer options A and C cannot be identical");
        }
        if (a.equals(d)) {
            throw new IllegalArgumentException("Answer options A and D cannot be identical");
        }
        if (b.equals(c)) {
            throw new IllegalArgumentException("Answer options B and C cannot be identical");
        }
        if (b.equals(d)) {
            throw new IllegalArgumentException("Answer options B and D cannot be identical");
        }
        if (c.equals(d)) {
            throw new IllegalArgumentException("Answer options C and D cannot be identical");
        }
        
        // Validate difficulty (must be 1-4)
        int difficulty = question.getDifficulty();
        if (difficulty < 1 || difficulty > 4) {
            throw new IllegalArgumentException("Difficulty must be between 1 and 4");
        }
        
        // Validate correct answer (must be A, B, C, or D)
        String correctAnswer = question.getCorrectAnswer();
        if (correctAnswer == null || 
            (!correctAnswer.equals("A") && !correctAnswer.equals("B") && 
             !correctAnswer.equals("C") && !correctAnswer.equals("D"))) {
            throw new IllegalArgumentException("Correct answer must be A, B, C, or D");
        }
    }

    public void addQuestion(Question question) {
        validateUniqueOptions(question);
        questions.add(question);
    }

    /**
     * Imports questions from a CSV file and appends valid questions to the existing list.
     * Only questions with the correct structure (8 columns: ID,Question,Difficulty,A,B,C,D,Correct Answer) are imported.
     * 
     * @param csvFile The CSV file to import from
     * @return ImportResult containing the number of successfully imported questions and any errors
     */
    public ImportResult importQuestionsFromCSV(File csvFile) throws Exception {
        int importedCount = 0;
        int skippedCount = 0;
        List<String> errors = new ArrayList<>();

        // Check if file exists before attempting to read
        if (!csvFile.exists()) {
            throw new java.io.FileNotFoundException("CSV file does not exist: " + csvFile.getAbsolutePath());
        }

        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (isFirstLine) {
                    isFirstLine = false;
                    // Validate header
                    String expectedHeader = "ID,Question,Difficulty,A,B,C,D,Correct Answer";
                    if (!line.trim().equals(expectedHeader)) {
                        errors.add("Line 1: Header does not match expected format. Expected: " + expectedHeader);
                    }
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                String[] parts = line.split(",");
                if (parts.length < 8) {
                    skippedCount++;
                    errors.add("Line " + lineNumber + ": Insufficient columns (expected 8, found " + parts.length + ")");
                    continue;
                }

                try {
                    // Validate ID format (we'll generate a new one to avoid conflicts)
                    Integer.parseInt(parts[0].trim());
                    String questionText = parts[1].trim();
                    int difficulty = Integer.parseInt(parts[2].trim());
                    String a = parts[3].trim();
                    String b = parts[4].trim();
                    String c = parts[5].trim();
                    String d = parts[6].trim();
                    String correctAnswer = parts[7].trim();

                    // Validate difficulty (1-4)
                    if (difficulty < 1 || difficulty > 4) {
                        skippedCount++;
                        errors.add("Line " + lineNumber + ": Invalid difficulty (must be 1-4)");
                        continue;
                    }

                    // Validate correct answer (A, B, C, or D)
                    if (!correctAnswer.equals("A") && !correctAnswer.equals("B") && 
                        !correctAnswer.equals("C") && !correctAnswer.equals("D")) {
                        skippedCount++;
                        errors.add("Line " + lineNumber + ": Invalid correct answer (must be A, B, C, or D)");
                        continue;
                    }

                    // Check if question text is not empty
                    if (questionText.isEmpty()) {
                        skippedCount++;
                        errors.add("Line " + lineNumber + ": Question text cannot be empty");
                        continue;
                    }

                    // Generate new ID to avoid conflicts
                    int newId = getNextId();
                    Question question = new Question(newId, questionText, difficulty, a, b, c, d, correctAnswer);
                    questions.add(question);
                    importedCount++;
                } catch (NumberFormatException e) {
                    skippedCount++;
                    errors.add("Line " + lineNumber + ": Invalid number format - " + e.getMessage());
                } catch (Exception e) {
                    skippedCount++;
                    errors.add("Line " + lineNumber + ": Error parsing question - " + e.getMessage());
                }
            }
        } catch (java.io.FileNotFoundException e) {
            // Re-throw FileNotFoundException (file doesn't exist)
            throw new Exception("CSV file does not exist: " + csvFile.getAbsolutePath(), e);
        } catch (Exception e) {
            // For other I/O errors, add to errors list and return result
            errors.add("Error reading CSV file: " + e.getMessage());
        }

        return new ImportResult(importedCount, skippedCount, errors);
    }

    /**
     * Result of importing questions from CSV.
     */
    public static class ImportResult {
        private final int importedCount;
        private final int skippedCount;
        private final List<String> errors;

        public ImportResult(int importedCount, int skippedCount, List<String> errors) {
            this.importedCount = importedCount;
            this.skippedCount = skippedCount;
            this.errors = errors;
        }

        public int getImportedCount() {
            return importedCount;
        }

        public int getSkippedCount() {
            return skippedCount;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}
