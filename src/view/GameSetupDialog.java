package view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GameSetupDialog extends JDialog {
    private final JTextField player1TextField;
    private final JTextField player2TextField;
    private final GradientButton startButton;
    private boolean confirmed = false;
    private int selectedDifficulty = 1;
    private boolean playWithAI = false; // Track if AI mode is selected
    private DifficultyOption[] difficultyOptions;
    private GameModeOption twoPlayersOption;
    private GameModeOption vsAIOption;
    private JLabel infoText = new JLabel("Both players will share 10 hearts total");
    private RoundedPanel card;
    private JScrollPane scrollPane;
    private JPanel difficultyRow;
    private JPanel infoWrapper;
    private JPanel buttonHolder;
    private GradientPanel background;
    private JLabel player2Label; // Keep reference to show/hide


    public GameSetupDialog(JFrame parent) {
        super(parent, "Game Setup", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);

        // Get screen dimensions for percentage-based sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // ===== SIZE SETTINGS (Percentage-based) =====
        // Width: Change 0.60 to adjust width percentage (0.50 = 50%, 0.70 = 70%, etc.)
        double widthPercentage = 0.60; // 60% of screen width
        int minWidth = 950;  // Minimum width in pixels
        int maxWidth = 1200; // Maximum width in pixels
        int dialogWidth = Math.max(minWidth, Math.min((int) (screenSize.width * widthPercentage), maxWidth));
        
        // Height: Change 0.80 to adjust height percentage (0.60 = 60%, 0.90 = 90%, etc.)
        double heightPercentage = 0.85; // 80% of screen height
        int maxHeightPixels = 900; // Maximum height in pixels (optional constraint)
        int dialogHeight = Math.min(maxHeightPixels, (int) (screenSize.height * heightPercentage));
        setPreferredSize(new Dimension(dialogWidth, dialogHeight));
        setSize(dialogWidth, dialogHeight);
        setMinimumSize(new Dimension(900, 700)); // Increased minimum size to ensure all content is visible
        setMaximumSize(new Dimension(1200, 850)); // Prevent dialog from being too large

        background = new GradientPanel();
        int bgPadding = 20; // Reduced padding to maximize usable space
        background.setBorder(new EmptyBorder(bgPadding, bgPadding, bgPadding, bgPadding));
        background.setLayout(new BorderLayout());

        card = new RoundedPanel(32);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Calculate card width accounting for background padding and scrollbar
        int cardWidth = dialogWidth - (bgPadding * 2) - 30; // Account for padding and scrollbar margin
        card.setMaximumSize(new Dimension(cardWidth, Integer.MAX_VALUE));
        // Set a reasonable preferred width, let height be calculated by BoxLayout
        card.setPreferredSize(new Dimension(cardWidth, 800)); // Estimate height, will adjust

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setForeground(new Color(110, 110, 150));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(150, 150, 180), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        backButton.setBackground(new Color(255, 255, 255, 200));
        backButton.setOpaque(true);
        backButton.setContentAreaFilled(true);
        backButton.setFocusPainted(false);
        backButton.setMargin(new Insets(5, 10, 5, 10));
        backButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        // Add hover effect
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(new Color(240, 240, 255));
                backButton.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(120, 120, 180), 2, true),
                    new EmptyBorder(4, 9, 4, 9)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(new Color(255, 255, 255, 200));
                backButton.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(150, 150, 180), 1, true),
                    new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });

        JLabel title = new JLabel("Setup Game");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        title.setForeground(new Color(76, 63, 125));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Game Mode Section
        JLabel gameModeLabel = createSectionLabel("Game Mode");
        
        JPanel gameModeRow = new JPanel(new GridLayout(1, 2, 10, 0));
        gameModeRow.setOpaque(false);
        gameModeRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        int gameModeRowWidth = cardWidth - 50;
        gameModeRow.setMaximumSize(new Dimension(gameModeRowWidth, 100));
        gameModeRow.setPreferredSize(new Dimension(gameModeRowWidth, 100));
        
        twoPlayersOption = new GameModeOption("Two Players", "Play with a friend", "👥", true);
        vsAIOption = new GameModeOption("With AI", "Play against computer", "🤖", false);
        
        twoPlayersOption.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectGameMode(false);
            }
        });
        
        vsAIOption.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectGameMode(true);
            }
        });
        
        gameModeRow.add(twoPlayersOption);
        gameModeRow.add(vsAIOption);

        // Create text fields with percentage-based sizing
        int fieldWidth = cardWidth - 50; // Account for card padding
        player1TextField = createStyledTextField("Player 1 name", fieldWidth);
        player1TextField.setText("Player 1"); // Set default value
        player2TextField = createStyledTextField("Player 2 name", fieldWidth);
        player2TextField.setText("player2"); // Set default value

        JLabel player1Label = createSectionLabel("Player 1 Name");
        player2Label = createSectionLabel("Player 2 Name");
        JLabel difficultyLabel = createSectionLabel("Difficulty Level");
        
        // Initialize with Two Players mode selected by default - must be after player2Label is created
        selectGameMode(false);

        difficultyRow = new JPanel(new GridLayout(1, 3, 12, 0)); // Slightly increased gap between cards
        difficultyRow.setOpaque(false);
        difficultyRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Ensure difficulty row fits within card width - use full available width
        int diffRowWidth = cardWidth - 48; // Account for card padding (24px on each side)
        difficultyRow.setMaximumSize(new Dimension(diffRowWidth, 140));
        difficultyRow.setPreferredSize(new Dimension(diffRowWidth, 140));

        difficultyOptions = new DifficultyOption[]{
            new DifficultyOption("Easy", "9×9", 10, 10, 1, new Color(97, 207, 145)),
            new DifficultyOption("Medium", "13×13", 8, 10, 2, new Color(253, 176, 95)),
            new DifficultyOption("Hard", "16×16", 6, 10, 3, new Color(255, 105, 120))
        };

        for (DifficultyOption option : difficultyOptions) {
            option.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectDifficulty(option.getDifficulty());
                }
            });
            difficultyRow.add(option);
        }
        
        // Initialize with default difficulty (Easy = 1)
        selectDifficulty(1);

        infoWrapper = new JPanel();
        infoWrapper.setLayout(new BoxLayout(infoWrapper, BoxLayout.X_AXIS));
        infoWrapper.setOpaque(true);
        infoWrapper.setBackground(new Color(248, 251, 255));
        infoWrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(155, 190, 255), 2, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        infoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        int infoWidth = cardWidth - 50; // Account for card padding
        infoWrapper.setMaximumSize(new Dimension(infoWidth, 60));
        infoWrapper.setPreferredSize(new Dimension(infoWidth, 60));

        JLabel infoIcon = new JLabel("i", SwingConstants.CENTER);
        infoIcon.setPreferredSize(new Dimension(24, 24));
        infoIcon.setFont(new Font("Segoe UI", Font.BOLD, 15));
        infoIcon.setOpaque(true);
        infoIcon.setBackground(new Color(35, 123, 255));
        infoIcon.setForeground(Color.WHITE);
        infoIcon.setBorder(new LineBorder(new Color(35, 123, 255), 1, true));

        infoText = new JLabel("<html><body style='width: " + (infoWidth - 60) + "px'>Both players will share 10 hearts total</body></html>");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoText.setForeground(new Color(35, 89, 160));
        infoText.setBorder(new EmptyBorder(0, 10, 0, 0));

        infoWrapper.add(infoIcon);
        infoWrapper.add(infoText);

        startButton = new GradientButton("▶ Start Game");
        startButton.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(180, 44));
        startButton.setBorder(new EmptyBorder(12, 14, 12, 14));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        startButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        player1TextField.addActionListener(e -> {
            if (playWithAI) {
                // If AI mode, validate and start
                if (validateInput()) {
                    confirmed = true;
                    dispose();
                }
            } else {
                player2TextField.requestFocus();
            }
        });
        player2TextField.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        // Create a wrapper panel for the back button to center it
        JPanel backButtonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        backButtonWrapper.setOpaque(false);
        backButtonWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButtonWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        backButtonWrapper.add(backButton);
        card.add(backButtonWrapper);
        card.add(Box.createVerticalStrut(4));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(gameModeLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(gameModeRow);
        card.add(Box.createVerticalStrut(12));
        card.add(player1Label);
        card.add(player1TextField);
        card.add(Box.createVerticalStrut(10));
        card.add(player2Label);
        card.add(player2TextField);
        card.add(Box.createVerticalStrut(12));
        card.add(difficultyLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(difficultyRow);
        card.add(Box.createVerticalStrut(14));
        card.add(infoWrapper);
        card.add(Box.createVerticalStrut(20)); // Spacing before button

        buttonHolder = new JPanel();
        buttonHolder.setOpaque(false);
        buttonHolder.setLayout(new BoxLayout(buttonHolder, BoxLayout.X_AXIS));
        int buttonHolderWidth = cardWidth - 80; // Account for card padding
        buttonHolder.setMaximumSize(new Dimension(buttonHolderWidth, 60));
        buttonHolder.setPreferredSize(new Dimension(buttonHolderWidth, 60));
        buttonHolder.add(Box.createHorizontalGlue());
        buttonHolder.add(startButton);
        buttonHolder.add(Box.createHorizontalGlue());
        card.add(buttonHolder);
        // Add minimal bottom spacing for visual balance
        card.add(Box.createVerticalStrut(15));

        // Wrap card in scroll pane for scrollability
        // Let the card calculate its preferred size naturally based on content
        scrollPane = new JScrollPane(card);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        // Calculate proper height - fit content, don't force large height
        int scrollPaneHeight = Math.min(dialogHeight - (bgPadding * 2) - 10, 750); // Account for background padding, max 750px
        // Ensure scroll pane uses available space and allows scrolling
        scrollPane.setPreferredSize(new Dimension(cardWidth + 30, scrollPaneHeight)); // Add buffer for scrollbar
        // Allow scroll pane to resize with the dialog
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        // Force initial layout to ensure card size is calculated
        SwingUtilities.invokeLater(() -> {
            card.revalidate();
            scrollPane.revalidate();
            scrollPane.repaint();
        });
        
        // Use BorderLayout center to allow the scrollPane to fill available space
        background.add(scrollPane, BorderLayout.CENTER);

        setContentPane(background);
        // Don't use pack() as it overrides our size - use setSize instead
        // setSize was already called above, just position it now
        
        // ===== POSITION SETTINGS (Percentage-based) =====
        // X Position: Change to position horizontally
        // Option 1: Center horizontally (current)
        int x = (screenSize.width - dialogWidth) / 2;
        // Option 2: Use percentage from left (uncomment to use):
        // double xPercentage = 0.20; // 20% from left edge
        // int x = (int) (screenSize.width * xPercentage);
        
        // Y Position: Match main menu vertical position (centered)
        // Main menu uses setLocationRelativeTo(null) which centers vertically
        // This matches the main menu's vertical position
        int y = (screenSize.height - dialogHeight) / 3;
        // Alternative: Use percentage from top (uncomment to use):
        // double yPercentage = 0.10; // 10% from top (change this value: 0.10 = 10%, 0.20 = 20%, etc.)
        // int y = (int) (screenSize.height * yPercentage);
        
        setLocation(x, y);
        
        // Add resize listener to handle dynamic resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateResponsiveLayout();
            }
        });
    }
    
    /**
     * Updates the layout when dialog is resized.
     */
    private void updateResponsiveLayout() {
        if (card == null || scrollPane == null || background == null) {
            return;
        }
        
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        
        if (currentWidth <= 0 || currentHeight <= 0) {
            return;
        }
        
        // Calculate new card width based on current dialog size (accounting for padding)
        int bgPadding = 20; // Match the padding used in constructor
        int newCardWidth = currentWidth - (bgPadding * 2) - 20; // Account for padding and scrollbar margin
        
        // Update card maximum width - this allows BoxLayout to respect the width constraint
        card.setMaximumSize(new Dimension(newCardWidth, Integer.MAX_VALUE));
        card.setPreferredSize(new Dimension(newCardWidth, card.getPreferredSize().height));
        
        // Update text field sizes
        if (player1TextField != null) {
            int fieldWidth = newCardWidth - 50; // Account for card padding
            Dimension fieldSize = new Dimension(fieldWidth, 42);
            player1TextField.setMaximumSize(fieldSize);
            player1TextField.setPreferredSize(fieldSize);
        }
        if (player2TextField != null) {
            int fieldWidth = newCardWidth - 50; // Account for card padding
            Dimension fieldSize = new Dimension(fieldWidth, 42);
            player2TextField.setMaximumSize(fieldSize);
            player2TextField.setPreferredSize(fieldSize);
        }
        
        // Update difficulty row - ensure it fits
        // GridLayout will automatically divide the width equally among 3 cards
        if (difficultyRow != null) {
            int diffRowWidth = newCardWidth - 50; // Account for card padding
            difficultyRow.setMaximumSize(new Dimension(diffRowWidth, 130));
            difficultyRow.setPreferredSize(new Dimension(diffRowWidth, 130));
        }
        
        // Update info wrapper
        if (infoWrapper != null) {
            int infoWidth = newCardWidth - 50; // Account for card padding
            infoWrapper.setMaximumSize(new Dimension(infoWidth, 60));
            infoWrapper.setPreferredSize(new Dimension(infoWidth, 60));
            // Update info text width for word wrapping
            if (infoText != null) {
                // Rebuild text with current mode
                int hearts;
                switch (selectedDifficulty) {
                    case 1: hearts = 10; break;
                    case 2: hearts = 8; break;
                    case 3: hearts = 6; break;
                    default: hearts = 10; break;
                }
                String text = playWithAI ? 
                    "You and the AI will share " + hearts + " hearts total" :
                    "Both players will share " + hearts + " hearts total";
                infoText.setText("<html><body style='width: " + Math.max(200, infoWidth - 50) + "px'>" + text + "</body></html>");
            }
        }
        
        // Update button holder
        if (buttonHolder != null) {
            int buttonHolderWidth = newCardWidth - 80; // Account for card padding
            buttonHolder.setMaximumSize(new Dimension(buttonHolderWidth, 55));
            buttonHolder.setPreferredSize(new Dimension(buttonHolderWidth, 55));
        }
        
        // Calculate available height for scroll pane - use most of the dialog height
        int availableHeight = currentHeight - (bgPadding * 2) - 10; // Account for background padding
        
        // Update scroll pane to fill available space
        // Set preferred size so it uses the available space, but allow it to grow/shrink
        scrollPane.setPreferredSize(new Dimension(newCardWidth + 20, availableHeight)); // Add buffer for scrollbar
        
        // Force layout update - start from the dialog and work down
        SwingUtilities.invokeLater(() -> {
            if (card != null) {
                card.revalidate();
            }
            if (scrollPane != null) {
                scrollPane.revalidate();
            }
            if (background != null) {
                background.revalidate();
            }
            revalidate();
            repaint();
        });
    }

    private JTextField createStyledTextField(String placeholder, int width) {
        JTextField field = new JTextField();
        Dimension fieldSize = new Dimension(width, 42);
        field.setMaximumSize(fieldSize);
        field.setPreferredSize(fieldSize);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(202, 210, 255), 2, true),
            new EmptyBorder(9, 12, 9, 12)
        ));
        field.setBackground(new Color(250, 251, 255));
        field.setForeground(new Color(60, 60, 95));
        field.setCaretColor(new Color(76, 63, 125));
        field.setText("");
        field.setToolTipText(placeholder);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(116, 107, 150));
        label.setBorder(new EmptyBorder(10, 0, 5, 0));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void selectDifficulty(int difficulty) {
        selectedDifficulty = difficulty;
        for (DifficultyOption option : difficultyOptions) {
            option.setSelected(option.getDifficulty() == difficulty);
        }
        // Update hearts text based on selected difficulty
        updateHeartsText(difficulty);
    }
    
    private void selectGameMode(boolean aiMode) {
        playWithAI = aiMode;
        twoPlayersOption.setSelected(!aiMode);
        vsAIOption.setSelected(aiMode);
        
        // Show/hide Player 2 field based on mode (only if already created)
        if (player2Label != null) {
            player2Label.setVisible(!aiMode);
        }
        if (player2TextField != null) {
            player2TextField.setVisible(!aiMode);
        }
        
        // Update info text
        updateHeartsText(selectedDifficulty);
        
        // Revalidate to update layout
        if (card != null) {
            card.revalidate();
            card.repaint();
        }
    }
    
    private void updateHeartsText(int difficulty) {
        int hearts;
        switch (difficulty) {
            case 1: // Easy
                hearts = 10;
                break;
            case 2: // Medium
                hearts = 8;
                break;
            case 3: // Hard
                hearts = 6;
                break;
            default:
                hearts = 10;
                break;
        }
        // Get current info wrapper width for word wrapping
        int infoWidth = infoWrapper != null ? infoWrapper.getWidth() : 400;
        if (infoWidth <= 0) {
            infoWidth = (int) (getWidth() * 0.88 * 0.88); // Estimate based on dialog width
        }
        String text;
        if (playWithAI) {
            text = "You and the AI will share " + hearts + " hearts total";
        } else {
            text = "Both players will share " + hearts + " hearts total";
        }
        infoText.setText("<html><body style='width: " + Math.max(200, infoWidth - 60) + "px'>" + text + "</body></html>");
    }

    private boolean validateInput() {
        String player1Name = player1TextField.getText().trim();

        if (player1Name.isEmpty()) {
            ErrorDialog.showErrorDialog(this, "Please enter Player 1 name.");
            player1TextField.requestFocus();
            return false;
        }
        if (player1Name.length() > 20) {
            ErrorDialog.showErrorDialog(this, "Player 1 name must be 20 characters or less.");
            player1TextField.requestFocus();
            return false;
        }

        // Only validate Player 2 if not in AI mode
        if (!playWithAI) {
            String player2Name = player2TextField.getText().trim();
            if (player2Name.isEmpty()) {
                ErrorDialog.showErrorDialog(this, "Please enter Player 2 name.");
                player2TextField.requestFocus();
                return false;
            }
            if (player2Name.length() > 20) {
                ErrorDialog.showErrorDialog(this, "Player 2 name must be 20 characters or less.");
                player2TextField.requestFocus();
                return false;
            }
            if (player1Name.equalsIgnoreCase(player2Name)) {
                ErrorDialog.showErrorDialog(this, "Player 1 and Player 2 must have different names.");
                player2TextField.requestFocus();
                return false;
            }
        }

        return true;
    }

    public String getPlayer1Name() {
        return player1TextField.getText().trim();
    }

    public String getPlayer2Name() {
        if (playWithAI) {
            return "SlothAI"; // Return AI name when in AI mode
        }
        return player2TextField.getText().trim();
    }
    
    public boolean isPlayWithAI() {
        return playWithAI;
    }

    public int getDifficulty() {
        return selectedDifficulty;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(
                0, 0, new Color(209, 224, 255),
                getWidth(), getHeight(), new Color(255, 208, 239)
            );
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color fillColor;

        RoundedPanel(int cornerRadius) {
            this(cornerRadius, Color.WHITE);
        }

        RoundedPanel(int cornerRadius, Color fillColor) {
            this.cornerRadius = cornerRadius;
            this.fillColor = fillColor;
            setOpaque(false);
            setBorder(new EmptyBorder(24, 24, 24, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, cornerRadius + 6, cornerRadius + 6);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }

    private static class DifficultyOption extends JPanel {
        private final int difficulty;
        private boolean selected;
        private final Color accentColor;
        private final int filledHearts;
        private final int totalHearts;

        DifficultyOption(String title, String grid, int filledHearts, int totalHearts, int difficulty, Color accentColor) {
            this.difficulty = difficulty;
            this.accentColor = accentColor;
            this.filledHearts = filledHearts;
            this.totalHearts = totalHearts;
            setOpaque(false);
            setBorder(new EmptyBorder(12, 12, 12, 12));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            // Calculate size dynamically to fit 3 cards with gaps
            // Will be set by parent container, but set a reasonable default
            Dimension boxSize = new Dimension(140, 125);
            setPreferredSize(boxSize);
            setMinimumSize(new Dimension(100, 110));
            // Don't set maximum size - let GridLayout control the width
            setAlignmentY(Component.TOP_ALIGNMENT);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
            titleLabel.setForeground(new Color(78, 66, 120));

            JLabel gridLabel = new JLabel(grid);
            gridLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            gridLabel.setForeground(new Color(116, 107, 150));
            gridLabel.setBorder(new EmptyBorder(3, 0, 10, 0));

            JPanel heartsPanel = createHeartsPanel();
            heartsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            add(titleLabel);
            add(gridLabel);
            add(heartsPanel);

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        private JPanel createHeartsPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            panel.setOpaque(false);
            for (int i = 0; i < totalHearts; i++) {
                JLabel heart = new JLabel("\u2665");
                heart.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                heart.setForeground(i < filledHearts ? new Color(208, 45, 85) : new Color(191, 196, 214));
                panel.add(heart);
            }
            return panel;
        }

        int getDifficulty() {
            return difficulty;
        }

        void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = selected ? new Color(241, 249, 244) : new Color(255, 255, 255, 220);
            Color border = selected ? accentColor : new Color(220, 225, 245);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 24, 24);
            g2.dispose();
        }
    }
    
    /**
     * Game Mode Option component (Two Players or vs AI)
     */
    private static class GameModeOption extends JPanel {
        private boolean selected;
        private final Color selectedColor = new Color(138, 43, 226); // Purple color for AI mode
        
        GameModeOption(String title, String subtitle, String icon, boolean initiallySelected) {
            this.selected = initiallySelected;
            
            setOpaque(false);
            setBorder(new EmptyBorder(15, 15, 15, 15));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Icon label
            JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Title label
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            titleLabel.setForeground(new Color(78, 66, 120));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(new EmptyBorder(8, 0, 4, 0));
            
            // Subtitle label
            JLabel subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitleLabel.setForeground(new Color(116, 107, 150));
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            add(iconLabel);
            add(titleLabel);
            add(subtitleLabel);
            
            setSelected(initiallySelected);
        }
        
        void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (selected) {
                // Selected state - purple border and light purple background
                Color fill = new Color(245, 240, 255); // Light purple background
                Color border = selectedColor; // Purple border
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            } else {
                // Unselected state - light grey border and white background
                Color fill = new Color(255, 255, 255, 220);
                Color border = new Color(220, 225, 245);
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
            
            g2.dispose();
        }
    }

    private static class GradientButton extends JButton {
        private final Color startColor = new Color(78, 199, 154);
        private final Color endColor = new Color(64, 120, 255);

        GradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 22, 22);
            g2.dispose();
            super.paintComponent(g);
        }

    }
}
