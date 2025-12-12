package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

public class HistoryPanel extends JPanel {

    private final JButton homeButton;
    private JLabel placeholder;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        homeButton = new JButton("\u2190 Home");
        homeButton.setFocusPainted(false);
        homeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        homeButton.setBackground(new Color(245, 245, 245));
        homeButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        topBar.add(homeButton, BorderLayout.WEST);

        placeholder = new JLabel("History (Coming Soon)", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        placeholder.setForeground(new Color(120, 120, 120));

        add(topBar, BorderLayout.NORTH);
        add(placeholder, BorderLayout.CENTER);
    }
    
    /**
     * Updates the responsive layout based on scale factor.
     * 
     * @param scaleFactor The scaling factor from MainView
     */
    public void updateResponsiveLayout(double scaleFactor) {
        // Scale home button font
        if (homeButton != null) {
            int buttonFontSize = (int) (16 * scaleFactor);
            buttonFontSize = Math.max(14, Math.min(20, buttonFontSize)); // Clamp between 14-20
            homeButton.setFont(new Font("Segoe UI", Font.BOLD, buttonFontSize));
            
            // Scale button padding
            int padding = (int) (8 * scaleFactor);
            padding = Math.max(6, Math.min(12, padding)); // Clamp between 6-12
            int horizontalPadding = (int) (18 * scaleFactor);
            horizontalPadding = Math.max(14, Math.min(24, horizontalPadding)); // Clamp between 14-24
            homeButton.setBorder(BorderFactory.createEmptyBorder(padding, horizontalPadding, padding, horizontalPadding));
        }
        
        // Scale placeholder font
        if (placeholder != null) {
            int placeholderSize = (int) (22 * scaleFactor);
            placeholderSize = Math.max(18, Math.min(28, placeholderSize)); // Clamp between 18-28
            placeholder.setFont(new Font("Segoe UI", Font.PLAIN, placeholderSize));
        }
        
        revalidate();
        repaint();
    }

    public void setHomeAction(ActionListener actionListener) {
        for (ActionListener listener : homeButton.getActionListeners()) {
            homeButton.removeActionListener(listener);
        }
        if (actionListener != null) {
            homeButton.addActionListener(actionListener);
        }
    }
}

