package view;

import controller.QuestionLogic;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import model.Question;

public class QuestionManagerPanel extends JPanel {

    private final JButton homeButton;
    private final JTable questionTable;
    private final DefaultTableModel tableModel;
    private final QuestionLogic questionLogic;
    private final JTextField searchField;
    private final JComboBox<String> difficultyFilter;
    private List<Question> allQuestions;

    public QuestionManagerPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        questionLogic = new QuestionLogic();

        // Home button (top left)
        homeButton = new JButton("\u2190 Home");
        homeButton.setFocusPainted(false);
        homeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        homeButton.setBackground(new Color(245, 245, 245));
        homeButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        // Title (center)
        JLabel titleLabel = new JLabel("Question Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(156, 39, 176)); // Purple
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add Question button (top right)
        JButton addButton = new JButton("+ Add Question");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addButton.setOpaque(true);
        addButton.setContentAreaFilled(true);
        addButton.setBorderPainted(false);
        addButton.setBackground(Color.BLACK);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> handleAddQuestion());

        // Top header panel: Home (left) | Title (center) | Add Question (right)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        headerPanel.add(homeButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(addButton, BorderLayout.EAST);

        // Controls panel with search and filter in one line
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 15, 30));

        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        searchPanel.setPreferredSize(new Dimension(300, 40));
        
        searchField = new JTextField("Search questions...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(new Color(150, 150, 150));
        searchField.setBorder(BorderFactory.createEmptyBorder(8, 40, 8, 12));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search questions...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search questions...");
                    searchField.setForeground(new Color(150, 150, 150));
                }
            }
        });
        searchField.addCaretListener(e -> {
            if (!searchField.getText().equals("Search questions...")) {
                filterQuestions();
            }
        });
        
        // Magnifying glass icon (using Unicode search symbol)
        JLabel searchIcon = new JLabel("\u2315"); // Search symbol
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        searchIcon.setForeground(new Color(150, 150, 150));
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Difficulty filter
        String[] difficultyOptions = { "All Difficulties", "Easy", "Medium", "Hard", "Expert" };
        difficultyFilter = new JComboBox<>(difficultyOptions);
        difficultyFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        difficultyFilter.setBackground(Color.WHITE);
        difficultyFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        difficultyFilter.setPreferredSize(new Dimension(180, 40));
        difficultyFilter.addActionListener(e -> filterQuestions());
        
        // Add search and filter to controls panel in one line
        controlsPanel.add(searchPanel, BorderLayout.WEST);
        controlsPanel.add(difficultyFilter, BorderLayout.EAST);

        // Create table model with columns matching reference design
        String[] columnNames = { "Question", "Options", "Correct", "Difficulty", "Actions" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are non-editable, buttons handle actions
            }
        };

        questionTable = new JTable(tableModel);
        questionTable.setRowHeight(80); // Taller rows for better display
        questionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        questionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionTable.setShowGrid(false);
        questionTable.setIntercellSpacing(new Dimension(0, 0));
        questionTable.setBorder(BorderFactory.createEmptyBorder());

        // Style header
        JTableHeader header = questionTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(60, 60, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Set column widths
        questionTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        questionTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        questionTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        questionTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        questionTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Set custom renderers
        questionTable.getColumn("Question").setCellRenderer(new QuestionTextRenderer());
        questionTable.getColumn("Options").setCellRenderer(new OptionsRenderer());
        questionTable.getColumn("Correct").setCellRenderer(new CorrectAnswerRenderer());
        questionTable.getColumn("Difficulty").setCellRenderer(new DifficultyRenderer());
        questionTable.getColumn("Actions").setCellRenderer(new ActionsRenderer());

        // Add mouse listener for button clicks
        questionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = questionTable.rowAtPoint(e.getPoint());
                int col = questionTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col == 4) { // Actions column
                    int x = e.getX() - questionTable.getCellRect(row, col, false).x;
                    int cellWidth = questionTable.getCellRect(row, col, false).width;

                    if (x < cellWidth / 2) {
                        // Edit button clicked
                        handleEdit(row);
                    } else {
                        // Delete button clicked
                        handleDelete(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(questionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        scrollPane.setBackground(Color.WHITE);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(controlsPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add header and main content
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Load questions after panel is fully constructed
        SwingUtilities.invokeLater(this::loadQuestions);
    }

    private void loadQuestions() {
        try {
            // Try different possible resource paths
            String[] possiblePaths = {
                    "resources/Questions.csv",
                    "Questions.csv",
                    "/resources/Questions.csv",
                    "/Questions.csv"
            };

            boolean loaded = false;
            Exception lastException = null;

            for (String path : possiblePaths) {
                try {
                    questionLogic.loadQuestionsFromCSV(path);
                    loaded = true;
                    break;
                } catch (Exception e) {
                    lastException = e;
                }
            }

            if (!loaded) {
                throw new RuntimeException("Could not find Questions.csv in any expected location. " +
                        (lastException != null ? lastException.getMessage() : ""));
            }

            populateTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading questions: " + e.getMessage()
                            + "\n\nPlease ensure Questions.csv is in the resources folder.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void populateTable() {
        allQuestions = questionLogic.getQuestions();
        filterQuestions();
    }

    private void filterQuestions() {
        tableModel.setRowCount(0); // Clear existing rows
        
        if (allQuestions == null || allQuestions.isEmpty()) {
            System.err.println("Warning: No questions loaded from CSV");
            return;
        }

        String searchTextRaw = searchField.getText().toLowerCase().trim();
        final String finalSearchText = searchTextRaw.equals("search questions...") ? "" : searchTextRaw;
        
        String selectedDifficulty = (String) difficultyFilter.getSelectedItem();
        final int finalDifficultyFilterValue;
        
        if (!"All Difficulties".equals(selectedDifficulty)) {
            switch (selectedDifficulty) {
                case "Easy": finalDifficultyFilterValue = 1; break;
                case "Medium": finalDifficultyFilterValue = 2; break;
                case "Hard": finalDifficultyFilterValue = 3; break;
                case "Expert": finalDifficultyFilterValue = 4; break;
                default: finalDifficultyFilterValue = 0; break;
            }
        } else {
            finalDifficultyFilterValue = 0;
        }

        List<Question> filtered = allQuestions.stream()
                .filter(q -> {
                    // Filter by difficulty
                    if (finalDifficultyFilterValue > 0 && q.getDifficulty() != finalDifficultyFilterValue) {
                        return false;
                    }
                    // Filter by search text
                    if (!finalSearchText.isEmpty()) {
                        String questionText = q.getQuestionText().toLowerCase();
                        return questionText.contains(finalSearchText);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        for (Question question : filtered) {
            // Store question object in row for easy access
            Object[] row = {
                    question, // Question object
                    question, // Options (will be rendered)
                    question, // Correct answer (will be rendered)
                    question, // Difficulty (will be rendered)
                    question // Actions (will be rendered)
            };
            tableModel.addRow(row);
        }
    }

    private void handleEdit(int row) {
        Question question = (Question) tableModel.getValueAt(row, 0);

        // Show edit dialog
        EditQuestionDialog dialog = new EditQuestionDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                question);
        dialog.setVisible(true);

        // If saved, update the question and save to CSV
        if (dialog.isSaved()) {
            questionLogic.updateQuestion(dialog.getQuestion());
            try {
                questionLogic.saveQuestionsToCSV(questionLogic.getCSVPath());
                populateTable(); // Refresh the table
                JOptionPane.showMessageDialog(this,
                        "Question updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error saving changes: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void handleAddQuestion() {
        AddQuestionDialog dialog = new AddQuestionDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Question newQuestion = dialog.getQuestion();
            int nextId = questionLogic.getNextId();
            newQuestion.setId(nextId);
            questionLogic.addQuestion(newQuestion);
            try {
                questionLogic.saveQuestionsToCSV(questionLogic.getCSVPath());
                populateTable(); // Refresh the table
                JOptionPane.showMessageDialog(this,
                        "Question added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error saving question: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void handleDelete(int row) {
        Question question = (Question) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this question?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            questionLogic.deleteQuestion(question.getId());
            try {
                questionLogic.saveQuestionsToCSV(questionLogic.getCSVPath());
                populateTable(); // Refresh the table
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error saving changes: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public void setHomeAction(ActionListener actionListener) {
        for (ActionListener listener : homeButton.getActionListeners()) {
            homeButton.removeActionListener(listener);
        }
        if (actionListener != null) {
            homeButton.addActionListener(actionListener);
        }
    }

    /**
     * Reloads questions from the CSV file.
     * This should be called whenever the question manager panel is shown
     * to ensure the latest data from the CSV file is displayed.
     */
    public void reloadQuestions() {
        loadQuestions();
    }

    // Custom renderer for Question text
    private static class QuestionTextRenderer extends JLabel implements TableCellRenderer {
        public QuestionTextRenderer() {
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(new Color(60, 60, 60));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof Question) {
                Question q = (Question) value;
                setText(q.getQuestionText());
            }

            if (isSelected) {
                setBackground(new Color(245, 245, 250));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    // Custom renderer for Options column (A, B, C, D stacked)
    private static class OptionsRenderer extends JPanel implements TableCellRenderer {
        private final JLabel label;

        public OptionsRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            label = new JLabel();
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(new Color(60, 60, 60));
            add(label);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof Question) {
                Question q = (Question) value;
                String options = String.format("<html>A: %s<br>B: %s<br>C: %s<br>D: %s</html>",
                        q.getA(), q.getB(), q.getC(), q.getD());
                label.setText(options);
            }

            if (isSelected) {
                setBackground(new Color(245, 245, 250));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    // Custom renderer for Correct Answer (green circle)
    private static class CorrectAnswerRenderer extends JLabel implements TableCellRenderer {
        public CorrectAnswerRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof Question) {
                Question q = (Question) value;
                setText(q.getCorrectAnswer());
            }

            setForeground(Color.WHITE);
            setBackground(new Color(76, 175, 80)); // Green
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw circle
            int size = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g2.setColor(getBackground());
            g2.fillOval(x, y, size, size);

            // Draw text
            FontMetrics fm = g2.getFontMetrics(getFont());
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getAscent();
            g2.setColor(getForeground());
            g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);

            g2.dispose();
        }
    }

    // Custom renderer for Difficulty (color-coded pills)
    private static class DifficultyRenderer extends JLabel implements TableCellRenderer {
        public DifficultyRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof Question) {
                Question q = (Question) value;
                String difficulty = translateDifficulty(q.getDifficulty());
                setText(difficulty);

                // Set color based on difficulty
                switch (q.getDifficulty()) {
                    case 1: // Easy
                        setForeground(new Color(76, 175, 80)); // Light green
                        break;
                    case 2: // Medium
                        setForeground(new Color(255, 193, 7)); // Yellow
                        break;
                    case 3: // Hard
                        setForeground(new Color(244, 67, 54)); // Light red
                        break;
                    case 4: // Expert
                        setForeground(new Color(156, 39, 176)); // Purple
                        break;
                    default:
                        setForeground(new Color(120, 120, 120));
                }
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw pill background
            Color bgColor = getForeground().brighter().brighter();
            g2.setColor(bgColor);
            int height = getHeight() - 4;
            int y = 2;
            g2.fillRoundRect(2, y, getWidth() - 4, height, height, height);

            g2.dispose();
            super.paintComponent(g);
        }

        private String translateDifficulty(int difficulty) {
            switch (difficulty) {
                case 1:
                    return "Easy";
                case 2:
                    return "Medium";
                case 3:
                    return "Hard";
                case 4:
                    return "Expert";
                default:
                    return "Unknown";
            }
        }
    }

    // Custom renderer for Actions (Edit and Delete buttons)
    private static class ActionsRenderer extends JPanel implements TableCellRenderer {
        public ActionsRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (isSelected) {
                setBackground(new Color(245, 245, 250));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int cellWidth = getWidth();
            int cellHeight = getHeight();
            int iconSize = 20;
            int spacing = 15;
            
            // Calculate positions for edit and delete icons
            int totalWidth = (iconSize * 2) + spacing;
            int startX = (cellWidth - totalWidth) / 2;
            int iconY = (cellHeight - iconSize) / 2;
            
            // Draw edit icon (pencil) on the left
            int editX = startX;
            g2.setColor(new Color(60, 60, 60));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // Draw pencil icon - simplified and clearer
            int px = editX + iconSize / 2;
            int py = iconY + iconSize / 2;
            
            // Pencil body (diagonal line)
            g2.drawLine(px - 6, py + 6, px + 4, py - 4);
            // Pencil tip
            g2.drawLine(px + 4, py - 4, px + 6, py - 6);
            // Pencil eraser (top)
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(px - 7, py + 7, px - 5, py + 5);
            
            // Draw delete icon (trash can) on the right
            int deleteX = startX + iconSize + spacing;
            g2.setColor(new Color(244, 67, 54));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int dx = deleteX + iconSize / 2;
            int dy = iconY + iconSize / 2;
            
            // Trash can lid
            g2.drawLine(dx - 5, dy - 4, dx + 5, dy - 4);
            g2.drawLine(dx - 4, dy - 5, dx + 4, dy - 5);
            // Trash can body
            g2.drawRect(dx - 4, dy - 3, 8, 10);
            // Vertical lines on trash can
            g2.drawLine(dx - 2, dy - 1, dx - 2, dy + 5);
            g2.drawLine(dx, dy - 1, dx, dy + 5);
            g2.drawLine(dx + 2, dy - 1, dx + 2, dy + 5);
            
            g2.dispose();
        }
    }
}
