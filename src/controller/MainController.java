package controller;

import model.Game;
import model.SysData;
import view.GameSetupDialog;
import view.MainView;

import javax.swing.*;
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
                createExitListener());
        view.setVisible(true);
    }

    private ActionListener createStartGameListener() {
        return e -> {
            // Show game setup dialog
            GameSetupDialog setupDialog = new GameSetupDialog(view);
            setupDialog.setVisible(true);
            if (setupDialog.isConfirmed()) {
                String player1Name = setupDialog.getPlayer1Name();
                String player2Name = setupDialog.getPlayer2Name();
                Game.Difficulty difficulty = mapDifficulty(setupDialog.getDifficulty());

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

    private Game.Difficulty mapDifficulty(int difficultySelection) {
        return switch (difficultySelection) {
            case 1 -> Game.Difficulty.EASY;
            case 2 -> Game.Difficulty.MEDIUM;
            case 3 -> Game.Difficulty.HARD;
            default -> Game.Difficulty.EASY;
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
                        JOptionPane.ERROR_MESSAGE);
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
