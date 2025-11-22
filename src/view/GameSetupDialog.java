package view;

import javax.swing.*;
import java.awt.*;

public class GameSetupDialog extends JDialog {
    private final JTextField player1TextField;
    private final JTextField player2TextField;
    private final JComboBox<String> difficultyComboBox;
    private final JButton startButton;
    private final JButton cancelButton;
    private boolean confirmed = false;

    public GameSetupDialog(JFrame parent) {
        super(parent, "Game Setup", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main content panel
        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("Enter Player Names & Select Difficulty");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(40, 40, 40));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Player 1 name section
        JLabel player1Label = new JLabel("Player 1 Name:");
        player1Label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        player1Label.setForeground(new Color(60, 60, 60));
        player1Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        player1Label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        player1TextField = new JTextField(20);
        player1TextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        player1TextField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        player1TextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        player1TextField.setAlignmentX(Component.LEFT_ALIGNMENT);
        player1TextField.setBorder(BorderFactory.createCompoundBorder(
            player1TextField.getBorder(),
            BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));

        // Player 2 name section
        JLabel player2Label = new JLabel("Player 2 Name:");
        player2Label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        player2Label.setForeground(new Color(60, 60, 60));
        player2Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        player2Label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        player2TextField = new JTextField(20);
        player2TextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        player2TextField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        player2TextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        player2TextField.setAlignmentX(Component.LEFT_ALIGNMENT);
        player2TextField.setBorder(BorderFactory.createCompoundBorder(
            player2TextField.getBorder(),
            BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));

        // Difficulty section
        JLabel difficultyLabel = new JLabel("Difficulty Level:");
        difficultyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        difficultyLabel.setForeground(new Color(60, 60, 60));
        difficultyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        difficultyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        String[] difficulties = {"Easy (1)", "Medium (2)", "Hard (3)"};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        difficultyComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        difficultyComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        difficultyComboBox.setBorder(BorderFactory.createCompoundBorder(
            difficultyComboBox.getBorder(),
            BorderFactory.createEmptyBorder(0, 0, 20, 0)
        ));

        // Button panel
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.RIGHT_ALIGNMENT);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.setBackground(new Color(245, 245, 245));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        startButton.setFocusPainted(false);
        startButton.setBackground(new Color(91, 161, 255));
        startButton.setForeground(Color.WHITE);
        startButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        startButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        // Enter key support
        player1TextField.addActionListener(e -> player2TextField.requestFocus());
        player2TextField.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        buttonRow.add(cancelButton);
        buttonRow.add(startButton);

        // Assemble content
        content.add(title);
        content.add(player1Label);
        content.add(player1TextField);
        content.add(player2Label);
        content.add(player2TextField);
        content.add(difficultyLabel);
        content.add(difficultyComboBox);
        content.add(buttonRow);

        setContentPane(content);
        pack();
        setLocationRelativeTo(parent);
    }

    private boolean validateInput() {
        String player1Name = player1TextField.getText().trim();
        String player2Name = player2TextField.getText().trim();
        
        // Validate Player 1 name
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
        
        // Validate Player 2 name
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
        
        // Check if names are different
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
        // Returns 1-3 based on selection (Easy=1, Medium=2, Hard=3)
        return difficultyComboBox.getSelectedIndex() + 1;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
