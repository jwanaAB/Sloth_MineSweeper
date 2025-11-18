package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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

        cardPanel.add(menuPanel, "menu");
        cardPanel.add(questionManagerPanel, "question");
        cardPanel.add(historyPanel, "history");

        setContentPane(cardPanel);
        setTitle("MineSweeper");
        cardLayout.show(cardPanel, "menu");
        pack();
        setLocationRelativeTo(null);
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
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }

    public String promptForPinCode() {
        return JOptionPane.showInputDialog(
                this,
                "Enter 4-digit PIN code:",
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
        cardLayout.show(cardPanel, "menu");
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
}

