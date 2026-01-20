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
        // Use slightly larger width percentage but still reasonable
        int dialogWidth = Math.min((int) (screenSize.width * 0.70), 650); // 45% of screen width, max 650px
        int minWidth = 400; // Minimum width for small screens
        dialogWidth = Math.max(dialogWidth, minWidth);
        
        // Calculate height based on content - allow up to 90% of screen height
        int dialogHeight = Math.min((int) (screenSize.height * 0.92), 750); // 90% max, but will fit content
        setPreferredSize(new Dimension(dialogWidth, dialogHeight));
        setSize(dialogWidth, dialogHeight);

        background = new GradientPanel();
        int bgPadding = 20; // Reduced padding
        background.setBorder(new EmptyBorder(bgPadding, bgPadding, bgPadding, bgPadding));
        background.setLayout(new BorderLayout());

        card = new RoundedPanel(32);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Calculate card width accounting for background padding
        int cardWidth = dialogWidth - (bgPadding * 2) - 10; // Account for padding and some margin
        card.setMaximumSize(new Dimension(cardWidth, Integer.MAX_VALUE));
        card.setPreferredSize(new Dimension(cardWidth, card.getPreferredSize().height));

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
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        title.setForeground(new Color(76, 63, 125));
        title.setBorder(new EmptyBorder(0, 0, 8, 0));

        // Game Mode Section
        JLabel gameModeLabel = createSectionLabel("Game Mode");
        
        JPanel gameModeRow = new JPanel(new GridLayout(1, 2, 10, 0));
        gameModeRow.setOpaque(false);
        gameModeRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        int gameModeRowWidth = cardWidth - 50;
        int gameModeRowHeight = 80; // Increased to accommodate text without cropping
        gameModeRow.setMaximumSize(new Dimension(gameModeRowWidth, gameModeRowHeight));
        gameModeRow.setPreferredSize(new Dimension(gameModeRowWidth, gameModeRowHeight));
        
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

        difficultyRow = new JPanel(new GridLayout(1, 3, 10, 0));
        difficultyRow.setOpaque(false);
        difficultyRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Ensure difficulty row fits within card width (account for card padding ~48px total)
        int diffRowWidth = cardWidth - 50; // Leave some margin for card padding
        difficultyRow.setMaximumSize(new Dimension(diffRowWidth, 100));
        difficultyRow.setPreferredSize(new Dimension(diffRowWidth, 100));

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
            new EmptyBorder(8, 12, 8, 12)
        ));
        infoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        int infoWidth = cardWidth - 50; // Account for card padding
        infoWrapper.setMaximumSize(new Dimension(infoWidth, 50));
        infoWrapper.setPreferredSize(new Dimension(infoWidth, 50));

        JLabel infoIcon = new JLabel("i", SwingConstants.CENTER);
        infoIcon.setPreferredSize(new Dimension(20, 20));
        infoIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoIcon.setOpaque(true);
        infoIcon.setBackground(new Color(35, 123, 255));
        infoIcon.setForeground(Color.WHITE);
        infoIcon.setBorder(new LineBorder(new Color(35, 123, 255), 1, true));

        infoText = new JLabel("<html><body style='width: " + (infoWidth - 60) + "px'>Both players will share 10 hearts total</body></html>");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoText.setForeground(new Color(35, 89, 160));
        infoText.setBorder(new EmptyBorder(0, 8, 0, 0));

        infoWrapper.add(infoIcon);
        infoWrapper.add(infoText);

        startButton = new GradientButton("▶ Start Game");
        startButton.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(160, 38));
        startButton.setBorder(new EmptyBorder(10, 12, 10, 12));
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
        backButtonWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        backButtonWrapper.add(backButton);
        card.add(backButtonWrapper);
        card.add(Box.createVerticalStrut(3));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(gameModeLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(gameModeRow);
        card.add(Box.createVerticalStrut(8));
        card.add(player1Label);
        card.add(player1TextField);
        card.add(Box.createVerticalStrut(6));
        card.add(player2Label);
        card.add(player2TextField);
        card.add(Box.createVerticalStrut(8));
        card.add(difficultyLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(difficultyRow);
        card.add(Box.createVerticalStrut(10));
        card.add(infoWrapper);
        card.add(Box.createVerticalStrut(12));

        buttonHolder = new JPanel();
        buttonHolder.setOpaque(false);
        buttonHolder.setLayout(new BoxLayout(buttonHolder, BoxLayout.X_AXIS));
        int buttonHolderWidth = cardWidth - 80; // Account for card padding
        buttonHolder.setMaximumSize(new Dimension(buttonHolderWidth, 45));
        buttonHolder.setPreferredSize(new Dimension(buttonHolderWidth, 45));
        buttonHolder.add(Box.createHorizontalGlue());
        buttonHolder.add(startButton);
        buttonHolder.add(Box.createHorizontalGlue());
        card.add(buttonHolder);

        // Add card directly to background without scroll pane
        // Use BorderLayout center to allow the card to fill available space
        background.add(card, BorderLayout.CENTER);

        setContentPane(background);
        
        // Pack to fit content naturally, then adjust if needed
        pack();
        
        // Ensure minimum size and maximum size constraints
        Dimension packedSize = getSize();
        int finalWidth = Math.max(dialogWidth, packedSize.width);
        int finalHeight = Math.max(packedSize.height, Math.min((int)(screenSize.height * 0.90), 750));
        setSize(finalWidth, finalHeight);
        
        // Manually center on screen
        int x = (screenSize.width - finalWidth) / 2;  // Centered horizontally
        int y = (screenSize.height - finalHeight) / 3; // Positioned higher (1/3 from top instead of center)
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
        if (card == null || background == null) {
            return;
        }
        
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        
        if (currentWidth <= 0 || currentHeight <= 0) {
            return;
        }
        
        // Calculate new card width based on current dialog size (accounting for padding)
        int bgPadding = 20; // Match the padding used in constructor
        int newCardWidth = currentWidth - (bgPadding * 2) - 10; // Account for padding and margin
        
        // Update card maximum width - this allows BoxLayout to respect the width constraint
        card.setMaximumSize(new Dimension(newCardWidth, Integer.MAX_VALUE));
        card.setPreferredSize(new Dimension(newCardWidth, card.getPreferredSize().height));
        
        // Update text field sizes
        if (player1TextField != null) {
            int fieldWidth = newCardWidth - 50; // Account for card padding
            Dimension fieldSize = new Dimension(fieldWidth, 35);
            player1TextField.setMaximumSize(fieldSize);
            player1TextField.setPreferredSize(fieldSize);
        }
        if (player2TextField != null) {
            int fieldWidth = newCardWidth - 50; // Account for card padding
            Dimension fieldSize = new Dimension(fieldWidth, 35);
            player2TextField.setMaximumSize(fieldSize);
            player2TextField.setPreferredSize(fieldSize);
        }
        
        // Update game mode row
        if (twoPlayersOption != null && vsAIOption != null) {
            JPanel gameModeRow = (JPanel) twoPlayersOption.getParent();
            if (gameModeRow != null) {
                int gameModeRowWidth = newCardWidth - 50;
                int gameModeRowHeight = 80; // Match the increased height
                gameModeRow.setMaximumSize(new Dimension(gameModeRowWidth, gameModeRowHeight));
                gameModeRow.setPreferredSize(new Dimension(gameModeRowWidth, gameModeRowHeight));
            }
        }
        
        // Update difficulty row - ensure it fits
        // GridLayout will automatically divide the width equally among 3 cards
        if (difficultyRow != null) {
            int diffRowWidth = newCardWidth - 50; // Account for card padding
            difficultyRow.setMaximumSize(new Dimension(diffRowWidth, 100));
            difficultyRow.setPreferredSize(new Dimension(diffRowWidth, 100));
        }
        
        // Update info wrapper
        if (infoWrapper != null) {
            int infoWidth = newCardWidth - 50; // Account for card padding
            infoWrapper.setMaximumSize(new Dimension(infoWidth, 50));
            infoWrapper.setPreferredSize(new Dimension(infoWidth, 50));
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
            buttonHolder.setMaximumSize(new Dimension(buttonHolderWidth, 45));
            buttonHolder.setPreferredSize(new Dimension(buttonHolderWidth, 45));
        }
        
        // Force layout update - start from the dialog and work down
        SwingUtilities.invokeLater(() -> {
            if (card != null) {
                card.revalidate();
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
        Dimension fieldSize = new Dimension(width, 35);
        field.setMaximumSize(fieldSize);
        field.setPreferredSize(fieldSize);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(202, 210, 255), 2, true),
            new EmptyBorder(7, 10, 7, 10)
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
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(116, 107, 150));
        label.setBorder(new EmptyBorder(6, 0, 3, 0));
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
            setBorder(new EmptyBorder(18, 18, 18, 18));
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
            setBorder(new EmptyBorder(8, 8, 8, 8));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            // Calculate size dynamically to fit 3 cards with gaps
            // Will be set by parent container, but set a reasonable default
            Dimension boxSize = new Dimension(140, 100);
            setPreferredSize(boxSize);
            setMinimumSize(new Dimension(100, 85));
            // Don't set maximum size - let GridLayout control the width
            setAlignmentY(Component.TOP_ALIGNMENT);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
            titleLabel.setForeground(new Color(78, 66, 120));

            JLabel gridLabel = new JLabel(grid);
            gridLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            gridLabel.setForeground(new Color(116, 107, 150));
            gridLabel.setBorder(new EmptyBorder(2, 0, 6, 0));

            JPanel heartsPanel = createHeartsPanel();
            heartsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            add(titleLabel);
            add(gridLabel);
            add(heartsPanel);

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        private JPanel createHeartsPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
            panel.setOpaque(false);
            for (int i = 0; i < totalHearts; i++) {
                JLabel heart = new JLabel("\u2665");
                heart.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Fix the visual height of the mode cards so they don't change
            // size when toggling between "Two Players" and "With AI"
            int fixedHeight = 80; // Match gameModeRow height to prevent cropping
            Dimension fixedSize = new Dimension(0, fixedHeight);
            setPreferredSize(fixedSize);
            setMinimumSize(fixedSize);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, fixedHeight));
            
            // Icon label
            JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22)); // Slightly smaller icon
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Title label
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
            titleLabel.setForeground(new Color(78, 66, 120));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(new EmptyBorder(3, 0, 2, 0)); // Reduced spacing
            
            // Subtitle label - use HTML for word wrapping
            JLabel subtitleLabel = new JLabel("<html><center>" + subtitle + "</center></html>");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Slightly smaller font
            subtitleLabel.setForeground(new Color(116, 107, 150));
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitleLabel.setBorder(new EmptyBorder(0, 0, 0, 0)); // No extra border
            
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
