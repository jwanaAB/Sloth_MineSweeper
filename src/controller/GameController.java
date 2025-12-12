package controller;

import model.Cell;
import model.EmptyCell;
import model.MineCell;
import model.NumberCell;
import model.QuestionCell;
import model.SurpriseCell;
import model.Game;
import model.GameBoard;
import model.Question;
import model.SysData;
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
    private final ScoringService scoringService;
    @SuppressWarnings("unused")
    private final Runnable onReturnToMainMenu;
    private boolean gameOver = false;
    
    /**
     * Constructs a new GameController.
     * 
     * @param game The Game model instance
     * @param gamePanel The GamePanel view instance
     * @param questionLogic The QuestionLogic instance for loading questions
     * @param onReturnToMainMenu Callback to return to main menu when game ends
     */
    public GameController(Game game, GamePanel gamePanel, QuestionLogic questionLogic, Runnable onReturnToMainMenu) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.scoringService = new ScoringService(SysData.getInstance());
        this.onReturnToMainMenu = onReturnToMainMenu;
        
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
        // Don't allow actions if game is over
        if (gameOver) {
            return;
        }
        
        // Validate that it's the current player's turn
        if (!game.canRevealCell(row, col, player)) {
            showMessage("It's not your turn!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if it's a question cell that needs special handling (already revealed)
        Cell cell = game.getBoard(player).getCell(row, col);
        if (cell != null && cell.isRevealed() && 
            cell instanceof QuestionCell && !((QuestionCell) cell).isQuestionOpened()) {
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
        String playerName = game.getCurrentPlayerName();
        int gameDifficulty = convertDifficultyToInt(game.getDifficulty());
        
        if (mineHit) {
            // Mine hit - score the mine hit
            scoringService.scoreMineHit(game, playerName);
            showMessage(
                playerName + " hit a mine!",
                "Mine Hit!",
                JOptionPane.WARNING_MESSAGE
            );
            
            // Check if game is over due to lives running out
            if (game.getSharedLives() <= 0) {
                handleGameOver(false);
                return;
            }
            
            // Switch turn after mine hit
            game.switchTurn();
        } else {
            // Score the revealed cell (only for the initially clicked cell, not cascade reveals)
            if (revealedCell != null) {
                scoreCellReveal(revealedCell, playerName, gameDifficulty);
            }
            
            // Check if current player won (BEFORE switching turn for non-mine cells)
            // Only check if ALL non-mine cells are revealed
            GameBoard currentBoard = game.getCurrentBoard();
            if (currentBoard.isGameWon()) {
                // Set game over in the model
                game.setGameOver(true);
                showMessage(
                    "Congratulations! Both players won!\n" +
                    game.getPlayer1Name() + " and " + game.getPlayer2Name() + 
                    " successfully revealed all cells on their boards!",
                    "Game Won!",
                    JOptionPane.INFORMATION_MESSAGE
                );
                handleGameOver(true);
                return; // Don't continue with turn switching
            }
            
            // Handle non-mine cells
            if (revealedCell != null) {
                // Check if it's a question or surprise cell
                if (revealedCell instanceof QuestionCell) {
                    // Question cell revealed - player must wait until next turn to activate
                    showMessage(
                        "Question cell revealed! You can activate it on your next turn.",
                        "Question Cell Found",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    // Switch turn - player cannot activate in same turn
                    game.switchTurn();
                } else if (revealedCell instanceof SurpriseCell) {
                    // Surprise cell revealed - player must wait until next turn to activate
                    showMessage(
                        "Surprise cell revealed! You can activate it on your next turn.",
                        "Surprise Cell Found",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    // Switch turn - player cannot activate in same turn
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
        // Don't allow actions if game is over
        if (gameOver) {
            return;
        }
        
        // Check if it's the current player's turn
        if (player != game.getCurrentPlayer()) {
            showMessage("It's not your turn!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the cell to check if it can be flagged
        Cell cell = game.getBoard(player).getCell(row, col);
        if (cell == null) {
            return;
        }
        
        // Check if the cell is revealed (cannot flag revealed cells)
        if (cell.isRevealed()) {
            showMessage("Cannot flag revealed cells!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the flag state before toggling
        boolean wasFlagged = cell.isFlagged();
        
        // Toggle flag
        game.flagCell(row, col);
        
        // Score the flag action (only if flagging, not unflagging, and hasn't contributed to score yet)
        if (!wasFlagged && cell.isFlagged() && !cell.hasFlagScoreContributed()) {
            scoreCellFlag(cell, row, col, player);
            // Mark that this cell has now contributed to score via flagging
            cell.setFlagScoreContributed(true);
        }
        
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
        // Don't allow actions if game is over
        if (gameOver) {
            return;
        }
        
        // Validate that it's the current player's turn
        if (player != game.getCurrentPlayer()) {
            showMessage("It's not your turn!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Cell cell = game.getBoard(player).getCell(row, col);
        
        if (cell == null || !(cell instanceof QuestionCell)) {
            return;
        }
        
        QuestionCell questionCell = (QuestionCell) cell;
        
        // Check if question already opened
        if (questionCell.isQuestionOpened()) {
            showMessage("This question has already been opened.", "Question Already Used", 
                       JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Directly open the question without asking for confirmation
        openQuestion(questionCell, player);
    }
    
    /**
     * Handles clicking on a revealed surprise cell.
     * Can be called from GamePanel when user clicks on an already-revealed surprise cell.
     * 
     * @param row The row index of the surprise cell
     * @param col The column index of the surprise cell
     * @param player The player number (1 or 2)
     */
    public void handleSurpriseCellClick(int row, int col, int player) {
        // Don't allow actions if game is over
        if (gameOver) {
            return;
        }
        
        // Validate that it's the current player's turn
        if (player != game.getCurrentPlayer()) {
            showMessage("It's not your turn!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Cell cell = game.getBoard(player).getCell(row, col);
        
        if (cell == null || !(cell instanceof SurpriseCell)) {
            return;
        }
        
        SurpriseCell surpriseCell = (SurpriseCell) cell;
        
        // Check if surprise already activated
        if (surpriseCell.isSurpriseActivated()) {
            showMessage("This surprise cell has already been activated.", "Surprise Already Used", 
                       JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Directly activate the surprise cell without asking for confirmation
        activateSurpriseCell(surpriseCell, player);
    }
    
    /**
     * Opens a question for the player to answer.
     * 
     * @param questionCell The question cell
     * @param player The player number
     */
    private void openQuestion(QuestionCell questionCell, int player) {
        Question question = questionCell.getQuestion();
        
        if (question == null) {
            showMessage("No question available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Mark question as opened
        questionCell.markQuestionOpened();
        
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
        
        // Score the question activation
        String playerName = player == 1 ? game.getPlayer1Name() : game.getPlayer2Name();
        int gameDifficulty = convertDifficultyToInt(game.getDifficulty());
        int questionType = question.getDifficulty(); // Question difficulty maps to question type (1-4)
        
        scoringService.scoreQuestionCellActivated(game, playerName, gameDifficulty, questionType, isCorrect);
        
        if (isCorrect) {
            showMessage("Correct! Well done!", "Correct Answer", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showMessage("Incorrect. The correct answer was " + question.getCorrectAnswer() + ".", 
                       "Wrong Answer", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Switch turn after answering question
        game.switchTurn();
        
        // Update UI
        gamePanel.updateUI();
    }
    
    /**
     * Handles game over scenarios (win or lose).
     * 
     * @param won true if the game was won, false if lives ran out
     */
    private void handleGameOver(boolean won) {
        // Mark game as over to prevent further actions
        gameOver = true;
        
        // Disable all game interactions
        gamePanel.setGameOver(true);
        
        // Convert remaining lives to points
        int pointsAdded = scoringService.convertRemainingLivesToPoints(game);
        
        String message;
        String title;
        
        if (won) {
            message = "Congratulations! Both " + game.getPlayer1Name() + " and " + 
                     game.getPlayer2Name() + " won together!\n" +
                     "Final Score: " + game.getCombinedScore() + " points";
            title = "Game Won";
        } else {
            message = "Game Over! Both " + game.getPlayer1Name() + " and " + 
                     game.getPlayer2Name() + " lost.\n" +
                     "Shared lives ran out.\n" +
                     "Final Score: " + game.getCombinedScore() + " points";
            if (pointsAdded > 0) {
                message += "\n" + pointsAdded + " points added from remaining lives.";
            }
            title = "Game Over";
        }
        
        // Reveal all cells for both players
        game.revealAllCells();
        
        // Show game over message
        JOptionPane.showMessageDialog(
            gamePanel,
            message,
            title,
            won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
        
        // Update UI to show final state with all cells revealed
        gamePanel.updateUI();
        
        // Stay on game board view - do not return to main menu
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
    
    /**
     * Scores a cell reveal based on the cell type.
     * 
     * @param cell The cell that was revealed
     * @param playerName The name of the player who revealed it
     * @param gameDifficulty The game difficulty (1=Easy, 2=Medium, 3=Hard)
     */
    private void scoreCellReveal(Cell cell, String playerName, int gameDifficulty) {
        if (cell == null) return;
        
        if (cell instanceof MineCell) {
            // Mine hit is handled separately in handleCellReveal
        } else if (cell instanceof NumberCell) {
            scoringService.scoreNumberedCellRevealedCorrectly(game, playerName, ((NumberCell) cell).getAdjacentMines());
        } else if (cell instanceof EmptyCell) {
            scoringService.scoreEmptyCellRevealedCorrectly(game, playerName);
        } else if (cell instanceof QuestionCell) {
            scoringService.scoreQuestionCellRevealedCorrectly(game, playerName);
        } else if (cell instanceof SurpriseCell) {
            scoringService.scoreSurpriseCellRevealedCorrectly(game, playerName);
        }
    }
    
    /**
     * Scores a cell flag based on whether it's correct or incorrect.
     * 
     * @param cell The cell that was flagged
     * @param row The row index
     * @param col The column index
     * @param player The player number
     */
    private void scoreCellFlag(Cell cell, int row, int col, int player) {
        if (cell == null) return;
        
        String playerName = player == 1 ? game.getPlayer1Name() : game.getPlayer2Name();
        
        if (cell instanceof MineCell) {
            // Correct flag on a mine
            scoringService.scoreMineFlaggedCorrectly(game, playerName);
        } else {
            // Incorrect flag on non-mine cell
            if (cell instanceof NumberCell) {
                scoringService.scoreNumberedCellFlaggedIncorrectly(game, playerName, ((NumberCell) cell).getAdjacentMines());
            } else if (cell instanceof EmptyCell) {
                scoringService.scoreEmptyCellFlaggedIncorrectly(game, playerName);
            } else if (cell instanceof QuestionCell) {
                scoringService.scoreQuestionCellFlaggedIncorrectly(game, playerName);
            } else if (cell instanceof SurpriseCell) {
                scoringService.scoreSurpriseCellFlaggedIncorrectly(game, playerName);
            }
        }
    }
    
    /**
     * Activates a surprise cell and applies its effects.
     * 
     * @param surpriseCell The surprise cell to activate
     * @param player The player number
     */
    private void activateSurpriseCell(SurpriseCell surpriseCell, int player) {
        if (surpriseCell == null) {
            return;
        }
        
        // Mark surprise as activated
        surpriseCell.markSurpriseActivated();
        
        String playerName = player == 1 ? game.getPlayer1Name() : game.getPlayer2Name();
        int gameDifficulty = convertDifficultyToInt(game.getDifficulty());
        
        // Score the surprise cell activation and get the surprise details
        String surpriseMessage = scoringService.scoreSurpriseCellActivated(game, playerName, gameDifficulty);
        
        // Show result message with surprise details
        showMessage(
            surpriseMessage,
            "Surprise!",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        // Check if game is over due to lives running out (bad surprise can decrease lives)
        if (game.getSharedLives() <= 0) {
            handleGameOver(false);
            return;
        }
        
        // Switch turn after activating surprise
        game.switchTurn();
        
        // Update UI
        gamePanel.updateUI();
    }
    
    /**
     * Converts Game.Difficulty enum to integer (1=Easy, 2=Medium, 3=Hard).
     * 
     * @param difficulty The Game.Difficulty enum value
     * @return Integer representation (1, 2, or 3)
     */
    private int convertDifficultyToInt(Game.Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return 1;
            case MEDIUM:
                return 2;
            case HARD:
                return 3;
            default:
                return 1;
        }
    }
}

