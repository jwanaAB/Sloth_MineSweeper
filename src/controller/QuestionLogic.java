package controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import model.Question;

public class QuestionLogic {
    private List<Question> questions;

    public QuestionLogic() {
        this.questions = new ArrayList<>();
    }

    public void loadQuestionsFromCSV(String csvPath) {
        questions.clear();
        
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(csvPath);
            if (inputStream == null) {
                throw new RuntimeException("Could not find resource: " + csvPath);
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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
            
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error loading questions from CSV: " + csvPath, e);
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }
}

