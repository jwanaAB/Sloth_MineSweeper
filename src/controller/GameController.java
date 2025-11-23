package controller;

import model.Cell;
import model.Game;
import model.GameBoard;
import model.Question;
import view.GamePanel;

import javax.swing.*;

/**
 * Controller for managing game logic and interactions between the Game model
 * and GamePanel view. Handles cell reveals, flagging, turn switching, and
 * question cell interactions.
 * 
 * @author Team Sloth
 */
public class GameController {
    
    private final Game game;
    private final GamePanel gamePanel;
    
    /**
     * Constructs a new GameController.
     * 
     * @param game The Game model instance
     * @param gamePanel The GamePanel view instance
     * @param questionLogic The QuestionLogic instance for loading questions
     */
    public GameController(Game game, GamePanel gamePanel, QuestionLogic questionLogic) {
        this.game = game;
        this.gamePanel = gamePanel;
        
        // Initialize the game panel
        gamePanel.initializeGame(game, this);
    }
    
    /**
     * Handles a cell reveal action initiated by the user.
     * 
     * @param row The row index of the cell
     * @param col The column index of the cell
     * @param player The player number (1 or 2) attempting the action
     */
    public void handleCellReveal(int row, int col, int player) {
        // Validate that it's the current player's turn
        if (!game.canRevealCell(row, col, player)) {
            showMessage("It's not your turn!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if it's a question cell that needs special handling (already revealed)
        Cell cell = game.getBoard(player).getCell(row, col);
        if (cell != null && cell.isRevealed() && 
            cell.getType() == Cell.CellType.QUESTION && !cell.isQuestionOpened()) {
            // Question cell already revealed - offer to open question
            handleQuestionCellClick(row, col, player);
            return;
        }
        
        // Don't process if cell is already revealed (except question cells handled above)
        if (cell != null && cell.isRevealed()) {
            return;
        }
        
        // Reveal the cell (will unflag if needed)
        boolean mineHit = game.revealCell(row, col);
        
        // Update UI first to show the revealed cell
        gamePanel.updateUI();
        
        // Get the revealed cell to check its type
        Cell revealedCell = game.getBoard(player).getCell(row, col);
        
        if (mineHit) {
            // Mine hit - show message but don't decrease lives
            String playerName = game.getCurrentPlayerName();
            showMessage(
                playerName + " hit a mine!",
                "Mine Hit!",
                JOptionPane.WARNING_MESSAGE
            );
            // Switch turn after mine hit
            game.switchTurn();
        } else {
            // Check if current player won (BEFORE switching turn for non-mine cells)
            // Only check if ALL non-mine cells are revealed
            GameBoard currentBoard = game.getCurrentBoard();
            if (currentBoard.isGameWon()) {
                // Set game over in the model
                game.setGameOver(true);
                String winnerName = game.getCurrentPlayerName();
                showMessage(
                    winnerName + " revealed all cells on their board! Game Over!",
                    "Game Won!",
                    JOptionPane.INFORMATION_MESSAGE
                );
                handleGameOver(true);
                return; // Don't continue with turn switching
            }
            
            // Handle non-mine cells
            if (revealedCell != null) {
                // Check if it's a question or surprise cell - let user decide
                if (revealedCell.getType() == Cell.CellType.QUESTION) {
                    // Question cell - ask if user wants to open now
                    int option = JOptionPane.showConfirmDialog(
                        gamePanel,
                        "Question cell revealed! Do you want to open it now?",
                        "Question Cell Found",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    
                    if (option == JOptionPane.YES_OPTION) {
                        openQuestion(revealedCell, player);
                    }
                    // If NO, user can open it later by clicking on it
                    // Switch turn after question cell handling
                    game.switchTurn();
                } else if (revealedCell.getType() == Cell.CellType.SURPRISE) {
                    // Surprise cell - could add special handling here if needed
                    // Switch turn
                    game.switchTurn();
                } else {
                    // Regular cell (number, empty) - switch turn
                    game.switchTurn();
                }
            } else {
                // No cell revealed (shouldn't happen, but just in case)
                game.switchTurn();
            }
        }
        
        // Update UI again after turn switch
        gamePanel.updateUI();
    }
    
    /**
     * Handles a cell flag action initiated by the user.
     * 
     * @param row The row index of the cell
     * @param col The column index of the cell
     * @param player The player number (1 or 2) attempting the action
     */
    public void handleCellFlag(int row, int col, int player) {
        // Validate that it's the current player's turn
        if (!game.canFlagCell(row, col, player)) {
            showMessage("It's not your turn!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Toggle flag
        game.flagCell(row, col);
        
        // Update UI
        gamePanel.updateUI();
    }
    
    /**
     * Handles clicking on a revealed question cell.
     * Can be called from GamePanel when user clicks on an already-revealed question cell.
     * 
     * @param row The row index of the question cell
     * @param col The column index of the question cell
     * @param player The player number (1 or 2)
     */
    public void handleQuestionCellClick(int row, int col, int player) {
        Cell cell = game.getBoard(player).getCell(row, col);
        
        if (cell == null || cell.getType() != Cell.CellType.QUESTION) {
            return;
        }
        
        // Check if question already opened
        if (cell.isQuestionOpened()) {
            showMessage("This question has already been opened.", "Question Already Used", 
                       JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Ask if player wants to open the question now
        int option = JOptionPane.showConfirmDialog(
            gamePanel,
            "Do you want to open this question now?",
            "Question Cell",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            openQuestion(cell, player);
        }
    }
    
    /**
     * Opens a question for the player to answer.
     * 
     * @param cell The question cell
     * @param player The player number
     */
    private void openQuestion(Cell cell, int player) {
        Question question = cell.getQuestion();
        
        if (question == null) {
            showMessage("No question available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Mark question as opened
        cell.markQuestionOpened();
        
        // Show question dialog
        showQuestionDialog(question, player);
        
        // Update UI
        gamePanel.updateUI();
    }
    
    /**
     * Shows a dialog with a question for the player to answer.
     * 
     * @param question The Question object
     * @param player The player number
     */
    private void showQuestionDialog(Question question, int player) {
        // Create question dialog
        String[] options = {
            "A) " + question.getA(),
            "B) " + question.getB(),
            "C) " + question.getC(),
            "D) " + question.getD()
        };
        
        int answer = JOptionPane.showOptionDialog(
            gamePanel,
            question.getQuestionText() + "\n\n" +
            "A) " + question.getA() + "\n" +
            "B) " + question.getB() + "\n" +
            "C) " + question.getC() + "\n" +
            "D) " + question.getD(),
            "Question for " + (player == 1 ? 
                game.getPlayer1Name() : game.getPlayer2Name()),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        // Check answer (0=A, 1=B, 2=C, 3=D)
        String selectedAnswer = "";
        if (answer == 0) selectedAnswer = "A";
        else if (answer == 1) selectedAnswer = "B";
        else if (answer == 2) selectedAnswer = "C";
        else if (answer == 3) selectedAnswer = "D";
        else {
            // User closed dialog without answering
            return;
        }
        
        // Check if correct
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(selectedAnswer);
        
        if (isCorrect) {
            showMessage("Correct! Well done!", "Correct Answer", JOptionPane.INFORMATION_MESSAGE);
            // TODO: Add score points when scoring is implemented
        } else {
            showMessage("Incorrect. The correct answer was " + question.getCorrectAnswer() + ".", 
                       "Wrong Answer", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Handles game over scenarios (win or lose).
     * 
     * @param won true if the game was won, false if a mine was hit
     */
    private void handleGameOver(boolean won) {
        String message;
        String title;
        
        if (won) {
            int winner = game.getWinner();
            String winnerName = winner == 1 ? game.getPlayer1Name() : game.getPlayer2Name();
            message = "Congratulations! " + winnerName + " wins!";
            title = "Game Won";
        } else {
            int loser = game.getCurrentPlayer();
            String loserName = loser == 1 ? game.getPlayer1Name() : game.getPlayer2Name();
            int winner = game.getWinner();
            String winnerName = winner == 1 ? game.getPlayer1Name() : game.getPlayer2Name();
            message = loserName + " hit a mine! " + winnerName + " wins!";
            title = "Game Over";
        }
        
        JOptionPane.showMessageDialog(
            gamePanel,
            message,
            title,
            won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Shows a message dialog.
     * 
     * @param message The message to display
     * @param title The dialog title
     * @param messageType The message type (from JOptionPane constants)
     */
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(gamePanel, message, title, messageType);
    }
    
    /**
     * Gets the Game instance.
     * 
     * @return The Game instance
     */
    public Game getGame() {
        return game;
    }
}

