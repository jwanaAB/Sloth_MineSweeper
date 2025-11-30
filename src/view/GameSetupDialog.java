package view;

import java.awt.*;
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
    private DifficultyOption[] difficultyOptions;
    private JLabel infoText = new JLabel("Both players will share 10 hearts total");


    public GameSetupDialog(JFrame parent) {
        super(parent, "Game Setup", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);

        // Get screen dimensions for percentage-based sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogWidth = (int) (screenSize.width * 0.5); // 50% of screen width
        int dialogHeight = (int) (screenSize.height * 0.85); // 85% of screen height
        setPreferredSize(new Dimension(dialogWidth, dialogHeight));
        setSize(dialogWidth, dialogHeight);

        GradientPanel background = new GradientPanel();
        background.setBorder(new EmptyBorder(30, 30, 30, 30));
        background.setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(32);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Use percentage-based width instead of fixed
        int cardWidth = (int) (dialogWidth * 0.85); // 85% of dialog width
        card.setMaximumSize(new Dimension(cardWidth, Integer.MAX_VALUE));

        JLabel backLabel = new JLabel("\u2190 Back to Menu");
        backLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        backLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backLabel.setForeground(new Color(110, 110, 150));
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setBorder(new EmptyBorder(0, 0, 12, 0));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                confirmed = false;
                dispose();
            }
        });

        JLabel title = new JLabel("Setup Game");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        title.setForeground(new Color(76, 63, 125));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Create text fields with percentage-based sizing
        int fieldWidth = (int) (cardWidth * 0.9); // 90% of card width
        player1TextField = createStyledTextField("Player 1 name", fieldWidth);
        player2TextField = createStyledTextField("Player 2 name", fieldWidth);

        JLabel player1Label = createSectionLabel("Player 1 Name");
        JLabel player2Label = createSectionLabel("Player 2 Name");
        JLabel difficultyLabel = createSectionLabel("Difficulty Level");

        JPanel difficultyRow = new JPanel(new GridLayout(1, 3, 16, 0));
        difficultyRow.setOpaque(false);
        difficultyRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        int diffRowWidth = (int) (cardWidth * 0.9);
        difficultyRow.setMaximumSize(new Dimension(diffRowWidth, 150));

        difficultyOptions = new DifficultyOption[]{
            new DifficultyOption("Easy", "9 × 9", 10, 10, 1, new Color(97, 207, 145)),
            new DifficultyOption("Medium", "13 × 13", 8, 10, 2, new Color(253, 176, 95)),
            new DifficultyOption("Hard", "16 × 16", 6, 10, 3, new Color(255, 105, 120))
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

        JPanel infoWrapper = new JPanel();
        infoWrapper.setLayout(new BoxLayout(infoWrapper, BoxLayout.X_AXIS));
        infoWrapper.setOpaque(true);
        infoWrapper.setBackground(new Color(248, 251, 255));
        infoWrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(155, 190, 255), 2, true),
            new EmptyBorder(12, 18, 12, 18)
        ));
        infoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        int infoWidth = (int) (cardWidth * 0.85);
        infoWrapper.setMaximumSize(new Dimension(infoWidth, 70));

        JLabel infoIcon = new JLabel("i", SwingConstants.CENTER);
        infoIcon.setPreferredSize(new Dimension(26, 26));
        infoIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoIcon.setOpaque(true);
        infoIcon.setBackground(new Color(35, 123, 255));
        infoIcon.setForeground(Color.WHITE);
        infoIcon.setBorder(new LineBorder(new Color(35, 123, 255), 1, true));

        infoText = new JLabel("Both players will share 10 hearts total");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoText.setForeground(new Color(35, 89, 160));
        infoText.setBorder(new EmptyBorder(0, 12, 0, 0));

        infoWrapper.add(infoIcon);
        infoWrapper.add(infoText);

        startButton = new GradientButton("Start Game");
        startButton.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(200, 48));
        startButton.setBorder(new EmptyBorder(14, 16, 14, 16));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        startButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        player1TextField.addActionListener(e -> player2TextField.requestFocus());
        player2TextField.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        card.add(backLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(player1Label);
        card.add(player1TextField);
        card.add(Box.createVerticalStrut(12));
        card.add(player2Label);
        card.add(player2TextField);
        card.add(Box.createVerticalStrut(16));
        card.add(difficultyLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(difficultyRow);
        card.add(Box.createVerticalStrut(20));
        card.add(infoWrapper);
        card.add(Box.createVerticalStrut(28));

        JPanel buttonHolder = new JPanel();
        buttonHolder.setOpaque(false);
        buttonHolder.setLayout(new BoxLayout(buttonHolder, BoxLayout.X_AXIS));
        int buttonHolderWidth = (int) (cardWidth * 0.75);
        buttonHolder.setMaximumSize(new Dimension(buttonHolderWidth, 60));
        buttonHolder.add(Box.createHorizontalGlue());
        buttonHolder.add(startButton);
        buttonHolder.add(Box.createHorizontalGlue());
        card.add(buttonHolder);

        // Wrap card in scroll pane for scrollability
        // Set preferred size on card to enable scrolling
        card.setPreferredSize(new Dimension(cardWidth, Integer.MAX_VALUE));
        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Set scroll pane to fill available space
        scrollPane.setPreferredSize(new Dimension(cardWidth, dialogHeight - 60));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        background.add(scrollPane, gbc);

        setContentPane(background);
        // Don't use pack() as it overrides our size - use setSize instead
        // setSize was already called above, just center it now
        // Manually center on screen
        int x = (screenSize.width - dialogWidth) / 2;
        int y = (screenSize.height - dialogHeight) / 2;
        setLocation(x, y);
    }

    private JTextField createStyledTextField(String placeholder, int width) {
        JTextField field = new JTextField();
        Dimension fieldSize = new Dimension(width, 44);
        field.setMaximumSize(fieldSize);
        field.setPreferredSize(fieldSize);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(202, 210, 255), 2, true),
            new EmptyBorder(10, 14, 10, 14)
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
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(116, 107, 150));
        label.setBorder(new EmptyBorder(12, 0, 6, 0));
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
        infoText.setText("Both players will share " + hearts + " hearts total");
    }

    private boolean validateInput() {
        String player1Name = player1TextField.getText().trim();
        String player2Name = player2TextField.getText().trim();

        if (player1Name.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter Player 1 name.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );
            player1TextField.requestFocus();
            return false;
        }
        if (player1Name.length() > 20) {
            JOptionPane.showMessageDialog(
                this,
                "Player 1 name must be 20 characters or less.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );
            player1TextField.requestFocus();
            return false;
        }

        if (player2Name.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter Player 2 name.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );
            player2TextField.requestFocus();
            return false;
        }
        if (player2Name.length() > 20) {
            JOptionPane.showMessageDialog(
                this,
                "Player 2 name must be 20 characters or less.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );
            player2TextField.requestFocus();
            return false;
        }

        if (player1Name.equalsIgnoreCase(player2Name)) {
            JOptionPane.showMessageDialog(
                this,
                "Player 1 and Player 2 must have different names.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );
            player2TextField.requestFocus();
            return false;
        }

        return true;
    }

    public String getPlayer1Name() {
        return player1TextField.getText().trim();
    }

    public String getPlayer2Name() {
        return player2TextField.getText().trim();
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
            setBorder(new EmptyBorder(18, 20, 18, 20));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            Dimension boxSize = new Dimension(170, 150);
            setPreferredSize(boxSize);
            setMinimumSize(boxSize);
            setMaximumSize(boxSize);
            setAlignmentY(Component.TOP_ALIGNMENT);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            titleLabel.setForeground(new Color(78, 66, 120));

            JLabel gridLabel = new JLabel(grid);
            gridLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            gridLabel.setForeground(new Color(116, 107, 150));
            gridLabel.setBorder(new EmptyBorder(4, 0, 12, 0));

            JPanel heartsPanel = createHeartsPanel();
            heartsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            add(titleLabel);
            add(gridLabel);
            add(heartsPanel);

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        private JPanel createHeartsPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            panel.setOpaque(false);
            for (int i = 0; i < totalHearts; i++) {
                JLabel heart = new JLabel("\u2665");
                heart.setFont(new Font("Segoe UI", Font.PLAIN, 16));
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
