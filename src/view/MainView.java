package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class MainView extends JFrame {

    private final JButton startGameButton;
    private final JButton historyButton;
    private final JButton questionManagerButton;
    private final JButton exitButton;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel menuPanel;
    private final QuestionManagerPanel questionManagerPanel;
    private final HistoryPanel historyPanel;
    private final GamePanel gamePanel;
    private JPanel headerPanel;
    private JPanel menuGrid;
    private JLabel iconLabel;
    private JLabel titleLabel;
    private GradientPanel gradientRoot;
    
    // Base dimensions for scaling calculations
    private int baseWindowWidth;
    private int baseWindowHeight;

    public MainView() {
        super("MineSweeper");
        configureLookAndFeel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        // Use percentage-based sizing for better screen compatibility
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowWidth = (int) (screenSize.width * 0.8); // 80% of screen width
        int windowHeight = (int) (screenSize.height * 0.85); // 85% of screen height
        setPreferredSize(new Dimension(windowWidth, windowHeight));

        startGameButton = buildMenuButton(
                "Start Game",
                new Color(91, 161, 255),
                new Color(26, 90, 219)
        );
        historyButton = buildMenuButton(
                "View History",
                new Color(196, 107, 255),
                new Color(124, 59, 203)
        );
        questionManagerButton = buildMenuButton(
                "Question Manager",
                new Color(78, 214, 137),
                new Color(32, 150, 88)
        );
        exitButton = buildMenuButton(
                "Exit",
                new Color(255, 140, 120),
                new Color(204, 55, 55)
        );

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        menuPanel = buildMainMenuPanel();
        questionManagerPanel = new QuestionManagerPanel();
        historyPanel = new HistoryPanel();
        gamePanel = new GamePanel();

        cardPanel.add(menuPanel, "menu");
        cardPanel.add(questionManagerPanel, "question");
        cardPanel.add(historyPanel, "history");
        cardPanel.add(gamePanel, "game");

        setContentPane(cardPanel);
        setTitle("MineSweeper");
        cardLayout.show(cardPanel, "menu");
        pack();
        setLocationRelativeTo(null);
        
        // Store base dimensions for scaling (use preferred size if width/height is 0)
        Dimension size = getSize();
        baseWindowWidth = size.width > 0 ? size.width : (int)(Toolkit.getDefaultToolkit().getScreenSize().width * 0.8);
        baseWindowHeight = size.height > 0 ? size.height : (int)(Toolkit.getDefaultToolkit().getScreenSize().height * 0.85);
        
        // Add resize listener for responsive UI
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateResponsiveLayout();
            }
        });
        
        // Ensure frame is visible and brought to front
        setVisible(true);
        toFront();
        repaint();
        
        // Update base dimensions after showing (in case pack() set different size) and trigger initial layout update
        SwingUtilities.invokeLater(() -> {
            Dimension actualSize = getSize();
            if (actualSize.width > 0 && actualSize.height > 0) {
                baseWindowWidth = actualSize.width;
                baseWindowHeight = actualSize.height;
                updateResponsiveLayout(); // Initial layout update
            }
        });
    }

    private void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                 | InstantiationException
                 | IllegalAccessException
                 | UnsupportedLookAndFeelException ignored) {
        }
    }

    private JPanel buildMainMenuPanel() {
        gradientRoot = new GradientPanel(
                new Color(225, 232, 255),
                new Color(245, 213, 255)
        );
        gradientRoot.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        gradientRoot.setLayout(new BorderLayout());
        headerPanel = buildHeader();
        menuGrid = buildMenuGrid();
        gradientRoot.add(headerPanel, BorderLayout.NORTH);
        gradientRoot.add(menuGrid, BorderLayout.CENTER);
        return gradientRoot;
    }

    private JPanel buildHeader() {
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Top panel with How to Play button on the right
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // How to Play button (small, top right) with visible border
        JButton smallHowToPlayButton = new JButton("How to Play");
        smallHowToPlayButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        smallHowToPlayButton.setForeground(new Color(91, 161, 255));
        smallHowToPlayButton.setBackground(new Color(240, 248, 255)); // Light blue background
        smallHowToPlayButton.setContentAreaFilled(true);
        smallHowToPlayButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(91, 161, 255), 2, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        smallHowToPlayButton.setFocusPainted(false);
        smallHowToPlayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        smallHowToPlayButton.addActionListener(e -> showHowToPlayDialog());
        
        topPanel.add(Box.createHorizontalGlue(), BorderLayout.WEST);
        topPanel.add(smallHowToPlayButton, BorderLayout.EAST);

        iconLabel = new JLabel("\u26CF", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);
        iconLabel.setForeground(new Color(91, 161, 255));

        titleLabel = new JLabel("Minesweeper PRO", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(140, 70, 215));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 46));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        contentPanel.add(topPanel);
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        return contentPanel;
    }

    private JPanel buildMenuGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 18, 18));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 120, 10, 120));
        grid.add(startGameButton);
        grid.add(historyButton);
        grid.add(questionManagerButton);
        grid.add(exitButton);
        return grid;
    }

    private JButton buildMenuButton(String text, Color startColor, Color endColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(28, 12, 28, 12));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        return button;
    }

    public void setButtonListeners(ActionListener startGameListener,
                                   ActionListener historyListener,
                                   ActionListener questionManagerListener,
                                   ActionListener exitListener) {
        startGameButton.addActionListener(startGameListener);
        historyButton.addActionListener(historyListener);
        questionManagerButton.addActionListener(questionManagerListener);
        exitButton.addActionListener(exitListener);
    }
    
    public void showHowToPlayDialog() {
        SwingUtilities.invokeLater(() -> {
            HowToPlayDialog dialog = new HowToPlayDialog(this);
            dialog.setVisible(true);
        });
    }

    public void showPlaceholderScreen(String featureName) {
        JOptionPane.showMessageDialog(
                this,
                featureName + " screen is under construction.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public boolean confirmExit() {
        final boolean[] confirmed = {false};

        JDialog dialog = new JDialog(this, "Exit Confirmation", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(24, 32, 24, 32));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Are you sure you want to quit?");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(40, 40, 40));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("This will close the game application.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 20, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.RIGHT_ALIGNMENT);

        RoundedButton noButton = new RoundedButton(
                "No",
                Color.WHITE,
                new Color(45, 45, 45),
                new Color(210, 210, 210)
        );
        RoundedButton yesButton = new RoundedButton(
                "Yes",
                new Color(15, 15, 20),
                Color.WHITE,
                new Color(15, 15, 20)
        );

        noButton.addActionListener(e -> {
            confirmed[0] = false;
            dialog.dispose();
        });
        yesButton.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });

        buttonRow.add(noButton);
        buttonRow.add(yesButton);

        content.add(title);
        content.add(subtitle);
        content.add(buttonRow);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return confirmed[0];
    }

    public String promptForPinCode() {
        return JOptionPane.showInputDialog(
                this,
                "Enter 4-digit PIN code: \n It's 2580 for testing",
                "Question Manager Access",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    public void showHistoryPanel(Runnable onReturnToMain) {
        historyPanel.setHomeAction(e -> {
            showMainMenu();
            if (onReturnToMain != null) {
                onReturnToMain.run();
            }
        });
        setTitle("History");
        cardLayout.show(cardPanel, "history");
        SwingUtilities.invokeLater(() -> {
            updateResponsiveLayout();
        });
    }

    public void showQuestionManagerPanel(Runnable onReturnToMain) {
        questionManagerPanel.setHomeAction(e -> {
            showMainMenu();
            if (onReturnToMain != null) {
                onReturnToMain.run();
            }
        });
        // Reload questions from CSV file every time the panel is shown
        questionManagerPanel.reloadQuestions();
        setTitle("Question Manager");
        cardLayout.show(cardPanel, "question");
        SwingUtilities.invokeLater(() -> {
            updateResponsiveLayout();
        });
    }

    public void showMainMenu() {
        setTitle("MineSweeper");
        setResizable(true);
        // Reset extended state in case window was maximized
        setExtendedState(JFrame.NORMAL);
        // Use percentage-based sizing for better screen compatibility
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowWidth = (int) (screenSize.width * 0.8); // 80% of screen width
        int windowHeight = (int) (screenSize.height * 0.85); // 85% of screen height
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        pack();
        setLocationRelativeTo(null); // Center the window on screen
        
        // Update base dimensions for scaling
        Dimension size = getSize();
        if (size.width > 0 && size.height > 0) {
            baseWindowWidth = size.width;
            baseWindowHeight = size.height;
        }
        
        cardLayout.show(cardPanel, "menu");
        SwingUtilities.invokeLater(() -> {
            updateResponsiveLayout();
        });
    }
    
    /**
     * Shows the game panel with the specified game controller.
     * 
     * @param onReturnToMain Runnable to execute when returning to main menu
     */
    public void showGamePanel(Runnable onReturnToMain) {
        gamePanel.setHomeAction(e -> {
            showMainMenu();
            if (onReturnToMain != null) {
                onReturnToMain.run();
            }
        });
        setTitle("Game - Minesweeper PRO");
        setResizable(true);
        // Use near-full screen sizing for better visibility, especially in hard mode
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowWidth = (int) (screenSize.width * 0.98); // 98% of screen width (almost full screen)
        int windowHeight = (int) (screenSize.height * 0.95); // 95% of screen height (almost full screen)
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        pack();
        setLocationRelativeTo(null); // Center the window on screen
        // Maximize the window for best experience, especially in hard mode
        SwingUtilities.invokeLater(() -> {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            // Update base dimensions after maximizing
            Dimension size = getSize();
            if (size.width > 0 && size.height > 0) {
                baseWindowWidth = size.width;
                baseWindowHeight = size.height;
                updateResponsiveLayout();
            }
        });
        cardLayout.show(cardPanel, "game");
    }
    
    /**
     * Gets the game panel instance.
     * 
     * @return The GamePanel instance
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }
    
    /**
     * Updates the responsive layout based on current window size.
     */
    private void updateResponsiveLayout() {
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        
        // Skip if window hasn't been sized yet
        if (currentWidth <= 0 || currentHeight <= 0 || baseWindowWidth <= 0 || baseWindowHeight <= 0) {
            return;
        }
        
        // Calculate scaling factor with min/max bounds
        double widthScale = (double) currentWidth / baseWindowWidth;
        double heightScale = (double) currentHeight / baseWindowHeight;
        double scaleFactor = Math.min(widthScale, heightScale);
        scaleFactor = Math.max(0.75, Math.min(1.5, scaleFactor)); // Limit between 0.75x and 1.5x
        
        // Update menu panel if it's visible (check by component visibility or always update since it's safe)
        if (menuPanel != null && menuPanel.isVisible()) {
            updateMenuPanelScaling(scaleFactor);
        }
        
        // Update other panels (they handle their own scaling)
        if (gamePanel != null && gamePanel.isVisible()) {
            gamePanel.updateResponsiveLayout(scaleFactor);
        }
        if (questionManagerPanel != null && questionManagerPanel.isVisible()) {
            questionManagerPanel.updateResponsiveLayout(scaleFactor);
        }
        if (historyPanel != null && historyPanel.isVisible()) {
            historyPanel.updateResponsiveLayout(scaleFactor);
        }
    }
    
    /**
     * Updates menu panel elements with scaling.
     */
    private void updateMenuPanelScaling(double scaleFactor) {
        if (iconLabel != null) {
            int iconSize = (int) (64 * scaleFactor);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, iconSize));
        }
        
        if (titleLabel != null) {
            int titleSize = (int) (46 * scaleFactor);
            titleSize = Math.max(32, Math.min(60, titleSize)); // Clamp between 32-60
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, titleSize));
        }
        
        // Update button fonts
        int buttonFontSize = (int) (20 * scaleFactor);
        buttonFontSize = Math.max(16, Math.min(28, buttonFontSize)); // Clamp between 16-28
        Font buttonFont = new Font("Segoe UI", Font.BOLD, buttonFontSize);
        startGameButton.setFont(buttonFont);
        historyButton.setFont(buttonFont);
        questionManagerButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);
        
        // Update button padding
        int buttonPadding = (int) (28 * scaleFactor);
        buttonPadding = Math.max(20, Math.min(40, buttonPadding)); // Clamp between 20-40
        startGameButton.setBorder(BorderFactory.createEmptyBorder(buttonPadding, 12, buttonPadding, 12));
        historyButton.setBorder(BorderFactory.createEmptyBorder(buttonPadding, 12, buttonPadding, 12));
        questionManagerButton.setBorder(BorderFactory.createEmptyBorder(buttonPadding, 12, buttonPadding, 12));
        exitButton.setBorder(BorderFactory.createEmptyBorder(buttonPadding, 12, buttonPadding, 12));
        
        // Update menu grid padding
        if (menuGrid != null) {
            int gridPadding = (int) (120 * scaleFactor);
            gridPadding = Math.max(60, Math.min(200, gridPadding)); // Clamp between 60-200
            int gridGap = (int) (18 * scaleFactor);
            gridGap = Math.max(12, Math.min(30, gridGap)); // Clamp between 12-30
            menuGrid.setLayout(new GridLayout(2, 2, gridGap, gridGap));
            menuGrid.setBorder(BorderFactory.createEmptyBorder(10, gridPadding, 10, gridPadding));
        }
        
        // Update root padding
        if (gradientRoot != null) {
            int rootPadding = (int) (24 * scaleFactor);
            rootPadding = Math.max(16, Math.min(40, rootPadding)); // Clamp between 16-40
            gradientRoot.setBorder(BorderFactory.createEmptyBorder(rootPadding, rootPadding, rootPadding, rootPadding));
        }
        
        revalidate();
        repaint();
    }

    private static class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;

        private GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedButton extends JButton {
        private final Color fillColor;
        private final Color borderColor;

        private RoundedButton(String text, Color fillColor, Color textColor, Color borderColor) {
            super(text);
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            setForeground(textColor);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

