package controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import model.Question;

public class QuestionLogic {
    private List<Question> questions;
    private String csvPath;

    public QuestionLogic() {
        this.questions = new ArrayList<>();
    }

    public void loadQuestionsFromCSV(String csvPath) {
        this.csvPath = csvPath; // Store the path for later saving
        questions.clear();

        BufferedReader reader = null;
        InputStream inputStream = null;

        try {
            // First try as resource stream
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
                reader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading questions from CSV: " + csvPath, e);
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void updateQuestion(Question updatedQuestion) {
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
        java.io.File file = new java.io.File("src/" + csvPath);
        if (!file.exists()) {
            file = new java.io.File(csvPath);
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

    public void addQuestion(Question question) {
        questions.add(question);
    }
}
