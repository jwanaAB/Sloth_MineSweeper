package controller;

import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import model.SysData;
import view.MainView;

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
        return e -> view.showPlaceholderScreen("Start Game");
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

