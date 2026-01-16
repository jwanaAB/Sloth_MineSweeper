package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import model.Game;
import model.GameHistory;
import model.SysData;

public class HistoryPanel extends JPanel {

    private final JButton homeButton;
    private final JPanel contentPanel;
    private final JPanel filterPanel;
    private final JLabel totalGamesLabel;
    private JButton allButton;
    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;
    private Game.Difficulty currentFilter = null; // null means "All"
    
    public HistoryPanel() {
        setLayout(new BorderLayout());
        // Set gradient background
        setBackground(new Color(230, 240, 255)); // Light blue-purple gradient base

        // Back button with arrow icon
        homeButton = new JButton("← Back to Menu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        homeButton.setFocusPainted(false);
        homeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        homeButton.setBackground(Color.WHITE);
        homeButton.setForeground(new Color(66, 66, 66));
        homeButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        homeButton.setBorderPainted(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setOpaque(false);
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeButton.setPreferredSize(new Dimension(120, 28));
        homeButton.setMaximumSize(new Dimension(120, 28));

        // Top bar with back button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        topBar.add(homeButton, BorderLayout.WEST);

        // Title panel
        JPanel titlePanel = createTitlePanel();
        
        // Filter buttons panel
        filterPanel = createFilterPanel();
        
        // Combine title and filters in a compact header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 15, 30));
        headerPanel.add(titlePanel);
        headerPanel.add(filterPanel);
        
        topBar.add(headerPanel, BorderLayout.CENTER);
        
        // Content panel for game records
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        // Scroll pane for content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Increase scroll speed - make it less sensitive (scroll more per wheel click)
        scrollPane.getVerticalScrollBar().setUnitIncrement(30); // Default is usually 1, increase to 30
        scrollPane.getVerticalScrollBar().setBlockIncrement(100); // Page up/down scroll amount
        
        // Total games label at bottom
        totalGamesLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        totalGamesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        totalGamesLabel.setForeground(new Color(66, 66, 66));
        totalGamesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalGamesLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        totalGamesLabel.setOpaque(false);
        totalGamesLabel.setBackground(new Color(187, 222, 251)); // Light blue
        
        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(totalGamesLabel, BorderLayout.SOUTH);
        
        // Load and display history
        refreshHistory();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw gradient background (light blue to light purple)
        int width = getWidth();
        int height = getHeight();
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(230, 240, 255), // Light blue
            0, height, new Color(243, 229, 245) // Light purple
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);
        
        g2.dispose();
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Icon (purple square)
        JLabel iconLabel = new JLabel("■") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(156, 39, 176)); // Purple
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(30, 30));
        iconLabel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Game History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(156, 39, 176)); // Purple
        
        panel.add(iconLabel);
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        // Create filter buttons
        allButton = createFilterButton("All", null);
        easyButton = createFilterButton("Easy", Game.Difficulty.EASY);
        mediumButton = createFilterButton("Medium", Game.Difficulty.MEDIUM);
        hardButton = createFilterButton("Hard", Game.Difficulty.HARD);
        
        // Set "All" as selected by default
        setFilterButtonSelected(allButton, true);
        
        panel.add(allButton);
        panel.add(easyButton);
        panel.add(mediumButton);
        panel.add(hardButton);
        
        return panel;
    }
    
    private JButton createFilterButton(String text, Game.Difficulty difficulty) {
        final Game.Difficulty filterDifficulty = difficulty; // Final for use in inner class
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean isSelected = (filterDifficulty == null && currentFilter == null) ||
                                    (filterDifficulty != null && filterDifficulty == currentFilter);
                
                if (isSelected) {
                    g2.setColor(new Color(33, 150, 243)); // Blue when selected
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        
        // Set initial state (unselected)
        setFilterButtonSelected(button, false);
        
        button.addActionListener(e -> {
            currentFilter = filterDifficulty;
            setFilterButtonSelected(allButton, filterDifficulty == null);
            setFilterButtonSelected(easyButton, filterDifficulty == Game.Difficulty.EASY);
            setFilterButtonSelected(mediumButton, filterDifficulty == Game.Difficulty.MEDIUM);
            setFilterButtonSelected(hardButton, filterDifficulty == Game.Difficulty.HARD);
            refreshHistory();
        });
        
        return button;
    }
    
    private void setFilterButtonSelected(JButton button, boolean selected) {
        if (selected) {
            button.setBackground(new Color(33, 150, 243)); // Blue
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(66, 66, 66)); // Dark gray
        }
        button.repaint();
    }
    
    private void refreshHistory() {
        // Clear existing content
        contentPanel.removeAll();
        
        // Get filtered history
        List<GameHistory> history = SysData.getInstance().getGameHistory();
        List<GameHistory> filteredHistory = history.stream()
            .filter(h -> currentFilter == null || h.getDifficulty() == currentFilter)
            .sorted((h1, h2) -> h2.getDate().compareTo(h1.getDate())) // Sort by date descending (newest first)
            .collect(Collectors.toList());
        
        // Create game record cards
        for (GameHistory gameHistory : filteredHistory) {
            JPanel card = createGameRecordCard(gameHistory);
            contentPanel.add(card);
            contentPanel.add(Box.createVerticalStrut(15));
        }
        
        // Update total games label
        int totalGames = history.size();
        totalGamesLabel.setText("✓ Total Games Played: " + totalGames);
        totalGamesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        // If no games, show message
        if (filteredHistory.isEmpty()) {
            JLabel emptyLabel = new JLabel("No games found");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            emptyLabel.setForeground(new Color(150, 150, 150));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
            contentPanel.add(emptyLabel);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createGameRecordCard(GameHistory history) {
        // Main card container with shadow and rounded corners
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Draw white rounded background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        // Header section at top
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Left: Difficulty badge and date
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);
        
        JLabel difficultyLabel = createDifficultyBadge(history.getDifficulty());
        leftHeader.add(difficultyLabel);
        
        JLabel dateLabel = new JLabel(history.getFormattedDate());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(66, 66, 66)); // Dark gray
        leftHeader.add(dateLabel);
        
        // Right: Time badge and delete button (side by side)
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightHeader.setOpaque(false);
        
        // Time badge
        JPanel timePanel = createTimeBadge(history.getFormattedDuration());
        rightHeader.add(timePanel);
        
        // Delete button (to the right of timer)
        JButton deleteButton = createDeleteButton();
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this game record?",
                "Delete Game History",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                SysData.getInstance().removeGameHistory(history);
                refreshHistory();
            }
        });
        rightHeader.add(deleteButton);
        
        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        
        // Content panel for card body
        JPanel cardContent = new JPanel();
        cardContent.setLayout(new BoxLayout(cardContent, BoxLayout.Y_AXIS));
        cardContent.setOpaque(false);
        
        // Player information row
        JPanel playerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        playerRow.setOpaque(false);
        playerRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        playerRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Player 1 panel
        JPanel player1Panel = createPlayerPanel("Player 1", history.getPlayer1Name(), 
            new Color(179, 224, 242)); // Light blue (#B3E0F2)
        playerRow.add(player1Panel);
        
        // Player 2 panel
        JPanel player2Panel = createPlayerPanel("Player 2", history.getPlayer2Name(), 
            new Color(225, 190, 231)); // Light purple (#E1BEE7)
        playerRow.add(player2Panel);
        
        // Combined Score bar
        JPanel scorePanel = createScorePanel(history.getCombinedScore());
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        cardContent.add(playerRow);
        cardContent.add(scorePanel);
        
        // Hearts Remaining bar (for all difficulties)
        JPanel heartsPanel = createHeartsPanel(history.getRemainingHearts());
        heartsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardContent.add(heartsPanel);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(cardContent, BorderLayout.CENTER);
        
        // Container with spacing
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        container.add(card);
        
        return container;
    }
    
    /**
     * Creates a time badge with light blue background and clock icon.
     */
    private JPanel createTimeBadge(String timeText) {
        JPanel timePanel = createRoundedPanel(new Color(187, 222, 251), 15); // Light blue (#BBDEFB)
        timePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));
        timePanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        // Set minimum size but allow expansion for longer time strings (e.g., "100:00")
        timePanel.setMinimumSize(new Dimension(75, 30));
        // Don't set maximum size - let it expand to fit content
        
        // Clock icon (blue circle with clock symbol)
        JLabel clockIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(33, 150, 243)); // Blue
                int size = 18;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.fillOval(x, y, size, size);
                
                // Draw clock hands
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.5f));
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                // Hour hand
                g2.drawLine(centerX, centerY, centerX, centerY - 4);
                // Minute hand
                g2.drawLine(centerX, centerY, centerX + 3, centerY);
                
                g2.dispose();
            }
        };
        clockIcon.setPreferredSize(new Dimension(18, 18));
        clockIcon.setOpaque(false);
        timePanel.add(clockIcon);
        
        JLabel durationLabel = new JLabel(timeText);
        durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        durationLabel.setForeground(new Color(33, 150, 243)); // Blue text
        timePanel.add(durationLabel);
        
        return timePanel;
    }
    
    /**
     * Creates a rounded panel with the specified background color.
     */
    private JPanel createRoundedPanel(Color bgColor, int arcSize) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcSize, arcSize);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }
    
    
    private JLabel createDifficultyBadge(Game.Difficulty difficulty) {
        Color bgColor;
        String text;
        
        switch (difficulty) {
            case EASY:
                text = "EASY";
                bgColor = new Color(76, 175, 80); // Green
                break;
            case MEDIUM:
                text = "MEDIUM";
                bgColor = new Color(255, 193, 7); // Yellow/Orange
                break;
            case HARD:
                text = "HARD";
                bgColor = new Color(244, 67, 54); // Red
                break;
            default:
                text = "EASY";
                bgColor = new Color(76, 175, 80);
        }
        
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        badge.setOpaque(false);
        
        return badge;
    }
    
    private JPanel createPlayerPanel(String label, String name, Color bgColor) {
        JPanel panel = createRoundedPanel(bgColor, 12);
        panel.setLayout(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        panel.setPreferredSize(new Dimension(180, 90));
        panel.setMaximumSize(new Dimension(180, 90));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Label in top left
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(new Color(117, 117, 117)); // Dark gray (#757575)
        labelText.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(labelText, BorderLayout.NORTH);
        
        // Name centered below
        JLabel nameText = new JLabel(name);
        nameText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        if (label.equals("Player 1")) {
            nameText.setForeground(new Color(33, 150, 243)); // Blue (#2196F3)
        } else {
            nameText.setForeground(new Color(156, 39, 176)); // Purple (#9C27B0)
        }
        nameText.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(nameText, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the Combined Score panel.
     */
    private JPanel createScorePanel(int score) {
        JPanel panel = createRoundedPanel(new Color(200, 230, 201), 12); // Light green (#C8E6C9)
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Target icon (red circle with target rings)
        JLabel targetIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Outer circle (red)
                g2.setColor(new Color(244, 67, 54)); // Red
                g2.fillOval(centerX - 8, centerY - 8, 16, 16);
                
                // Middle ring (white)
                g2.setColor(Color.WHITE);
                g2.fillOval(centerX - 5, centerY - 5, 10, 10);
                
                // Inner circle (red)
                g2.setColor(new Color(244, 67, 54)); // Red
                g2.fillOval(centerX - 2, centerY - 2, 4, 4);
                
                g2.dispose();
            }
        };
        targetIcon.setPreferredSize(new Dimension(16, 16));
        targetIcon.setOpaque(false);
        panel.add(targetIcon);
        
        JLabel labelText = new JLabel("Combined Score:");
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(new Color(66, 66, 66)); // Dark gray/black
        panel.add(labelText);
        
        // Target icon with score (red circle)
        JLabel scoreIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Outer circle (red)
                g2.setColor(new Color(244, 67, 54)); // Red
                g2.fillOval(centerX - 9, centerY - 9, 18, 18);
                
                // Middle ring (white)
                g2.setColor(Color.WHITE);
                g2.fillOval(centerX - 6, centerY - 6, 12, 12);
                
                // Inner circle (red)
                g2.setColor(new Color(244, 67, 54)); // Red
                g2.fillOval(centerX - 3, centerY - 3, 6, 6);
                
                g2.dispose();
            }
        };
        scoreIcon.setPreferredSize(new Dimension(18, 18));
        scoreIcon.setOpaque(false);
        panel.add(scoreIcon);
        
        JLabel valueText = new JLabel(String.valueOf(score));
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueText.setForeground(new Color(244, 67, 54)); // Red (#F44336)
        panel.add(valueText);
        
        return panel;
    }
    
    /**
     * Creates a small delete button with trash icon.
     */
    private JButton createDeleteButton() {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw trash can icon in red
                g2.setColor(new Color(220, 53, 69)); // Red color
                
                int width = getWidth();
                int height = getHeight();
                int centerX = width / 2;
                int centerY = height / 2;
                
                // Draw trash can body (rectangle)
                int bodyWidth = 10;
                int bodyHeight = 12;
                g2.fillRect(centerX - bodyWidth/2, centerY - bodyHeight/2 + 2, bodyWidth, bodyHeight);
                
                // Draw lid (rectangle on top)
                int lidWidth = 12;
                int lidHeight = 2;
                g2.fillRect(centerX - lidWidth/2, centerY - bodyHeight/2, lidWidth, lidHeight);
                
                // Draw handle (small rectangle on lid)
                int handleWidth = 2;
                int handleHeight = 2;
                g2.fillRect(centerX - lidWidth/2 - 2, centerY - bodyHeight/2 - handleHeight, handleWidth, handleHeight);
                
                // Draw lines on body (to show it's a trash can)
                g2.setStroke(new BasicStroke(1.0f));
                g2.setColor(new Color(255, 255, 255)); // White lines
                g2.drawLine(centerX - bodyWidth/2 + 2, centerY - 2, centerX + bodyWidth/2 - 2, centerY - 2);
                g2.drawLine(centerX - bodyWidth/2 + 2, centerY + 2, centerX + bodyWidth/2 - 2, centerY + 2);
                
                g2.dispose();
            }
        };
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(24, 24));
        button.setMaximumSize(new Dimension(24, 24));
        button.setMinimumSize(new Dimension(24, 24));
        button.setToolTipText("Delete game record");
        return button;
    }
    
    /**
     * Creates the Hearts Remaining panel.
     */
    private JPanel createHeartsPanel(int hearts) {
        JPanel panel = createRoundedPanel(new Color(255, 205, 210), 12); // Light pink (#FFCDD2)
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel labelText = new JLabel("Hearts Remaining:");
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(new Color(66, 66, 66)); // Dark gray/black
        panel.add(labelText);
        
        JLabel valueText = new JLabel(String.valueOf(hearts));
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueText.setForeground(new Color(244, 67, 54)); // Red (#F44336)
        panel.add(valueText);
        
        // Heart symbol (solid red) - drawn as a shape
        JLabel heartSolid = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int width = getWidth();
                int height = getHeight();
                int centerX = width / 2;
                int centerY = height / 2;
                
                // Draw heart shape using two circles and a triangle
                g2.setColor(new Color(244, 67, 54)); // Red
                
                // Left circle of heart (top part)
                g2.fillOval(centerX - 7, centerY - 6, 7, 7);
                // Right circle of heart (top part)
                g2.fillOval(centerX, centerY - 6, 7, 7);
                
                // Triangle (point) of heart (bottom part)
                int[] xPoints = {centerX - 7, centerX, centerX + 7};
                int[] yPoints = {centerY - 1, centerY + 5, centerY - 1};
                g2.fillPolygon(xPoints, yPoints, 3);
                
                g2.dispose();
            }
        };
        heartSolid.setPreferredSize(new Dimension(18, 16));
        heartSolid.setOpaque(false);
        panel.add(heartSolid);
        
        return panel;
    }
    
    
    /**
     * Updates the responsive layout based on scale factor.
     * 
     * @param scaleFactor The scaling factor from MainView
     */
    public void updateResponsiveLayout(double scaleFactor) {
        // Scale home button font
        if (homeButton != null) {
            int buttonFontSize = (int) (12 * scaleFactor);
            buttonFontSize = Math.max(11, Math.min(14, buttonFontSize));
            homeButton.setFont(new Font("Segoe UI", Font.PLAIN, buttonFontSize));
            
            int padding = (int) (6 * scaleFactor);
            padding = Math.max(5, Math.min(8, padding));
            int horizontalPadding = (int) (12 * scaleFactor);
            horizontalPadding = Math.max(10, Math.min(15, horizontalPadding));
            // Maintain rectangle border with responsive padding
            homeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(padding, horizontalPadding, padding, horizontalPadding)
            ));
        }
        
        revalidate();
        repaint();
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
     * Refreshes the history display (called when returning to this panel).
     */
    public void refresh() {
        refreshHistory();
    }
}
