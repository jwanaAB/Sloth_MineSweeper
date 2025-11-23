package controller;



import model.Game;
import model.SysData;
import view.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainController {

    private static final String QUESTION_MANAGER_PIN = "2580";

    @SuppressWarnings("unused")
    private final SysData model;
    private final MainView view;

    public MainController(SysData model, MainView view) {
        this.model = model;
        this.view = view;
    }

    public void init() {
        view.setButtonListeners(
                createStartGameListener(),
                createHistoryListener(),
                createQuestionManagerListener(),
                createExitListener()
        );
        view.setVisible(true);
    }

    private ActionListener createStartGameListener() {
        return e -> {
            // Show game setup dialog
            GameSetupDialog setupDialog = new GameSetupDialog(view);
            if (setupDialog.isConfirmed()) {
                String player1Name = setupDialog.getPlayer1Name();
                String player2Name = setupDialog.getPlayer2Name();
                Game.Difficulty difficulty = setupDialog.getDifficulty();
                
                // Load questions
                QuestionLogic questionLogic = new QuestionLogic();
                questionLogic.loadQuestionsFromCSV("resources/Questions.csv");
                
                // Create game
                Game game = new Game(player1Name, player2Name, difficulty, questionLogic);
                
                // Create game controller 
                new GameController(game, view.getGamePanel(), questionLogic);
                
                // Show game panel
                view.showGamePanel(null);
            }
        };
    }
    
    /**
     * Dialog for game setup (player names and difficulty selection).
     */
    private static class GameSetupDialog extends JDialog {
        private String player1Name;
        private String player2Name;
        private Game.Difficulty difficulty;
        private boolean confirmed = false;
        
        private final JTextField player1Field;
        private final JTextField player2Field;
        private final JComboBox<String> difficultyCombo;
        
        public GameSetupDialog(JFrame parent) {
            super(parent, "Game Setup", true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            
            // Create components
            player1Field = new JTextField(15);
            player2Field = new JTextField(15);
            String[] difficulties = {"Easy (9x9)", "Medium (13x13)", "Hard (16x16)"};
            difficultyCombo = new JComboBox<>(difficulties);
            difficultyCombo.setSelectedIndex(0);
            
            // Build dialog
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(Color.WHITE);
            
            // Form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Player 1
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Player 1 Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(player1Field, gbc);
            
            // Player 2
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("Player 2 Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(player2Field, gbc);
            
            // Difficulty
            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("Difficulty:"), gbc);
            gbc.gridx = 1;
            formPanel.add(difficultyCombo, gbc);
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.setBackground(Color.WHITE);
            JButton startButton = new JButton("Start Game");
            JButton cancelButton = new JButton("Cancel");
            
            startButton.addActionListener(e -> {
                player1Name = player1Field.getText().trim();
                player2Name = player2Field.getText().trim();
                
                if (player1Name.isEmpty() || player2Name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter names for both players.", 
                        "Invalid Input", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int selectedIndex = difficultyCombo.getSelectedIndex();
                switch (selectedIndex) {
                    case 0:
                        difficulty = Game.Difficulty.EASY;
                        break;
                    case 1:
                        difficulty = Game.Difficulty.MEDIUM;
                        break;
                    case 2:
                        difficulty = Game.Difficulty.HARD;
                        break;
                }
                
                confirmed = true;
                dispose();
            });
            
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(startButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            setContentPane(mainPanel);
            pack();
            setLocationRelativeTo(parent);
            setVisible(true);
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
        
        public String getPlayer1Name() {
            return player1Name;
        }
        
        public String getPlayer2Name() {
            return player2Name;
        }
        
        public Game.Difficulty getDifficulty() {
            return difficulty;
        }
    }

    private ActionListener createHistoryListener() {
        return e -> view.showHistoryPanel(null);
    }

    private ActionListener createQuestionManagerListener() {
        return e -> {
            String pin = view.promptForPinCode();
            if (pin == null) {
                return;
            }

            if (QUESTION_MANAGER_PIN.equals(pin.trim())) {
                view.showQuestionManagerPanel(null);
            } else {
                JOptionPane.showMessageDialog(
                        view,
                        "Incorrect PIN. Please try again.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        };
    }

    private ActionListener createExitListener() {
        return e -> {
            if (view.confirmExit()) {
                view.dispose();
                System.exit(0);
            }
        };
    }
}

