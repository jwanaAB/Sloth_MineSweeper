package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dialog showing the "How to Play" guide for Minesweeper Pro.
 * Scrollable panel displayed in the center of the screen.
 * Clicking outside the dialog closes it.
 */
public class HowToPlayDialog extends JDialog {
    
    public HowToPlayDialog(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Create a semi-transparent overlay panel that covers the whole dialog
        JPanel overlayPanel = new JPanel(new GridBagLayout());
        overlayPanel.setOpaque(true);
        overlayPanel.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent dark background
        
        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(24, 32, 24, 32));
        
        // Title panel with icon and close button
        JPanel titlePanel = createTitlePanel();
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Create guide sections
        contentPanel.add(createSection(
            "Objective",
            "🎯",
            new Color(173, 216, 230), // Light blue
            "<html>Work together with your partner to reveal all safe cells without hitting mines.<br>" +
            "Answer trivia questions to earn bonus points and lives!</html>"
        ));
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        contentPanel.add(createSection(
            "Controls",
            "🎮",
            new Color(144, 238, 144), // Light green
            "<html><b>Left Click:</b> Reveal a cell<br>" +
            "<b>Right Click:</b> Cycle through flag ► — question mark ? — hidden<br>" +
            "<b>Numbers:</b> Show how many mines are adjacent to that cell</html>"
        ));
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        contentPanel.add(createSection(
            "Shared Hearts (Lives)",
            "❤️",
            new Color(255, 182, 193), // Light pink
            "<html>Both players share a pool of hearts. Hitting a mine or answering a question incorrectly<br>" +
            "may cost hearts. The game ends when all hearts are lost.</html>"
        ));
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        contentPanel.add(createSection(
            "Question Cells",
            "❓",
            new Color(255, 255, 153), // Light yellow
            "<html>Clicking on a question cell (after revealing it) triggers a trivia challenge.<br>" +
            "Questions have an activation cost (5/8/12 points by difficulty).<br>" +
            "Correct answers earn points and may grant lives based on question type and difficulty.<br>" +
            "Wrong answers may cost points and lives depending on the question type.</html>"
        ));
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        contentPanel.add(createSection(
            "Flags & Marks",
            "🚩",
            new Color(221, 160, 221), // Light purple
            "<html>Use flags to mark suspected mines. Use question marks to indicate uncertainty.<br>" +
            "These are just visual aids and don't affect gameplay directly.</html>"
        ));
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        contentPanel.add(createSection(
            "Combined Score",
            "🏆",
            new Color(173, 216, 230), // Light blue
            "<html>Both players share a combined score. Work together to maximize points by answering<br>" +
            "questions correctly and revealing cells strategically!</html>"
        ));
        
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Close button at the bottom
        JButton closeButton = createCloseButton();
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        contentPanel.add(buttonPanel);
        
        // Wrap content in a panel for proper scrolling
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        // Add content panel to center to allow full width usage
        wrapperPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(true);
        scrollPane.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Set size to fit inside main menu (80% of main menu width, 85% of main menu height)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int mainMenuWidth = (int) (screenSize.width * 0.8); // Main menu is 80% of screen
        int mainMenuHeight = (int) (screenSize.height * 0.85); // Main menu is 85% of screen
        int dialogWidth = (int) (mainMenuWidth * 0.75); // 75% of main menu width
        int dialogHeight = (int) (mainMenuHeight * 0.85); // 85% of main menu height
        scrollPane.setPreferredSize(new Dimension(dialogWidth, dialogHeight));
        scrollPane.setMaximumSize(new Dimension(dialogWidth, dialogHeight));
        
        // Center the scroll pane in the overlay using GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        overlayPanel.add(scrollPane, gbc);
        
        // Add click listener to overlay to close dialog when clicking outside
        overlayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == overlayPanel) {
                    dispose();
                }
            }
        });
        
        // Prevent clicks on scroll pane from closing dialog
        scrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
        
        setContentPane(overlayPanel);
        
        // Set dialog size to cover parent window or screen
        if (parent != null && parent.isVisible()) {
            Rectangle parentBounds = parent.getBounds();
            setBounds(parentBounds);
        } else {
            setBounds(0, 0, screenSize.width, screenSize.height);
        }
        
        // Also close on ESC key
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Pack and center on screen
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
        toFront();
        requestFocus();
        repaint();
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Title with house icon
        JLabel titleLabel = new JLabel("How to Play Minesweeper Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(40, 40, 40));
        
        // House icon (using emoji)
        JLabel iconLabel = new JLabel("🏠");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        JPanel titleWithIcon = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleWithIcon.setOpaque(false);
        titleWithIcon.add(iconLabel);
        titleWithIcon.add(titleLabel);
        
        // Close button - make it more visible and ensure it works on first click
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        closeButton.setForeground(new Color(80, 80, 80));
        closeButton.setBackground(new Color(245, 245, 245));
        closeButton.setContentAreaFilled(true);
        closeButton.setOpaque(true);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(new Color(220, 220, 220));
                closeButton.setForeground(new Color(40, 40, 40));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(new Color(245, 245, 245));
                closeButton.setForeground(new Color(80, 80, 80));
            }
        });
        // Use actionPerformed directly without lambda to ensure it works on first click
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        
        panel.add(titleWithIcon, BorderLayout.WEST);
        panel.add(closeButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSection(String title, String icon, Color bgColor, String text) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 100), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        
        // Icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(40, 40, 40));
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);
        
        // Text content with proper wrapping - use full card width
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLabel.setForeground(new Color(60, 60, 60));
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setHorizontalAlignment(SwingConstants.LEFT);
        // Remove width constraint to use full card width, but set alignment
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Allow text to wrap naturally within the card width
        textLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        section.add(headerPanel);
        section.add(Box.createVerticalStrut(8));
        section.add(textLabel);
        
        return section;
    }
    
    private JButton createCloseButton() {
        JButton button = new JButton("Close") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(91, 161, 255), getWidth(), getHeight(), new Color(140, 70, 215)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> dispose());
        
        return button;
    }
}
