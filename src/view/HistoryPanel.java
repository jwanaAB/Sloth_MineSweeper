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
        setBackground(Color.WHITE);

        // Home button - rectangle shape, top left
        homeButton = new JButton("<- Home");
        homeButton.setFocusPainted(false);
        homeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        homeButton.setBackground(new Color(245, 245, 245));
        homeButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Rectangle border
        homeButton.setBorderPainted(true);
        homeButton.setContentAreaFilled(true);
        homeButton.setOpaque(true);
        homeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        // Rectangle button size
        homeButton.setPreferredSize(new Dimension(90, 32));
        homeButton.setMaximumSize(new Dimension(90, 32));
        homeButton.setMinimumSize(new Dimension(90, 32));

        // Top bar with home button, title, and filters all together
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        topBar.add(homeButton, BorderLayout.WEST);

        // Title panel
        JPanel titlePanel = createTitlePanel();
        
        // Filter buttons panel
        filterPanel = createFilterPanel();
        
        // Combine title and filters in a compact header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        headerPanel.add(titlePanel);
        headerPanel.add(filterPanel);
        
        topBar.add(headerPanel, BorderLayout.CENTER);
        
        // Content panel for game records
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        // Scroll pane for content - now takes more space
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Total games label at bottom
        totalGamesLabel = new JLabel();
        totalGamesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        totalGamesLabel.setForeground(new Color(100, 100, 100));
        totalGamesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalGamesLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        totalGamesLabel.setOpaque(true);
        totalGamesLabel.setBackground(new Color(230, 240, 255));
        
        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(totalGamesLabel, BorderLayout.SOUTH);
        
        // Load and display history
        refreshHistory();
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Icon (purple square)
        JLabel iconLabel = new JLabel("■");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(new Color(156, 39, 176)); // Purple
        
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
        panel.setBackground(Color.WHITE);
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
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        
        // Set initial state (unselected)
        setFilterButtonSelected(button, false);
        
        button.addActionListener(e -> {
            currentFilter = difficulty;
            setFilterButtonSelected(allButton, difficulty == null);
            setFilterButtonSelected(easyButton, difficulty == Game.Difficulty.EASY);
            setFilterButtonSelected(mediumButton, difficulty == Game.Difficulty.MEDIUM);
            setFilterButtonSelected(hardButton, difficulty == Game.Difficulty.HARD);
            refreshHistory();
        });
        
        return button;
    }
    
    private void setFilterButtonSelected(JButton button, boolean selected) {
        if (selected) {
            button.setBackground(new Color(33, 150, 243)); // Blue
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(240, 240, 240)); // Light gray
            button.setForeground(new Color(100, 100, 100));
        }
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
        // Outer container for the entire card entry (includes header outside card)
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Top row: Difficulty badge, Date, and Delete button (outside the card)
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Left: Difficulty badge and date
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);
        
        // Difficulty badge (rounded)
        JLabel difficultyLabel = createDifficultyBadge(history.getDifficulty());
        leftHeader.add(difficultyLabel);
        
        // Date
        JLabel dateLabel = new JLabel(history.getFormattedDate());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(100, 100, 100));
        leftHeader.add(dateLabel);
        
        // Right: Delete button (red trash icon)
        JButton deleteButton = createTrashButton();
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
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(deleteButton);
        
        headerRow.add(leftHeader, BorderLayout.WEST);
        headerRow.add(rightPanel, BorderLayout.EAST);
        
        // The actual card (white with rounded corners)
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        // Time display in top-right corner as small rectangle
        JPanel timePanel = createRoundedPanel(new Color(173, 216, 255), 8);
        timePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
        timePanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        timePanel.setPreferredSize(new Dimension(70, 24));
        timePanel.setMaximumSize(new Dimension(70, 24));
        
        JLabel clockIcon = new JLabel("\u25A0"); // Square symbol (black square)
        clockIcon.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        clockIcon.setForeground(new Color(60, 60, 60));
        timePanel.add(clockIcon);
        
        JLabel durationLabel = new JLabel(history.getFormattedDuration());
        durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        durationLabel.setForeground(new Color(60, 60, 60));
        timePanel.add(durationLabel);
        
        // Top panel to hold time in corner
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topPanel.setOpaque(false);
        topPanel.add(timePanel);
        
        // Content panel for card body
        JPanel cardContent = new JPanel();
        cardContent.setLayout(new BoxLayout(cardContent, BoxLayout.Y_AXIS));
        cardContent.setOpaque(false);
        
        // Player information row
        JPanel playerRow = new JPanel();
        playerRow.setLayout(new BoxLayout(playerRow, BoxLayout.X_AXIS));
        playerRow.setOpaque(false);
        playerRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        playerRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Player 1
        JPanel player1Panel = createPlayerPanel("Player 1", history.getPlayer1Name(), new Color(173, 216, 230)); // Light blue
        playerRow.add(Box.createHorizontalGlue());
        playerRow.add(player1Panel);
        playerRow.add(Box.createHorizontalStrut(15));
        
        // Player 2
        JPanel player2Panel = createPlayerPanel("Player 2", history.getPlayer2Name(), new Color(221, 160, 221)); // Light purple
        playerRow.add(player2Panel);
        playerRow.add(Box.createHorizontalGlue());
        
        // Combined Score row
        JPanel scorePanel = createInfoPanel("Combined Score:", 
            String.valueOf(history.getCombinedScore()), 
            "🎯", 
            new Color(144, 238, 144)); // Light green
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Hearts Remaining row
        JPanel heartsPanel = createInfoPanel("Hearts Remaining:", 
            String.valueOf(history.getRemainingHearts()), 
            "❤", 
            new Color(255, 182, 193)); // Light pink
        heartsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cardContent.add(playerRow);
        cardContent.add(scorePanel);
        cardContent.add(heartsPanel);
        
        // Add top panel with time in corner and content to center
        card.add(topPanel, BorderLayout.NORTH);
        card.add(cardContent, BorderLayout.CENTER);
        
        // Add header and card to container
        container.add(headerRow);
        container.add(card);
        
        return container;
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
    
    private JButton createTrashButton() {
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
                int bodyWidth = 12;
                int bodyHeight = 14;
                g2.fillRect(centerX - bodyWidth/2, centerY - bodyHeight/2 + 2, bodyWidth, bodyHeight);
                
                // Draw lid (rectangle on top)
                int lidWidth = 14;
                int lidHeight = 2;
                g2.fillRect(centerX - lidWidth/2, centerY - bodyHeight/2, lidWidth, lidHeight);
                
                // Draw handle (small rectangle on lid)
                int handleWidth = 2;
                int handleHeight = 3;
                g2.fillRect(centerX - lidWidth/2 - 2, centerY - bodyHeight/2 - handleHeight, handleWidth, handleHeight);
                
                // Draw lines on body (to show it's a trash can)
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(255, 255, 255)); // White lines
                g2.drawLine(centerX - bodyWidth/2 + 3, centerY - 2, centerX + bodyWidth/2 - 3, centerY - 2);
                g2.drawLine(centerX - bodyWidth/2 + 3, centerY + 2, centerX + bodyWidth/2 - 3, centerY + 2);
                
                g2.dispose();
            }
        };
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(30, 30));
        button.setMaximumSize(new Dimension(30, 30));
        button.setMinimumSize(new Dimension(30, 30));
        button.setToolTipText("Delete game record");
        return button;
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
        JPanel panel = createRoundedPanel(bgColor, 15);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        panel.setPreferredSize(new Dimension(160, 85));
        panel.setMaximumSize(new Dimension(160, 85));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Label in top left
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(new Color(60, 60, 60));
        labelText.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(labelText, BorderLayout.NORTH);
        
        // Name centered below
        JLabel nameText = new JLabel(name);
        nameText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameText.setForeground(new Color(40, 40, 40));
        nameText.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(nameText, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoPanel(String label, String value, String icon, Color bgColor) {
        JPanel panel = createRoundedPanel(bgColor, 15);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Add horizontal glue to center content
        panel.add(Box.createHorizontalGlue());
        
        if (icon != null && !icon.isEmpty()) {
            String iconChar = "";
            if (icon.equals("🎯")) {
                iconChar = "\u25A0"; // Square symbol (black square)
            } else if (icon.equals("❤")) {
                iconChar = "\u2665"; // Heart symbol
            } else {
                iconChar = "\u25A0"; // Default to square
            }
            JLabel iconLabel = new JLabel(iconChar);
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            iconLabel.setForeground(new Color(60, 60, 60));
            iconLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
            panel.add(iconLabel);
            panel.add(Box.createHorizontalStrut(8));
        }
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(new Color(60, 60, 60));
        labelText.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(labelText);
        
        // For score, add target icon before value
        if (icon != null && icon.equals("🎯")) {
            panel.add(Box.createHorizontalStrut(5));
            JLabel targetIcon = new JLabel("\u25A0"); // Square symbol
            targetIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            targetIcon.setForeground(new Color(200, 0, 0)); // Red color
            targetIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
            panel.add(targetIcon);
        }
        
        panel.add(Box.createHorizontalStrut(5));
        
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueText.setForeground(new Color(40, 40, 40));
        valueText.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(valueText);
        
        // For hearts, add heart icon after value
        if (icon != null && icon.equals("❤")) {
            panel.add(Box.createHorizontalStrut(5));
            JLabel heartIcon = new JLabel("\u2665"); // Heart symbol
            heartIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            heartIcon.setForeground(new Color(200, 0, 0)); // Red color
            heartIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
            panel.add(heartIcon);
        }
        
        // Add horizontal glue to center content
        panel.add(Box.createHorizontalGlue());
        
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
