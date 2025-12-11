package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameSetupPanel extends JPanel {

    private final JTextField player1TextField;
    private final JTextField player2TextField;
    private final JButton backButton;
    private final JButton startGameButton;
    private int selectedDifficulty = 1;

    private final DifficultyCard easyCard;
    private final DifficultyCard mediumCard;
    private final DifficultyCard hardCard;

    public GameSetupPanel() {

        setLayout(new BorderLayout());

        // Background gradient
        GradientPanel bg = new GradientPanel(
                new Color(225, 232, 255),
                new Color(245, 213, 255)
        );
        bg.setLayout(new BorderLayout());
        add(bg, BorderLayout.CENTER);

        // Top back button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        backButton = createBackButton();
        top.add(backButton, BorderLayout.WEST);
        bg.add(top, BorderLayout.NORTH);

        /** MAIN CARD PANEL **/
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(750, 600));
        card.setMaximumSize(new Dimension(750, 600));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(30),
                BorderFactory.createEmptyBorder(35, 45, 35, 45)
        ));

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new GridBagLayout());
        wrapper.add(card);
        bg.add(wrapper, BorderLayout.CENTER);

        // Title
        JLabel icon = new JLabel("🎮");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel title = new JLabel("Setup Game");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(80, 80, 80));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.add(icon);
        titlePanel.add(title);

        card.add(titlePanel);
        card.add(Box.createVerticalStrut(20));

        /** PLAYER 1 **/
        card.add(label("Player 1 Name"));
        player1TextField = textField(new Color(91, 161, 255));
        card.add(player1TextField);
        card.add(Box.createVerticalStrut(20));

        /** PLAYER 2 **/
        card.add(label("Player 2 Name"));
        player2TextField = textField(new Color(196, 107, 255));
        card.add(player2TextField);
        card.add(Box.createVerticalStrut(25));

        /** DIFFICULTY LABEL **/
        JLabel diffLabel = new JLabel("Difficulty Level");
        diffLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        diffLabel.setForeground(new Color(78, 214, 137));
        diffLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(diffLabel);
        card.add(Box.createVerticalStrut(15));

        /** DIFFICULTY CARDS **/
        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        diffPanel.setOpaque(false);

        easyCard = new DifficultyCard("Easy", "9×9", 10, new Color(78, 214, 137));
        mediumCard = new DifficultyCard("Medium", "13×13", 8, new Color(255, 165, 0));
        hardCard = new DifficultyCard("Hard", "16×16", 6, new Color(255, 69, 58));

        easyCard.setActive(true);

        diffPanel.add(easyCard);
        diffPanel.add(mediumCard);
        diffPanel.add(hardCard);
        card.add(diffPanel);

        // Card Click Handlers
        easyCard.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { select(1); } });
        mediumCard.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { select(2); } });
        hardCard.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { select(3); } });

        /** HEART INFO **/
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        JLabel infoIcon = new JLabel("ℹ");
        infoIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoIcon.setForeground(new Color(91, 161, 255));

        JLabel info = new JLabel("Both players will share 10 hearts total");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setForeground(new Color(110, 110, 110));

        infoPanel.add(infoIcon);
        infoPanel.add(info);
        card.add(Box.createVerticalStrut(15));
        card.add(infoPanel);

        card.add(Box.createVerticalStrut(30));

        /** START GAME BUTTON **/
        startGameButton = gradientButton("Start Game");
        card.add(startGameButton);
    }

    private JButton createBackButton() {
        JButton b = new JButton("← Back to Menu");
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        b.setForeground(new Color(70, 70, 70));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        l.setForeground(new Color(100, 100, 100));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField textField(Color borderColor) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    private JButton gradientButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0,
                        new Color(78, 214, 137),
                        getWidth(), 0,
                        new Color(91, 161, 255)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private void select(int d) {
        selectedDifficulty = d;
        easyCard.setActive(d == 1);
        mediumCard.setActive(d == 2);
        hardCard.setActive(d == 3);
    }
    
    // Public methods for controller access
    public String getPlayer1Name() {
        return player1TextField.getText().trim();
    }
    
    public String getPlayer2Name() {
        return player2TextField.getText().trim();
    }
    
    public int getDifficulty() {
        return selectedDifficulty;
    }
    
    public void setBackAction(ActionListener actionListener) {
        for (ActionListener listener : backButton.getActionListeners()) {
            backButton.removeActionListener(listener);
        }
        if (actionListener != null) {
            backButton.addActionListener(actionListener);
        }
    }
    
    public void setStartGameAction(ActionListener actionListener) {
        for (ActionListener listener : startGameButton.getActionListeners()) {
            startGameButton.removeActionListener(listener);
        }
        if (actionListener != null) {
            startGameButton.addActionListener(e -> {
                if (validateInput()) {
                    actionListener.actionPerformed(e);
                }
            });
        }
    }
    
    private boolean validateInput() {
        String player1Name = player1TextField.getText().trim();
        String player2Name = player2TextField.getText().trim();
        
        if (player1Name.isEmpty()) {
            ErrorDialog.showErrorDialog(this, "Please enter Player 1 name.");
            player1TextField.requestFocus();
            return false;
        }
        if (player2Name.isEmpty()) {
            ErrorDialog.showErrorDialog(this, "Please enter Player 2 name.");
            player2TextField.requestFocus();
            return false;
        }
        if (player1Name.equalsIgnoreCase(player2Name)) {
            ErrorDialog.showErrorDialog(this, "Player 1 and Player 2 must have different names.");
            player2TextField.requestFocus();
            return false;
        }
        return true;
    }

    /** Difficulty Card Component **/
    private static class DifficultyCard extends JPanel {

        private boolean active = false;
        private final Color activeColor;

        DifficultyCard(String title, String size, int hearts, Color activeColor) {
            this.activeColor = activeColor;

            setPreferredSize(new Dimension(160, 135));
            setBackground(new Color(250, 250, 250));
            setBorder(new RoundedBorder(15));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JLabel name = new JLabel(title);
            name.setFont(new Font("Segoe UI", Font.BOLD, 16));
            name.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sizeLabel = new JLabel(size);
            sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            sizeLabel.setForeground(new Color(120, 120, 120));
            sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel hp = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
            hp.setOpaque(false);
            for (int i = 0; i < hearts; i++) {
                JLabel h = new JLabel("❤");
                h.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                h.setForeground(Color.RED);
                hp.add(h);
            }

            add(Box.createVerticalStrut(10));
            add(name);
            add(sizeLabel);
            add(Box.createVerticalStrut(10));
            add(hp);
        }

        public void setActive(boolean active) {
            this.active = active;
            if (active) {
                setBorder(new LineBorderRounded(activeColor, 3, 15));
            } else {
                setBorder(new RoundedBorder(15));
            }
            repaint();
        }
    }

    /** Simple rounded border **/
    private static class RoundedBorder extends LineBorderRounded {
        RoundedBorder(int radius) { super(new Color(230, 230, 230), 1, radius); }
    }

    private static class LineBorderRounded extends javax.swing.border.LineBorder {
        private final int radius;

        public LineBorderRounded(Color color, int thickness, int radius) {
            super(color, thickness, true);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, radius, radius);
        }
    }

    /** Gradient Background Panel **/
    private static class GradientPanel extends JPanel {
        private final Color start, end;

        GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, start, 0, getHeight(), end));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
