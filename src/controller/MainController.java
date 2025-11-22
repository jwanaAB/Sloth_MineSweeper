package controller;

import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import model.SysData;
import view.MainView;
import view.GameSetupDialog;

public class MainController {

    private static final String QUESTION_MANAGER_PIN = "2580";

    private final SysData model;
    private final MainView view;
    private final GameController gameController;

    public MainController(SysData model, MainView view) {
        this.model = model;
        this.view = view;
        this.gameController = new GameController(model);
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
            GameSetupDialog setupDialog = view.showGameSetupDialog();
            
            // Check if user clicked start game button and confirmed the input
            if (setupDialog.isConfirmed()) {
                String player1Name = setupDialog.getPlayer1Name();
                String player2Name = setupDialog.getPlayer2Name();
                int difficulty = setupDialog.getDifficulty();
                
                // Pass setup data (both players and difficulty) to game controller for initialization
                gameController.initializeGame(player1Name, player2Name, difficulty);
                
                // TODO: Show the actual game board view here
                // For now, show a confirmation message
                JOptionPane.showMessageDialog(
                    view,
                    String.format("Game initialized!\nPlayer 1: %s\nPlayer 2: %s\nDifficulty: %d", 
                                 player1Name, player2Name, difficulty),
                    "Game Setup Complete",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        };
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

