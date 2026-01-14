package view;

import java.awt.*;
import javax.swing.*;
import model.Question;

/**
 * Custom dialog for displaying questions with answers in a 2x2 grid layout.
 * 
 * @author Team Sloth
 */
public class QuestionDialog extends JDialog {
    private Integer selectedAnswer = null;
    private final JPanel[] answerPanels;
    
    public QuestionDialog(JFrame parent, Question question, String playerName) {
        super(parent, "Question for " + playerName, true);
        
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Use percentage-based sizing for better screen compatibility
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogWidth = (int) (screenSize.width * 0.5); // 50% of screen width
        int dialogHeight = (int) (screenSize.height * 0.5); // 50% of screen height
        setPreferredSize(new Dimension(dialogWidth, dialogHeight));
        setSize(dialogWidth, dialogHeight);
        
        // Center on screen or relative to parent
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            setLocationRelativeTo(null);
        }
        
        setResizable(false);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Question text panel (icon removed to save space)
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(new Color(245, 245, 250));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        questionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Question text
        JTextArea questionText = new JTextArea(question.getQuestionText());
        questionText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        questionText.setBackground(new Color(245, 245, 250));
        questionText.setForeground(new Color(40, 40, 40));
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setEditable(false);
        questionText.setFocusable(false);
        questionText.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        questionPanel.add(questionText, BorderLayout.CENTER);
        
        contentPanel.add(questionPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Answers in 2x2 grid
        JPanel answersPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        answersPanel.setBackground(new Color(245, 245, 250));
        answersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        answersPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        answerPanels = new JPanel[4];
        String[] options = {question.getA(), question.getB(), question.getC(), question.getD()};
        String[] labels = {"A", "B", "C", "D"};
        
        for (int i = 0; i < 4; i++) {
            final int index = i;
            JPanel answerPanel = createAnswerPanel(labels[i], options[i], index);
            answerPanels[i] = answerPanel;
            answersPanel.add(answerPanel);
        }
        
        contentPanel.add(answersPanel);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Prevent closing without selecting an answer
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                JOptionPane.showMessageDialog(
                    QuestionDialog.this,
                    "You must answer the question before closing.",
                    "Answer Required",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });
    }
    
    private JPanel createAnswerPanel(String label, String text, int index) {
        // Create a clickable panel that looks like a button
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(true);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Default styling with reduced padding
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 220), 2),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        
        // Label (A, B, C, D)
        JLabel labelLabel = new JLabel(label + ")");
        labelLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelLabel.setForeground(new Color(70, 130, 180));
        labelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        labelLabel.setOpaque(false);
        
        // Answer text with proper wrapping
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setBackground(new Color(255, 255, 255));
        textArea.setForeground(new Color(40, 40, 40));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false); // Transparent so panel background shows
        textArea.setMargin(new Insets(0, 3, 0, 3));
        
        panel.add(labelLabel, BorderLayout.NORTH);
        panel.add(textArea, BorderLayout.CENTER);
        
        // Store reference for hover effect
        final JPanel finalPanel = panel;
        
        // Hover effect
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color hoverColor = new Color(230, 240, 255);
                finalPanel.setBackground(hoverColor);
                finalPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Color defaultColor = new Color(255, 255, 255);
                finalPanel.setBackground(defaultColor);
                finalPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 220), 2),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedAnswer = index;
                dispose();
            }
        });
        
        return panel;
    }
    
    /**
     * Shows the dialog and returns the selected answer index (0-3 for A-D).
     * Returns null if dialog was closed without selection.
     * 
     * @return Selected answer index (0=A, 1=B, 2=C, 3=D) or null
     */
    public Integer showDialog() {
        setVisible(true);
        return selectedAnswer;
    }
}

