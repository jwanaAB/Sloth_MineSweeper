package view;

import java.awt.*;
import javax.swing.*;
import model.Question;

public class AddQuestionDialog extends JDialog {
    private final JTextArea questionField;
    private final JTextField optionAField;
    private final JTextField optionBField;
    private final JTextField optionCField;
    private final JTextField optionDField;
    private final JComboBox<String> correctAnswerCombo;
    private final JComboBox<String> difficultyCombo;
    private boolean saved = false;
    private Question question;

    public AddQuestionDialog(JFrame parent) {
        super(parent, "Add Question", true);

        setLayout(new BorderLayout());
        setSize(600, 430);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Question field - wider and styled
        contentPanel.add(createLabel("Question"));
        questionField = new JTextArea("", 1, 50);
        questionField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        questionField.setBackground(new Color(245, 245, 245));
        questionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        questionField.setLineWrap(true);
        questionField.setWrapStyleWord(true);
        JScrollPane questionScroll = new JScrollPane(questionField);
        questionScroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        questionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        questionScroll.setPreferredSize(new Dimension(540, 50));
        questionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(questionScroll);
        contentPanel.add(Box.createVerticalStrut(15));

        // Options in 2x2 grid
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Option A
        JPanel optionAPanel = new JPanel();
        optionAPanel.setLayout(new BoxLayout(optionAPanel, BoxLayout.Y_AXIS));
        optionAPanel.setBackground(Color.WHITE);
        optionAPanel.add(createLabel("Option A"));
        optionAField = createTextField("");
        optionAPanel.add(optionAField);
        optionsPanel.add(optionAPanel);

        // Option B
        JPanel optionBPanel = new JPanel();
        optionBPanel.setLayout(new BoxLayout(optionBPanel, BoxLayout.Y_AXIS));
        optionBPanel.setBackground(Color.WHITE);
        optionBPanel.add(createLabel("Option B"));
        optionBField = createTextField("");
        optionBPanel.add(optionBField);
        optionsPanel.add(optionBPanel);

        // Option C
        JPanel optionCPanel = new JPanel();
        optionCPanel.setLayout(new BoxLayout(optionCPanel, BoxLayout.Y_AXIS));
        optionCPanel.setBackground(Color.WHITE);
        optionCPanel.add(createLabel("Option C"));
        optionCField = createTextField("");
        optionCPanel.add(optionCField);
        optionsPanel.add(optionCPanel);

        // Option D
        JPanel optionDPanel = new JPanel();
        optionDPanel.setLayout(new BoxLayout(optionDPanel, BoxLayout.Y_AXIS));
        optionDPanel.setBackground(Color.WHITE);
        optionDPanel.add(createLabel("Option D"));
        optionDField = createTextField("");
        optionDPanel.add(optionDField);
        optionsPanel.add(optionDPanel);

        contentPanel.add(optionsPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Correct Answer and Difficulty in a row
        JPanel bottomFieldsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomFieldsPanel.setBackground(Color.WHITE);
        bottomFieldsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        bottomFieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Correct Answer
        JPanel correctPanel = new JPanel();
        correctPanel.setLayout(new BoxLayout(correctPanel, BoxLayout.Y_AXIS));
        correctPanel.setBackground(Color.WHITE);
        correctPanel.add(createLabel("Correct Answer"));
        String[] answers = { "A", "B", "C", "D" };
        correctAnswerCombo = new JComboBox<>(answers);
        correctAnswerCombo.setSelectedIndex(0);
        styleComboBox(correctAnswerCombo);
        correctPanel.add(correctAnswerCombo);
        bottomFieldsPanel.add(correctPanel);

        // Difficulty
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new BoxLayout(difficultyPanel, BoxLayout.Y_AXIS));
        difficultyPanel.setBackground(Color.WHITE);
        difficultyPanel.add(createLabel("Difficulty"));
        String[] difficulties = { "Easy", "Medium", "Hard", "Expert" };
        difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.setSelectedIndex(0);
        styleComboBox(difficultyCombo);
        difficultyPanel.add(difficultyCombo);
        bottomFieldsPanel.add(difficultyPanel);

        contentPanel.add(bottomFieldsPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setBorderPainted(true);
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(e -> {
            saved = false;
            dispose();
        });

        JButton saveButton = new JButton("Save Question");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)));
        saveButton.setOpaque(true);
        saveButton.setContentAreaFilled(true);
        saveButton.setBorderPainted(true);
        saveButton.setBackground(Color.BLACK);
        saveButton.setForeground(Color.WHITE);
        saveButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        saveButton.addActionListener(e -> {
            if (validateFields()) {
                createQuestion();
                saved = true;
                dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        contentPanel.add(buttonPanel);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(60, 60, 60));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(245, 245, 245));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        combo.setMaximumSize(new Dimension(220, 40));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private boolean validateFields() {
        if (questionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Question text cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (optionAField.getText().trim().isEmpty() ||
                optionBField.getText().trim().isEmpty() ||
                optionCField.getText().trim().isEmpty() ||
                optionDField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All options must be filled",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void createQuestion() {
        // ID will be set by QuestionLogic when adding
        question = new Question(0, 
                questionField.getText().trim(),
                difficultyCombo.getSelectedIndex() + 1,
                optionAField.getText().trim(),
                optionBField.getText().trim(),
                optionCField.getText().trim(),
                optionDField.getText().trim(),
                (String) correctAnswerCombo.getSelectedItem());
    }

    public boolean isSaved() {
        return saved;
    }

    public Question getQuestion() {
        return question;
    }
}

