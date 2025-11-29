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
import java.awt.event.ActionListener;
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

    public MainView() {
        super("MineSweeper");
        configureLookAndFeel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setPreferredSize(new Dimension(900, 520));

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
        // Ensure frame is visible and brought to front
        setVisible(true);
        toFront();
        repaint();
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
        GradientPanel root = new GradientPanel(
                new Color(225, 232, 255),
                new Color(245, 213, 255)
        );
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        root.setLayout(new BorderLayout());
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildMenuGrid(), BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel("\u26CF", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);
        iconLabel.setForeground(new Color(91, 161, 255));

        JLabel title = new JLabel("Minesweeper PRO", SwingConstants.CENTER);
        title.setForeground(new Color(140, 70, 215));
        title.setFont(new Font("Segoe UI", Font.BOLD, 46));
        title.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(20));
        return headerPanel;
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
    }

    public void showQuestionManagerPanel(Runnable onReturnToMain) {
        questionManagerPanel.setHomeAction(e -> {
            showMainMenu();
            if (onReturnToMain != null) {
                onReturnToMain.run();
            }
        });
        setTitle("Question Manager");
        cardLayout.show(cardPanel, "question");
    }

    public void showMainMenu() {
        setTitle("MineSweeper");
        setResizable(false);
        setPreferredSize(new Dimension(900, 520));
        pack();
        cardLayout.show(cardPanel, "menu");
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
        setPreferredSize(new Dimension(1400, 800));
        pack();
        setLocationRelativeTo(null); // Center the window on screen
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

