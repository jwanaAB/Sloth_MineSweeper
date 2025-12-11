package view;

import javax.swing.*;
import java.awt.*;

/**
 * Custom error dialog that matches the game's modern UI design.
 */
public class ErrorDialog extends JDialog {
    
    public ErrorDialog(JFrame parent, String message) {
        super(parent, "Invalid Input", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Create gradient background panel
        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        background.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Main content panel with rounded corners
        RoundedPanel contentPanel = new RoundedPanel(25);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));
        contentPanel.setMaximumSize(new Dimension(480, 250));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Icon panel - centered
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconPanel.setOpaque(false);
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Warning icon - larger and centered
        JLabel iconLabel = new JLabel("⚠");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        iconLabel.setForeground(new Color(255, 193, 7));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        iconPanel.add(iconLabel);
        
        // Message label - centered with better font
        JLabel messageLabel = new JLabel("<html><div style='text-align:center; width:320px;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
        messageLabel.setForeground(new Color(50, 50, 50));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Message panel - centered
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);
        messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        messagePanel.add(messageLabel);
        
        // OK button
        JButton okButton = createStyledButton("OK");
        okButton.addActionListener(e -> dispose());
        
        // Button panel - centered
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        buttonPanel.add(okButton);
        
        // Add all components with symmetric spacing
        contentPanel.add(iconPanel);
        contentPanel.add(messagePanel);
        contentPanel.add(buttonPanel);
        
        // Center the content panel
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(contentPanel);
        
        background.add(wrapper, BorderLayout.CENTER);
        setContentPane(background);
        
        pack();
        setLocationRelativeTo(parent);
        
        // Set focus to OK button for Enter key
        SwingUtilities.invokeLater(() -> {
            okButton.requestFocus();
            getRootPane().setDefaultButton(okButton);
        });
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0,
                    new Color(91, 161, 255),
                    getWidth(), 0,
                    new Color(196, 107, 255)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 45));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 200), 2, true),
                    BorderFactory.createEmptyBorder(10, 38, 10, 38)
                ));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
            }
        });
        
        return button;
    }
    
    /**
     * Shows a custom error dialog with the given message.
     * 
     * @param parent The parent frame
     * @param message The error message to display
     */
    public static void showErrorDialog(JFrame parent, String message) {
        new ErrorDialog(parent, message).setVisible(true);
    }
    
    /**
     * Shows a custom error dialog with the given message (for JDialog parent).
     * 
     * @param parent The parent dialog
     * @param message The error message to display
     */
    public static void showErrorDialog(JDialog parent, String message) {
        Window window = SwingUtilities.getWindowAncestor(parent);
        JFrame frame = null;
        if (window instanceof JFrame) {
            frame = (JFrame) window;
        } else if (window instanceof JDialog) {
            // If parent is a dialog, use it as the owner
            ErrorDialog dialog = new ErrorDialog(null, message);
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
            return;
        }
        if (frame != null) {
            new ErrorDialog(frame, message).setVisible(true);
        } else {
            // Fallback if no frame ancestor found
            ErrorDialog dialog = new ErrorDialog(null, message);
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        }
    }
    
    /**
     * Shows a custom error dialog with the given message (for JPanel parent).
     * 
     * @param parent The parent panel
     * @param message The error message to display
     */
    public static void showErrorDialog(JPanel parent, String message) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(parent);
        if (frame != null) {
            new ErrorDialog(frame, message).setVisible(true);
        } else {
            // Fallback if no frame ancestor found
            new ErrorDialog(null, message).setVisible(true);
        }
    }
    
    // Gradient background panel
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(
                0, 0, new Color(225, 232, 255),
                getWidth(), getHeight(), new Color(245, 213, 255)
            );
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
    
    // Rounded panel with shadow
    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        
        RoundedPanel(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            setOpaque(false);
            setBackground(Color.WHITE);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, cornerRadius + 4, cornerRadius + 4);
            
            // Main panel
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius);
            
            // Border
            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius);
            
            g2.dispose();
        }
    }
}

