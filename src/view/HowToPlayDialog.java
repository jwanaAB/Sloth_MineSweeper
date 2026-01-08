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
        
        // Create guide sections - Objective card with special styling
        contentPanel.add(createObjectiveCard());
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        // Controls card with special styling
        contentPanel.add(createControlsCard());
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        // Shared Hearts card with special styling
        contentPanel.add(createSharedHeartsCard());
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        // Question Cells card with colored background
        contentPanel.add(createQuestionCellsCard());
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        // Flags & Marks card with colored background
        contentPanel.add(createFlagsMarksCard());
        
        contentPanel.add(Box.createVerticalStrut(12));
        
        // Combined Score card with colored background
        contentPanel.add(createCombinedScoreCard());
        
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
                // Get the component that was actually clicked
                Component clickedComponent = SwingUtilities.getDeepestComponentAt(
                    overlayPanel, e.getX(), e.getY());
                
                // Don't close if clicking on a button or inside the scroll pane
                if (clickedComponent != null) {
                    // Check if clicked component is a button or inside scroll pane
                    if (clickedComponent instanceof JButton) {
                        return; // Let button handle the click
                    }
                    
                    // Check if clicked component is inside the scroll pane
                    Component parent = clickedComponent.getParent();
                    while (parent != null && parent != overlayPanel) {
                        if (parent == scrollPane) {
                            return; // Click was inside scroll pane, don't close
                        }
                        parent = parent.getParent();
                    }
                }
                
                // Only close if clicking directly on the overlay (outside scroll pane)
                if (e.getComponent() == overlayPanel) {
                    dispose();
                }
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
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Stop event propagation to prevent overlay listener from firing
                e.consume();
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
    
    private JPanel createObjectiveCard() {
        // Light blue-gray background matching the last 3 cards design
        Color cardBg = new Color(235, 240, 245); // Light blue-gray
        Color borderColor = new Color(200, 210, 220); // Slightly darker blue-gray for border
        Color textColor = new Color(50, 50, 50); // Dark grey text
        
        // Create card panel with colored background, rounded corners and border
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card background
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Header row: icon + title (icon directly aligned, no container)
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Icon label (directly, no container)
        JLabel iconLabel = new JLabel("🎯");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        
        // Title label
        JLabel titleLabel = new JLabel("Objective");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        
        headerRow.add(iconLabel);
        headerRow.add(titleLabel);
        
        // Body text
        JLabel textLabel = new JLabel("<html><div style='line-height: 1.6;'>" +
            "Work together with your partner to reveal all safe cells without hitting mines. " +
            "Answer trivia questions to earn bonus points and lives!</div></html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(textColor);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textLabel.setBorder(new EmptyBorder(12, 0, 0, 0));
        textLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        contentPanel.add(headerRow);
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(textLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createControlsCard() {
        // Light green background matching the last 3 cards design
        Color cardBg = new Color(220, 255, 220); // Light pastel green
        Color borderColor = new Color(180, 220, 180); // Slightly darker green for border
        Color textColor = new Color(50, 50, 50); // Dark grey text
        Color keywordColor = new Color(34, 139, 34); // Dark green for keywords
        
        // Create card panel with colored background, rounded corners and border
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card background
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Header row: icon + title (icon directly aligned, no container)
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Icon label (directly, no container)
        JLabel iconLabel = new JLabel("🎮");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        
        // Title label
        JLabel titleLabel = new JLabel("Controls");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        
        headerRow.add(iconLabel);
        headerRow.add(titleLabel);
        
        // Body text with bold green keywords
        String content = "<html><div style='line-height: 1.6;'>" +
            "<b style='color: rgb(" + keywordColor.getRed() + ", " + keywordColor.getGreen() + ", " + keywordColor.getBlue() + ");'>Left Click:</b> Reveal a cell<br>" +
            "<b style='color: rgb(" + keywordColor.getRed() + ", " + keywordColor.getGreen() + ", " + keywordColor.getBlue() + ");'>Right Click:</b> For A flag Mark, Or turn flag mode on and start marking cells<br>" +
            "<b style='color: rgb(" + keywordColor.getRed() + ", " + keywordColor.getGreen() + ", " + keywordColor.getBlue() + ");'>Numbers:</b> Show how many mines are adjacent to that cell</div></html>";
        
        JLabel textLabel = new JLabel(content);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(textColor);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textLabel.setBorder(new EmptyBorder(12, 0, 0, 0));
        textLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        contentPanel.add(headerRow);
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(textLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createSharedHeartsCard() {
        // Light pink background matching the last 3 cards design
        Color cardBg = new Color(255, 230, 235); // Light pastel pink
        Color borderColor = new Color(220, 180, 190); // Slightly darker pink for border
        Color textColor = new Color(50, 50, 50); // Dark grey text
        
        // Create card panel with colored background, rounded corners and border
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card background
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Header row: icon + title (icon directly aligned, no container)
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Icon label (directly, no container)
        JLabel iconLabel = new JLabel("❤️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        
        // Title label
        JLabel titleLabel = new JLabel("Shared Hearts (Lives)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        
        headerRow.add(iconLabel);
        headerRow.add(titleLabel);
        
        // Body text
        JLabel textLabel = new JLabel("<html><div style='line-height: 1.6;'>" +
            "Both players share a pool of hearts. Hitting a mine or answering a question incorrectly " +
            "may cost hearts. The game ends when all hearts are lost.</div></html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(textColor);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textLabel.setBorder(new EmptyBorder(12, 0, 0, 0));
        textLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        contentPanel.add(headerRow);
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(textLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createQuestionCellsCard() {
        // Light yellow/cream background matching the image
        Color cardBg = new Color(255, 255, 240); // Light yellow/cream
        Color borderColor = new Color(255, 220, 100); // Slightly darker yellow for border
        Color textColor = new Color(50, 50, 50); // Dark grey text
        
        // Create card panel with colored background, rounded corners and border
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card background
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Header row: icon + title (icon directly aligned, no container)
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Icon label (directly, no container)
        JLabel iconLabel = new JLabel("❓");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        
        // Title label
        JLabel titleLabel = new JLabel("Question Cells");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        
        headerRow.add(iconLabel);
        headerRow.add(titleLabel);
        
        // Body text
        JLabel textLabel = new JLabel("<html><div style='line-height: 1.6;'>" +
            "Clicking on a question cell (after revealing it) triggers a trivia challenge.<br>" +
            "Questions have an activation cost (5/8/12 points by difficulty).<br>" +
            "Correct answers earn points and may grant lives based on question type and difficulty.<br>" +
            "Wrong answers may cost points and lives depending on the question type.</div></html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(textColor);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textLabel.setBorder(new EmptyBorder(12, 0, 0, 0));
        textLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        contentPanel.add(headerRow);
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(textLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createFlagsMarksCard() {
        // Light purple/lavender background matching the image
        Color cardBg = new Color(245, 230, 245); // Light purple/lavender
        Color borderColor = new Color(200, 150, 200); // Slightly darker purple for border
        Color textColor = new Color(50, 50, 50); // Dark grey text
        
        // Create card panel with colored background, rounded corners and border
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card background
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Header row: icon + title (icon directly aligned, no container)
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Icon label (directly, no container)
        JLabel iconLabel = new JLabel("🚩");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        
        // Title label
        JLabel titleLabel = new JLabel("Flags & Marks");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        
        headerRow.add(iconLabel);
        headerRow.add(titleLabel);
        
        // Body text
        JLabel textLabel = new JLabel("<html><div style='line-height: 1.6;'>" +
            "Use flags to mark suspected mines.<br>" +
            "These are just visual aids and don't affect gameplay directly.</div></html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(textColor);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textLabel.setBorder(new EmptyBorder(12, 0, 0, 0));
        textLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        contentPanel.add(headerRow);
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(textLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createCombinedScoreCard() {
        // Light blue background matching the image
        Color cardBg = new Color(230, 240, 255); // Light blue
        Color borderColor = new Color(150, 180, 220); // Slightly darker blue for border
        Color textColor = new Color(50, 50, 50); // Dark grey text
        
        // Create card panel with colored background, rounded corners and border
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card background
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Header row: icon + title (icon directly aligned, no container)
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Icon label (directly, no container)
        JLabel iconLabel = new JLabel("🏆");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        
        // Title label
        JLabel titleLabel = new JLabel("Combined Score");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        
        headerRow.add(iconLabel);
        headerRow.add(titleLabel);
        
        // Body text
        JLabel textLabel = new JLabel("<html><div style='line-height: 1.6;'>" +
            "Both players share a combined score. Work together to maximize points by answering " +
            "questions correctly and revealing cells strategically!</div></html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(textColor);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textLabel.setBorder(new EmptyBorder(12, 0, 0, 0));
        textLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        contentPanel.add(headerRow);
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(textLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
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
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Stop event propagation to prevent overlay listener from firing
                e.consume();
            }
        });
        button.addActionListener(e -> dispose());
        
        return button;
    }
}
